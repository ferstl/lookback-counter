package com.github.ferstl.lookbackcounter;

import java.util.function.LongSupplier;
import static java.lang.Math.min;

public class LookBackCounter {

  private final LongSupplier timestampSupplier;
  private final int lookBack;

  private final long[] buckets;
  private long lastTimestamp;

  public LookBackCounter(LongSupplier timestampSupplier, int lookBack) {
    this.timestampSupplier = timestampSupplier;
    this.lookBack = lookBack;
    this.buckets = new long[lookBack];
    this.lastTimestamp = now();
  }

  public void add(int nr) {
    long currentTimestamp = now();
    updateBuckets(currentTimestamp);
    int currentPosition = (int) (currentTimestamp % this.lookBack);
    this.buckets[currentPosition] += nr;
    this.lastTimestamp = currentTimestamp;
  }

  public long get() {
    updateBuckets(now());

    long sum = 0;
    for (long bucket : this.buckets) {
      sum += bucket;
    }

    return sum;
  }

  private void updateBuckets(long currentTimestamp) {
    int lastPosition = (int) (this.lastTimestamp % this.lookBack);
    long elapsed = min(currentTimestamp - this.lastTimestamp, this.lookBack);
    // clear the buckets between the last position (exclusively) and now
    for (int i = 1; i <= elapsed; i++) {
      this.buckets[(lastPosition + i) % this.lookBack] = 0;
    }
  }


  private long now() {
    return this.timestampSupplier.getAsLong();
  }
}
