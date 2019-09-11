package com.github.ferstl.lookbackcounter;

import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LookBackCounterTest {

  private static final int LOOK_BACK = 60;
  private static final int INITIAL_VALUE = 45;

  private AtomicLong currentTimestamp;
  private LookBackCounter lookBackCounter;

  @BeforeEach
  void beforeEach() {
    this.currentTimestamp = new AtomicLong(INITIAL_VALUE);
    this.lookBackCounter = new LookBackCounter(this.currentTimestamp::get, LOOK_BACK);
  }

  @Test
  void sameBucket() {
    // act
    this.lookBackCounter.add(1);
    this.lookBackCounter.add(1);
    this.lookBackCounter.add(1);

    // assert
    assertEquals(3, this.lookBackCounter.get());
  }

  @Test
  void differentBucketsWithoutRollover() {
    // act
    this.lookBackCounter.add(1);

    this.currentTimestamp.incrementAndGet();
    this.lookBackCounter.add(1);

    this.currentTimestamp.incrementAndGet();
    this.lookBackCounter.add(1);

    // assert
    assertEquals(3, this.lookBackCounter.get());
  }

  @Test
  void differentBucketsWithRollover() {
    // act
    this.lookBackCounter.add(1);

    this.currentTimestamp.addAndGet(LOOK_BACK - INITIAL_VALUE - 1);
    this.lookBackCounter.add(1);

    this.currentTimestamp.incrementAndGet();
    this.lookBackCounter.add(1);

    this.currentTimestamp.incrementAndGet();
    this.lookBackCounter.add(1);

    // assert
    assertEquals(4, this.lookBackCounter.get());
  }

  @Test
  void exceedLookBackWithOverlap() {
    // arrange
    int elapse = LOOK_BACK / 2;
    fillAllBuckets();

    // act
    this.currentTimestamp.addAndGet(elapse);
    this.lookBackCounter.add(1);

    // assert
    assertEquals(elapse + 1, this.lookBackCounter.get());
  }

  @Test
  void exceedLookBackExactly() {
    // arrange
    fillAllBuckets();

    // act
    this.currentTimestamp.addAndGet(LOOK_BACK);
    this.lookBackCounter.add(1);

    // assert
    assertEquals(1, this.lookBackCounter.get());
  }

  @Test
  void exceedLookBackMinusOne() {
    // arrange
    fillAllBuckets();

    // act
    this.currentTimestamp.addAndGet(LOOK_BACK - 1);
    this.lookBackCounter.add(1);

    // assert
    assertEquals(2, this.lookBackCounter.get());
  }

  @Test
  void exceedLookBackPlusOne() {
    // arrange
    fillAllBuckets();

    // act
    this.currentTimestamp.addAndGet(LOOK_BACK + 1);

    // assert
    assertEquals(0, this.lookBackCounter.get());
  }

  // Fill all buckets and leave the current timestamp at the last bucket
  private void fillAllBuckets() {
    for (int i = 0; i < LOOK_BACK; i++) {
      this.lookBackCounter.add(1);

      if (i < LOOK_BACK - 1) {
        this.currentTimestamp.incrementAndGet();
      }
    }
  }
}
