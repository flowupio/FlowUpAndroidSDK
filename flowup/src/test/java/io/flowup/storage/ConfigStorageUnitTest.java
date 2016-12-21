package io.flowup.storage;

import android.database.sqlite.SQLiteDatabaseLockedException;
import io.flowup.config.Config;
import io.flowup.config.storage.ConfigStorage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.assertFalse;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class) public class ConfigStorageUnitTest {

  private ConfigStorage configStorage;
  @Mock SQLDelightfulOpenHelper openHelper;

  @Before public void setUp() {
    configStorage = new ConfigStorage(openHelper);
  }

  @Test public void returnsDisabledConfigWhenAnUnrecoverableErrorIsThrown() throws Exception {
    when(openHelper.getWritableDatabase()).thenThrow(new SQLiteDatabaseLockedException());

    Config config = configStorage.getConfig();

    assertFalse(config.isEnabled());
  }
}
