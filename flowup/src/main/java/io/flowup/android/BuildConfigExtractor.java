package io.flowup.android;

import android.content.Context;
import java.lang.reflect.Field;

public class BuildConfigExtractor {

  public boolean isApplicationDebuggable(Context context) throws Exception {
    Class<?> clazz = Class.forName(context.getPackageName() + ".BuildConfig");
    Field field = clazz.getField("DEBUG");
    return field.getBoolean(null);
  }
}
