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
    this.internalStoragePath = context.getFilesDir().getAbsolutePath();
    this.sharedPreferencesPath = internalStoragePath.replace("files", "shared_prefs");
  }

  public long getInternalStorageWrittenBytes() {
    File internalStorageFolder = new File(internalStoragePath);
    return getFolderSize(internalStorageFolder);
  }

  public long getSharedPreferencesWrittenBytes() {
    File internalStorageFolder = new File(sharedPreferencesPath);
    return getFolderSize(internalStorageFolder);
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
