/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.config.storage;

import android.content.Context;
import io.flowup.config.Config;
import io.flowup.storage.RealmStorage;
import io.realm.Realm;

public class ConfigStorage extends RealmStorage {

  public ConfigStorage(Context context) {
    super(context);
  }

  public Config getConfig() {
    Realm realm = getRealm();
    RealmConfig realmConfig = realm.where(RealmConfig.class).findFirst();
    realm.close();
    if (realmConfig == null) {
      return new Config();
    } else {
      return new Config(realmConfig.isEnabled());
    }
  }

  public void updateConfig(final Config config) {
    Realm realm = getRealm();
    realm.executeTransaction(new Realm.Transaction() {
      @Override public void execute(Realm realm) {
        storeAsRealmObject(realm, config);
      }
    });
    realm.close();
  }

  private void storeAsRealmObject(Realm realm, Config config) {
    RealmConfig realmConfig = realm.where(RealmConfig.class).findFirst();
    if (realmConfig == null) {
      realmConfig = realm.createObject(RealmConfig.class, "1");
    }
    realmConfig.setEnabled(config.isEnabled());
    realm.copyToRealmOrUpdate(realmConfig);
  }
}
