/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup;

import android.os.Build;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@Config(constants = BuildConfig.class,
    sdk = Build.VERSION_CODES.LOLLIPOP,
    packageName = "com.flowup")
public class RobolectricTest {

}
