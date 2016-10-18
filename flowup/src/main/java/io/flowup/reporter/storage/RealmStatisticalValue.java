/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.reporter.storage;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmStatisticalValue extends RealmObject {

  static final String ID_FIELD_NAME = "id";

  @PrimaryKey private String id;
  private Long value;
  private Long count;
  private Long min;
  private Long max;
  private Double mean;
  private Double standardDev;
  private Double median;
  private Double p1;
  private Double p2;
  private Double p5;
  private Double p10;
  private Double p80;
  private Double p95;
  private Double p98;
  private Double p99;

  public String getId() {
    return id;
  }

  public Long getValue() {
    return value;
  }

  public void setValue(Long value) {
    this.value = value;
  }

  public Long getCount() {
    return count;
  }

  public void setCount(Long count) {
    this.count = count;
  }

  public Long getMin() {
    return min;
  }

  public void setMin(Long min) {
    this.min = min;
  }

  public Long getMax() {
    return max;
  }

  public void setMax(Long max) {
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

  public Double getP1() {
    return p1;
  }

  public void setP1(Double p1) {
    this.p1 = p1;
  }

  public Double getP2() {
    return p2;
  }

  public void setP2(Double p2) {
    this.p2 = p2;
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

  public Double getP80() {
    return p80;
  }

  public void setP80(Double p80) {
    this.p80 = p80;
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
