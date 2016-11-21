/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.reporter.model;

import com.google.gson.annotations.SerializedName;

public class StatisticalValue {

  @SerializedName("mean") private final double mean;
  @SerializedName("p10") private final double p10;
  @SerializedName("p90") private final double p90;

  public StatisticalValue(double mean, double p10, double p90) {
    this.mean = mean;
    this.p10 = p10;
    this.p90 = p90;
  }

  public double getMean() {
    return mean;
  }

  public double getP10() {
    return p10;
  }

  public double getP90() {
    return p90;
  }

  @Override public String toString() {
    return "StatisticalValue{" + "mean=" + mean + ", p90=" + p90 + '}';
  }
}
