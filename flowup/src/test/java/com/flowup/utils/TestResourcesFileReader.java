package com.flowup.utils;

import android.support.annotation.NonNull;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import org.apache.commons.io.FileUtils;

public class TestResourcesFileReader {

  private static final String FILE_ENCODING = "UTF-8";

  public static String getContentFromFile(String fileName) throws IOException {
    return getContentFromFile(fileName, "");
  }

  public static String getContentFromFileWithNewLines(String fileName) throws IOException {
    return getContentFromFile(fileName, "\n");
  }

  private static String getContentFromFile(String fileName, String separator)
      throws IOException {
    URL resource = fileName.getClass().getResource("/" + fileName);
    String composeFileName = resource.getFile();
    File file = new File(composeFileName);
    List<String> lines = FileUtils.readLines(file, FILE_ENCODING);
    StringBuilder stringBuilder = new StringBuilder();
    for (String line : lines) {
      stringBuilder.append(line);
      stringBuilder.append(separator);
    }
    return stringBuilder.toString();
  }
}
