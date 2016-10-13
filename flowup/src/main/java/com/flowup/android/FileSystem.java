/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.android;

import android.os.Environment;
import android.os.StatFs;

public class FileSystem {

  public int getInternalStorageUsage() {
    StatFs statFs = getInternalStorageStatFs();
    return getStorageUsage(statFs);
  }

  public int getExternalStorageUsage() {
    StatFs statFs = getExternalStorageStatFs();
    return getStorageUsage(statFs);
  }

  private int getStorageUsage(StatFs statFs) {
    double total = getTotalSpace(statFs);
    double busy = getBusySpace(statFs);
    return (int) ((busy / total) * 100);
  }

  private long getBusySpace(StatFs statFs) {
    long total = getTotalSpace(statFs);
    long free = (statFs.getAvailableBlocks() * statFs.getBlockSize());
    return total - free;
  }

  private long getTotalSpace(StatFs statFs) {
    return statFs.getBlockCount() * statFs.getBlockSize();
  }

  private StatFs getInternalStorageStatFs() {
    return getStatFsForPath(Environment.getRootDirectory().getAbsolutePath());
  }

  private StatFs getExternalStorageStatFs() {
    return getStatFsForPath(Environment.getExternalStorageDirectory().getAbsolutePath());
  }

  private StatFs getStatFsForPath(String path) {
    return new StatFs(path);
  }
}
