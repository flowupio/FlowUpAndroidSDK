/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.android;

import android.util.Log;
import java.io.IOException;
import java.io.RandomAccessFile;

public class CPU {

  private static final String LOGTAG = "FlowUp.CPU";

  public float getLoad() {
    try {
      RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
      String load = reader.readLine();

      String[] toks = load.split(" ");

      long idle1 = Long.parseLong(toks[5]);
      long cpu1 = Long.parseLong(toks[2])
          + Long.parseLong(toks[3])
          + Long.parseLong(toks[4])
          + Long.parseLong(toks[6])
          + Long.parseLong(toks[7])
          + Long.parseLong(toks[8]);

      try {
        Thread.sleep(360);
      } catch (Exception e) {
        Log.e(LOGTAG, "Exception catch waiting for CPU usage lecture",e);
      }

      reader.seek(0);
      load = reader.readLine();
      reader.close();

      toks = load.split(" ");

      long idle2 = Long.parseLong(toks[5]);
      long cpu2 = Long.parseLong(toks[2])
          + Long.parseLong(toks[3])
          + Long.parseLong(toks[4])
          + Long.parseLong(toks[6])
          + Long.parseLong(toks[7])
          + Long.parseLong(toks[8]);

      return (float) (cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));
    } catch (IOException ex) {
      Log.e(LOGTAG, "Exception catch reading CPU usage file",ex);
    }
    return 0;
  }
}
