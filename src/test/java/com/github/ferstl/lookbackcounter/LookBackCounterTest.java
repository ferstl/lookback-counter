package com.github.ferstl.lookbackcounter;

import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LookBackCounterTest {

  private static final int LOOK_BACK = 60;
  private static final int INITIAL_VALUE = 45;

  private AtomicLong initialTimestamp;
  private LookBackCounter lookBackCounter;

  @BeforeEach
  void beforeEach() {
    this.initialTimestamp = new AtomicLong(INITIAL_VALUE);
    this.lookBackCounter = new LookBackCounter(this.initialTimestamp::get, LOOK_BACK);
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

    this.initialTimestamp.incrementAndGet();
    this.lookBackCounter.add(1);

    this.initialTimestamp.incrementAndGet();
    this.lookBackCounter.add(1);

    // assert
    assertEquals(3, this.lookBackCounter.get());
  }

  @Test
  void differentBucketsWithRollover() {
    // act
    this.lookBackCounter.add(1);

    this.initialTimestamp.addAndGet(LOOK_BACK - INITIAL_VALUE - 1);
    this.lookBackCounter.add(1);

    this.initialTimestamp.incrementAndGet();
    this.lookBackCounter.add(1);

    this.initialTimestamp.incrementAndGet();
    this.lookBackCounter.add(1);

    // assert
    assertEquals(4, this.lookBackCounter.get());
  }

  @Test
  void exceedLookBack() {
    // arrange
    int elapse = LOOK_BACK / 2;

    for (int i = 0; i < LOOK_BACK; i++) {
      this.lookBackCounter.add(1);
      this.initialTimestamp.incrementAndGet();
    }

    this.initialTimestamp.addAndGet(elapse);

    // act
    this.lookBackCounter.add(1);

    // assert
    assertEquals(elapse + 1, this.lookBackCounter.get());
  }
}
