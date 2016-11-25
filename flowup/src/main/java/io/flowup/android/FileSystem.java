/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.android;

import android.content.Context;
import java.io.File;
import java.util.Stack;

public class FileSystem {

  private final String internalStoragePath;
  private final String sharedPreferencesPath;

  public FileSystem(Context context) {
    String absolutePath = context.getFilesDir().getAbsolutePath();
    this.internalStoragePath = absolutePath.replace("files", "");
    this.sharedPreferencesPath = absolutePath.replace("files", "shared_prefs");
  }

  public long getInternalStorageWrittenBytes() {
    File internalStorageFolder = new File(internalStoragePath);
    return getFolderSize(internalStorageFolder);
  }

  public long getSharedPreferencesWrittenBytes() {
    File sharedPreferencesStorage = new File(sharedPreferencesPath);
    return getFolderSize(sharedPreferencesStorage);
  }

  String getInternalStoragePath() {
    return internalStoragePath;
  }

  String getSharedPreferencesPath() {
    return sharedPreferencesPath;
  }

  private long getFolderSize(File dir) {
    long result = 0;
    Stack<File> foldersList = new Stack<>();
    foldersList.clear();
    foldersList.push(dir);
    while (!foldersList.isEmpty()) {
      File currentFolder = foldersList.pop();
      File[] filesList = currentFolder.listFiles();
      for (File file : filesList) {
        if (file.isDirectory()) {
          foldersList.push(file);
        } else {
          result += file.length();
        }
      }
    }

    return result;
  }
}
