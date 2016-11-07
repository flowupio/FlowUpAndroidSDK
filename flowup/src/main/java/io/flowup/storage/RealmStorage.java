/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.storage;

import android.content.Context;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RealmStorage {

  private final Context context;

  public RealmStorage(Context context) {
    this.context = context;
  }

  protected Realm getRealm() {
    RealmConfiguration config = RealmConfig.getRealmConfig(context);
    return Realm.getInstance(config);
  }
}
