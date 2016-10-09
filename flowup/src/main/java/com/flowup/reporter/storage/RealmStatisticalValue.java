/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter.storage;

import io.realm.RealmObject;

public class RealmStatisticalValue extends RealmObject {

  private long value;

  private Long count;
  private Double min;
  private Double max;
  private Double mean;
  private Double standardDev;
  private Double median;
  private Double p5;
  private Double p10;
  private Double p15;
  private Double p20;
  private Double p25;
  private Double p30;
  private Double p40;
  private Double p50;
  private Double p60;
  private Double p70;
  private Double p75;
  private Double p80;
  private Double p85;
  private Double p90;
  private Double p95;
  private Double p98;
  private Double p99;

  public long getValue() {
    return value;
  }

  public void setValue(long value) {
    this.value = value;
  }

  public Long getCount() {
    return count;
  }

  public void setCount(Long count) {
    this.count = count;
  }

  public Double getMin() {
    return min;
  }

  public void setMin(Double min) {
    this.min = min;
  }

  public Double getMax() {
    return max;
  }

  public void setMax(Double max) {
    this.max = max;
  }

  public Double getMean() {
    return mean;
  }

  public void setMean(Double mean) {
    this.mean = mean;
  }

  public Double getStandardDev() {
    return standardDev;
  }

  public void setStandardDev(Double standardDev) {
    this.standardDev = standardDev;
  }

  public Double getMedian() {
    return median;
  }

  public void setMedian(Double median) {
    this.median = median;
  }

  public Double getP5() {
    return p5;
  }

  public void setP5(Double p5) {
    this.p5 = p5;
  }

  public Double getP10() {
    return p10;
  }

  public void setP10(Double p10) {
    this.p10 = p10;
  }

  public Double getP15() {
    return p15;
  }

  public void setP15(Double p15) {
    this.p15 = p15;
  }

  public Double getP20() {
    return p20;
  }

  public void setP20(Double p20) {
    this.p20 = p20;
  }

  public Double getP25() {
    return p25;
  }

  public void setP25(Double p25) {
    this.p25 = p25;
  }

  public Double getP30() {
    return p30;
  }

  public void setP30(Double p30) {
    this.p30 = p30;
  }

  public Double getP40() {
    return p40;
  }

  public void setP40(Double p40) {
    this.p40 = p40;
  }

  public Double getP50() {
    return p50;
  }

  public void setP50(Double p50) {
    this.p50 = p50;
  }

  public Double getP60() {
    return p60;
  }

  public void setP60(Double p60) {
    this.p60 = p60;
  }

  public Double getP70() {
    return p70;
  }

  public void setP70(Double p70) {
    this.p70 = p70;
  }

  public Double getP75() {
    return p75;
  }

  public void setP75(Double p75) {
    this.p75 = p75;
  }

  public Double getP80() {
    return p80;
  }

  public void setP80(Double p80) {
    this.p80 = p80;
  }

  public Double getP85() {
    return p85;
  }

  public void setP85(Double p85) {
    this.p85 = p85;
  }

  public Double getP90() {
    return p90;
  }

  public void setP90(Double p90) {
    this.p90 = p90;
  }

  public Double getP95() {
    return p95;
  }

  public void setP95(Double p95) {
    this.p95 = p95;
  }

  public Double getP98() {
    return p98;
  }

  public void setP98(Double p98) {
    this.p98 = p98;
  }

  public Double getP99() {
    return p99;
  }

  public void setP99(Double p99) {
    this.p99 = p99;
  }
}
