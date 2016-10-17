/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.reporter.model;

public class StatisticalValue {

  private final long count;
  private final double min;
  private final double max;
  private final double mean;
  private final double standardDev;
  private final double median;
  private final double p1;
  private final double p2;
  private final double p5;
  private final double p10;
  private final double p80;
  private final double p95;
  private final double p98;
  private final double p99;

  public StatisticalValue(long count, double min, double max, double mean, double standardDev,
      double median, double p1, double p2, double p5, double p10, double p80, double p95,
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
    this.p80 = p80;
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

  public double getP80() {
    return p80;
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
