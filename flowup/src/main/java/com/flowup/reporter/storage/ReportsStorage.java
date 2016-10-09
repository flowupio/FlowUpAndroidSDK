/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter.storage;

import android.content.Context;
import com.flowup.reporter.DropwizardReport;
import com.flowup.reporter.model.Reports;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;
import java.util.LinkedList;
import java.util.List;

public class ReportsStorage {

  private static final String REALM_DB_NAME = "FlowUp.realm";
  private static final long REALM_SCHEMA_VERSION = 1;

  private final Context context;
  private final boolean persistent;

  public ReportsStorage(Context context) {
    this(context, true);
  }

  public ReportsStorage(Context context, boolean persistent) {
    this.context = context;
    this.persistent = persistent;
  }

  public void storeMetrics(final DropwizardReport dropwizardReport) {
    Realm realm = getRealm();
    realm.executeTransaction(new Realm.Transaction() {
      @Override public void execute(Realm realm) {
        storeAsRealmObject(realm, dropwizardReport);
      }
    });
    realm.close();
  }

  public Reports getReports() {
    Realm realm = getRealm();
    RealmResults<RealmReport> reportsSorted =
        realm.where(RealmReport.class).findAllSorted(RealmReport.ID_FIELD_NAME);
    Reports reports = new RealmReportsToReportsMapper().map(reportsSorted);
    realm.close();
    return reports;
  }

  public void deleteReports(final Reports reports) {
    Realm realm = getRealm();
    realm.executeTransaction(new Realm.Transaction() {
      @Override public void execute(Realm realm) {
        for (String reportId : reports.getReportsIds()) {
          RealmResults<RealmReport> reportsToRemove =
              realm.where(RealmReport.class).equalTo(RealmReport.ID_FIELD_NAME, reportId).findAll();
          for (RealmReport realmReport : reportsToRemove) {
            deleteMetricsReports(realm, realmReport.getMetrics());
          }
          reportsToRemove.deleteAllFromRealm();
        }
      }
    });
    realm.close();
  }

  private void deleteMetricsReports(Realm realm, RealmList<RealmMetricReport> metricsToRemove) {
    List<RealmResults<RealmMetricReport>> metricsReportsToRemove = new LinkedList<>();
    for (RealmMetricReport metric : metricsToRemove) {
      RealmResults<RealmMetricReport> metricsReports = realm.where(RealmMetricReport.class)
          .equalTo(RealmMetricReport.ID_FIELD_NAME, metric.getId())
          .findAll();
      deleteStatisticalValues(realm, metric.getStatisticalValue());
      metricsReportsToRemove.add(metricsReports);
    }
    for (RealmResults metricsReportToRemove : metricsReportsToRemove) {
      metricsReportToRemove.deleteAllFromRealm();
    }
  }

  private void deleteStatisticalValues(Realm realm,
      RealmStatisticalValue statisticalValueToRemove) {
    realm.where(RealmStatisticalValue.class)
        .equalTo(RealmStatisticalValue.ID_FIELD_NAME, statisticalValueToRemove.getId())
        .findAll()
        .deleteAllFromRealm();
  }

  private Realm getRealm() {
    RealmConfiguration config = getRealmConfig(context, persistent);
    return Realm.getInstance(config);
  }

  private RealmConfiguration getRealmConfig(Context context, boolean persistent) {
    Realm.init(context);
    RealmConfiguration.Builder builder = new RealmConfiguration.Builder().name(REALM_DB_NAME)
        .schemaVersion(REALM_SCHEMA_VERSION)
        .deleteRealmIfMigrationNeeded();
    if (!persistent) {
      builder.inMemory();
    }
    return builder.build();
  }

  private void storeAsRealmObject(Realm realm, DropwizardReport dropwizardReport) {
    String reportingTimestamp = String.valueOf(dropwizardReport.getReportingTimestamp());
    RealmReport report = realm.createObject(RealmReport.class, reportingTimestamp);
    RealmList<RealmMetricReport> realmMetricsReports =
        new DropwizardReportToRealmMetricReportMapper(realm).map(dropwizardReport);
    report.setMetrics(realmMetricsReports);
    realm.insertOrUpdate(report);
  }
}
