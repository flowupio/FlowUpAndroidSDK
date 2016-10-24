/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.collectors;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import io.flowup.android.EmptyActivityLifecycleCallback;
import io.flowup.android.MainThread;
import io.flowup.metricnames.MetricNamesGenerator;
import io.flowup.utils.Time;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN) class ActivityLifecycleCollector
    implements Collector {

  private final Application application;
  private final MetricNamesGenerator metricNamesGenerator;
  private final MainThread mainThread;
  private final Time time;

  ActivityLifecycleCollector(Application application, MetricNamesGenerator metricNamesGenerator,
      MainThread mainThread, Time time) {
    this.application = application;
    this.metricNamesGenerator = metricNamesGenerator;
    this.mainThread = mainThread;
    this.time = time;
  }

  @Override public void initialize(final MetricRegistry registry) {
    application.registerActivityLifecycleCallbacks(new EmptyActivityLifecycleCallback() {

      private Map<String, Long> onCreateTimers = new HashMap<>();
      private Map<String, Long> onStartTimers = new HashMap<>();
      private Map<String, Long> onResumeTimers = new HashMap<>();
      private Map<String, Long> onPauseTimers = new HashMap<>();
      private Map<String, Long> onStopTimers = new HashMap<>();
      private Map<String, Long> onDestroyTimers = new HashMap<>();

      @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        saveOnCreateTimestamp(activity);
      }

      @Override public void onActivityStarted(Activity activity) {
        updateOnCreateTimer(activity);
        saveOnStartTimestamp(activity);
      }

      @Override public void onActivityResumed(final Activity activity) {
        updateOnStartTimer(activity);
        saveOnresumeTimestamp(activity);
        mainThread.post(new Runnable() {
          @Override public void run() {
            updateOnResumeTimer(activity);
          }
        });
      }

      @Override public void onActivityPaused(Activity activity) {
        saveOnPauseTimestamp(activity);
      }

      @Override public void onActivityStopped(Activity activity) {
        updateOnPauseTimer(activity);
        saveOnStopTimestamp(activity);
      }

      @Override public void onActivityDestroyed(final Activity activity) {
        updateOnStopTimer(activity);
        saveOnDestroyTimestamp(activity);
        new Handler(Looper.myLooper()).post(new Runnable() {
          @Override public void run() {
            updateOnDestroyTimer(activity);
          }
        });
      }

      private Long saveOnCreateTimestamp(Activity activity) {
        return saveOnStartTimestamp(activity, onCreateTimers);
      }

      private void updateOnCreateTimer(Activity activity) {
        updateTimer(activity, onCreateTimers, new CreateTimer() {
          @Override Timer create(Activity activity) {
            return registry.timer(metricNamesGenerator.getOnActivityCreatedMetricName(activity));
          }
        });
      }

      private Long saveOnStartTimestamp(Activity activity) {
        return saveOnStartTimestamp(activity, onStartTimers);
      }

      private void updateOnStartTimer(Activity activity) {
        updateTimer(activity, onStartTimers, new CreateTimer() {
          @Override Timer create(Activity activity) {
            return registry.timer(metricNamesGenerator.getOnActivityStartedMetricName(activity));
          }
        });
      }

      private Long saveOnresumeTimestamp(Activity activity) {
        return saveOnStartTimestamp(activity, onResumeTimers);
      }

      private void updateOnResumeTimer(Activity activity) {
        updateTimer(activity, onResumeTimers, new CreateTimer() {
          @Override Timer create(Activity activity) {
            return registry.timer(metricNamesGenerator.getOnActivityResumedMetricName(activity));
          }
        });
      }

      private Long saveOnPauseTimestamp(Activity activity) {
        return saveOnStartTimestamp(activity, onPauseTimers);
      }

      private void updateOnPauseTimer(Activity activity) {
        updateTimer(activity, onPauseTimers, new CreateTimer() {
          @Override Timer create(Activity activity) {
            return registry.timer(metricNamesGenerator.getOnActivityPausedMetricName(activity));
          }
        });
      }

      private Long saveOnStopTimestamp(Activity activity) {
        return saveOnStartTimestamp(activity, onStopTimers);
      }

      private void updateOnStopTimer(Activity activity) {
        updateTimer(activity, onStopTimers, new CreateTimer() {
          @Override Timer create(Activity activity) {
            return registry.timer(metricNamesGenerator.getOnActivityStoppedMetricName(activity));
          }
        });
      }

      private Long saveOnDestroyTimestamp(Activity activity) {
        return saveOnStartTimestamp(activity, onDestroyTimers);
      }

      private void updateOnDestroyTimer(Activity activity) {
        updateTimer(activity, onDestroyTimers, new CreateTimer() {
          @Override Timer create(Activity activity) {
            return registry.timer(metricNamesGenerator.getOnActivityDestroyedMetricName(activity));
          }
        });
      }

      private Long saveOnStartTimestamp(Activity activity, Map<String, Long> map) {
        String activityClassName = activity.getClass().getName();
        Long context = map.get(activityClassName);
        if (context == null) {
          context = time.nowInNanos();
          map.put(activityClassName, context);
        }
        return context;
      }

      private void updateTimer(Activity activity, Map<String, Long> map, CreateTimer createTimer) {
        String activityName = activity.getClass().getName();
        Long context = map.get(activityName);
        if (context != null) {
          long duration = time.nowInNanos() - context;
          createTimer.create(activity).update(duration, TimeUnit.NANOSECONDS);
          map.remove(activityName);
        }
      }

      abstract class CreateTimer {
        abstract Timer create(Activity activity);
      }
    });
  }
}
