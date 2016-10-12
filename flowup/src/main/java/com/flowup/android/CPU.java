/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.android;

import android.app.Application;
import android.util.Log;
import com.flowup.metricnames.App;
import com.flowup.unix.Terminal;
import java.io.IOException;

public class CPU {

  private static final String LOGTAG = "FlowUp.CPU";

  private final App app;
  private final Terminal terminal;

  public CPU(Application application, Terminal terminal) {
    this.app = new App(application);
    this.terminal = terminal;
  }

  public int getUsage() {
    try {
      String result = terminal.exec("top -s cpu -n 1");
      return extractCPUUsage(result);
    } catch (IOException e) {
      Log.e(LOGTAG, "Exception catch while reading the CPU usage", e);
      return 0;
    }
  }

  private int extractCPUUsage(String topOutput) {
    String processLine = extractCurrentProcessStats(topOutput);
    String[] topValuesPerProcess = processLine.split(" ");
    for (String value : topValuesPerProcess) {
      if (value.contains("%")) {
        int cpuUsage = Integer.parseInt(value.replace("%", ""));
        return cpuUsage;
      }
    }
    return 0;
  }

  private String extractCurrentProcessStats(String topOutput) {
    String pid = String.valueOf(app.getPid());
    String[] split = topOutput.split("\\n");
    for (String line : split) {
      line = line.trim();
      String[] lineBySpaces = line.split(" ");
      if (lineBySpaces.length > 1 && lineBySpaces[0].equals(pid)) {
        return line;
      }
    }
    return "";
  }
}
