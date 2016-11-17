/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.reporter.storage;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.auto.value.AutoValue;
import com.squareup.sqldelight.RowMapper;

@AutoValue abstract class SQLDelightMetric implements MetricModel {

  private static final MetricModel.Factory<SQLDelightMetric> FACTORY =
      new MetricModel.Factory<>(new MetricModel.Creator<SQLDelightMetric>() {

        @Override public SQLDelightMetric create(long id, long reportId, @NonNull String metricName,
            @Nullable Double value, @Nullable Double mean, @Nullable Double p10,
            @Nullable Double p90) {
          return new AutoValue_SQLDelightMetric(id, reportId, metricName, value, mean, p10, p90);
        }
      });

  private static final RowMapper<SQLDelightMetric> METRIC_MAPPER =
      FACTORY.get_metrics_by_report_timestampMapper();
}
