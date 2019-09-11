package com.github.ferstl.lookbackcounter;

import java.util.Arrays;
import java.util.function.LongSupplier;
import static java.lang.Math.min;

public class LookBackCounter {

  private final LongSupplier timestampSupplier;
  private final int lookBack;

  private final int[] buckets;
  private long lastTimestamp;

  public LookBackCounter(LongSupplier timestampSupplier, int lookBack) {
    this.timestampSupplier = timestampSupplier;
    this.lookBack = lookBack;
    this.buckets = new int[lookBack];
    this.lastTimestamp = now();
  }

  public void add(int nr) {
    long currentTimestamp = updateBuckets();
    int currentPosition = (int) (currentTimestamp % this.lookBack);
    this.buckets[currentPosition] += nr;
    this.lastTimestamp = currentTimestamp;
  }

  public int get() {
    updateBuckets();
    return Arrays.stream(this.buckets).sum();
  }

  private long updateBuckets() {
    long currentTimestamp = now();
    int lastPosition = (int) (this.lastTimestamp % this.lookBack);
    long elapsed = min(currentTimestamp - this.lastTimestamp, this.lookBack);
    // clear the buckets between the last position (exclusively) and now
    for (int i = 1; i <= elapsed; i++) {
      this.buckets[(lastPosition + i) % this.lookBack] = 0;
    }

    return currentTimestamp;
  }


  private long now() {
    return this.timestampSupplier.getAsLong();
  }
}
