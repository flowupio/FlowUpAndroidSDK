/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.reporter.model;

import com.google.gson.annotations.SerializedName;

public class StatisticalValue {

  @SerializedName("count") private final long count;
  @SerializedName("min") private final double min;
  @SerializedName("max") private final double max;
  @SerializedName("mean") private final double mean;
  @SerializedName("standardDev") private final double standardDev;
  @SerializedName("median") private final double median;
  @SerializedName("p1") private final double p1;
  @SerializedName("p2") private final double p2;
  @SerializedName("p5") private final double p5;
  @SerializedName("p10") private final double p10;
  @SerializedName("p90") private final double p90;
  @SerializedName("p95") private final double p95;
  @SerializedName("p98") private final double p98;
  @SerializedName("p99") private final double p99;

  public StatisticalValue(long count, double min, double max, double mean, double standardDev,
      double median, double p1, double p2, double p5, double p10, double p90, double p95,
      double p98, double p99) {
    this.count = count;
    this.min = min;
    this.max = max;
    this.mean = mean;
    this.standardDev = standardDev;
    this.median = median;
    this.p1 = p1;
    this.p2 = p2;
    this.p5 = p5;
    this.p10 = p10;
    this.p90 = p90;
    this.p95 = p95;
    this.p98 = p98;
    this.p99 = p99;
  }

  public long getCount() {
    return count;
  }

  public double getMin() {
    return min;
  }

  public double getMax() {
    return max;
  }

  public double getMean() {
    return mean;
  }

  public double getStandardDev() {
    return standardDev;
  }

  public double getMedian() {
    return median;
  }

  public double getP1() {
    return p1;
  }

  public double getP2() {
    return p2;
  }

  public double getP5() {
    return p5;
  }

  public double getP10() {
    return p10;
  }

  public double getP90() {
    return p90;
  }

  public double getP95() {
    return p95;
  }

  public double getP98() {
    return p98;
  }

  public double getP99() {
    return p99;
  }

  @Override public String toString() {
    return "StatisticalValue{"
        + "mean="
        + mean
        + ", count="
        + count
        + ", max="
        + max
        + ", min="
        + min
        + '}';
  }
}
