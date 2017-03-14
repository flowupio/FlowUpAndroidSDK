/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.reporter.model;

import com.google.gson.annotations.SerializedName;

public class StatisticalValue {

  @SerializedName("mean") private final Double mean;
  @SerializedName("p10") private final Double p10;
  @SerializedName("p90") private final Double p90;

  public StatisticalValue(Double mean, Double p10, Double p90) {
    this.mean = mean;
    this.p10 = p10;
    this.p90 = p90;
  }

  public Double getMean() {
    return mean;
  }

  public Double getP10() {
    return p10;
  }

  public Double getP90() {
    return p90;
  }

  @Override public String toString() {
    return "StatisticalValue{" + "mean=" + mean + ", p90=" + p90 + '}';
  }
}
