/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup;

import android.os.Build;
import io.flowup.BuildConfig;
import org.robolectric.annotation.Config;

@Config(constants = BuildConfig.class,
    sdk = Build.VERSION_CODES.LOLLIPOP,
    packageName = "io.flowup") public class RobolectricTest {

}
