/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter.model;

public class StatisticalValue {

  private final long count;
  private final double min;
  private final double max;
  private final double mean;
  private final double standardDev;
  private final double median;
  private final double p5;
  private final double p10;
  private final double p15;
  private final double p20;
  private final double p25;
  private final double p30;
  private final double p40;
  private final double p50;
  private final double p60;
  private final double p70;
  private final double p75;
  private final double p80;
  private final double p85;
  private final double p90;
  private final double p95;
  private final double p98;
  private final double p99;

  public StatisticalValue(long count, double min, double max, double mean, double standardDev,
      double median, double p5, double p10, double p15, double p20, double p25, double p30,
      double p40, double p50, double p60, double p70, double p75, double p80, double p85,
      double p90, double p95, double p98, double p99) {
    this.count = count;
    this.min = min;
    this.max = max;
    this.mean = mean;
    this.standardDev = standardDev;
    this.median = median;
    this.p5 = p5;
    this.p10 = p10;
    this.p15 = p15;
    this.p20 = p20;
    this.p25 = p25;
    this.p30 = p30;
    this.p40 = p40;
    this.p50 = p50;
    this.p60 = p60;
    this.p70 = p70;
    this.p75 = p75;
    this.p80 = p80;
    this.p85 = p85;
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

  public double getP5() {
    return p5;
  }

  public double getP10() {
    return p10;
  }

  public double getP15() {
    return p15;
  }

  public double getP20() {
    return p20;
  }

  public double getP25() {
    return p25;
  }

  public double getP30() {
    return p30;
  }

  public double getP40() {
    return p40;
  }

  public double getP50() {
    return p50;
  }

  public double getP60() {
    return p60;
  }

  public double getP70() {
    return p70;
  }

  public double getP75() {
    return p75;
  }

  public double getP80() {
    return p80;
  }

  public double getP85() {
    return p85;
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
