/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.config.storage;

import android.content.Context;
import io.flowup.config.Config;
import io.flowup.storage.RealmStorage;
import io.realm.Realm;

public class ConfigStorage extends RealmStorage {

  private static final String CONFIG_UNIQUE_ID = "1";

  public ConfigStorage(Context context) {
    super(context);
  }

  public Config getConfig() {
    Realm realm = getRealm();
    RealmConfig realmConfig = realm.where(RealmConfig.class).findFirst();
    Config config = null;
    if (realmConfig == null) {
      config = new Config();
    } else {
      config = new Config(realmConfig.isEnabled());
    }
    realm.close();
    return config;
  }

  public void updateConfig(final Config config) {
    executeTransaction(new Realm.Transaction() {
      @Override public void execute(Realm realm) {
        storeAsRealmObject(realm, config);
      }
    });
  }

  private void storeAsRealmObject(Realm realm, Config config) {
    RealmConfig realmConfig = realm.where(RealmConfig.class).findFirst();
    if (realmConfig == null) {
      realmConfig = realm.createObject(RealmConfig.class, CONFIG_UNIQUE_ID);
    }
    realmConfig.setEnabled(config.isEnabled());
    realm.copyToRealmOrUpdate(realmConfig);
  }
}
