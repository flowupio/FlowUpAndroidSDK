package io.flowup.reporter.model;

import com.google.gson.annotations.SerializedName;

public class DiskMetric extends Metric {

  @SerializedName("internalStorageWrittenBytes") private final long internalStorageWrittenBytes;
  @SerializedName("sharedPreferencesWrittenBytes") private final long sharedPreferencesWrittenBytes;

  public DiskMetric(long timestamp, String appVersionName, String osVersion, boolean batterySaverOn,
      long internalStorageWrittenBytes, long sharedPreferencesWrittenBytes) {
    super(timestamp, appVersionName, osVersion, batterySaverOn);
    this.internalStorageWrittenBytes = internalStorageWrittenBytes;
    this.sharedPreferencesWrittenBytes = sharedPreferencesWrittenBytes;
  }

  public long getInternalStorageWrittenBytes() {
    return internalStorageWrittenBytes;
  }

  public long getSharedPreferencesWrittenBytes() {
    return sharedPreferencesWrittenBytes;
  }

  @Override public String toString() {
    return "DiskMetric{"
        + "internalStorageWrittenBytes="
        + internalStorageWrittenBytes
        + ", \n"
        + "sharedPreferencesWrittenBytes="
        + sharedPreferencesWrittenBytes
        + "\n"
        + '}';
  }
}
