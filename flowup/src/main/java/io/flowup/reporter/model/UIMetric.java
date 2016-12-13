/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.reporter.model;

import com.google.gson.annotations.SerializedName;

public class UIMetric extends Metric {

  @SerializedName("screen") private final String screen;
  @SerializedName("frameTime") private final StatisticalValue frameTime;
  @SerializedName("onActivityCreatedTime") private final StatisticalValue onActivityCreatedTime;
  @SerializedName("onActivityStartedTime") private final StatisticalValue onActivityStartedTime;
  @SerializedName("onActivityResumedTime") private final StatisticalValue onActivityResumedTime;
  @SerializedName("activityVisibleTime") private final StatisticalValue activityVisibleTime;
  @SerializedName("onActivityPausedTime") private final StatisticalValue onActivityPausedTime;
  @SerializedName("onActivityStoppedTime") private final StatisticalValue onActivityStoppedTime;
  @SerializedName("onActivityDestroyedTime") private final StatisticalValue onActivityDestroyedTime;

  public UIMetric(long timestamp, String appVersionName, String osVersion, boolean batterySaverOn,
      boolean isInBackground, String screen, StatisticalValue frameTime,
      StatisticalValue onActivityCreatedTime, StatisticalValue onActivityStartedTime,
      StatisticalValue onActivityResumedTime, StatisticalValue activityVisibleTime,
      StatisticalValue onActivityPausedTime, StatisticalValue onActivityStoppedTime,
      StatisticalValue onActivityDestroyedTime) {
    super(timestamp, appVersionName, osVersion, batterySaverOn, isInBackground);
    this.screen = screen;
    this.frameTime = frameTime;
    this.onActivityCreatedTime = onActivityCreatedTime;
    this.onActivityStartedTime = onActivityStartedTime;
    this.onActivityResumedTime = onActivityResumedTime;
    this.activityVisibleTime = activityVisibleTime;
    this.onActivityPausedTime = onActivityPausedTime;
    this.onActivityStoppedTime = onActivityStoppedTime;
    this.onActivityDestroyedTime = onActivityDestroyedTime;
  }

  public String getScreen() {
    return screen;
  }

  public StatisticalValue getFrameTime() {
    return frameTime;
  }

  public StatisticalValue getOnActivityCreatedTime() {
    return onActivityCreatedTime;
  }

  public StatisticalValue getOnActivityStartedTime() {
    return onActivityStartedTime;
  }

  public StatisticalValue getOnActivityResumedTime() {
    return onActivityResumedTime;
  }

  public StatisticalValue getActivityVisibleTime() {
    return activityVisibleTime;
  }

  public StatisticalValue getOnActivityPausedTime() {
    return onActivityPausedTime;
  }

  public StatisticalValue getOnActivityStoppedTime() {
    return onActivityStoppedTime;
  }

  public StatisticalValue getOnActivityDestroyedTime() {
    return onActivityDestroyedTime;
  }

  @Override public String toString() {
    return "UIMetric{"
        +
        "timestamp="
        + getTimestamp()
        + ", \n"
        + "screen="
        + screen
        + ", \n"
        + "frameTime="
        + frameTime
        + ", \n"
        + "onActivityCreatedTime="
        + onActivityCreatedTime
        + ", \n"
        + "onActivityStartedTime="
        + onActivityStartedTime
        + ", \n"
        + "onActivityResumedTime="
        + onActivityResumedTime
        + ", \n"
        + "activityVisibleTime="
        + activityVisibleTime
        + ", \n"
        + "onActivityPausedTime="
        + onActivityPausedTime
        + ", \n"
        + "onActivityStoppedTime="
        + onActivityStoppedTime
        + ", \n"
        + "onActivityDestroyedTime="
        + onActivityDestroyedTime
        + "\n"
        + '}';
  }
}
