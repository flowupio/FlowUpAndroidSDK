/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.config.storage;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmConfig extends RealmObject {

  @PrimaryKey private String uniqueId;
  private boolean enabled;

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }
}
