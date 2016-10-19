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
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import io.flowup.android.EmptyActivityLifecycleCallback;
import io.flowup.metricnames.MetricNamesGenerator;
import java.util.HashMap;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN) class ActivityLifecycleCollector
    implements Collector {

  private final Application application;
  private final MetricNamesGenerator metricNamesGenerator;

  ActivityLifecycleCollector(Application application, MetricNamesGenerator metricNamesGenerator) {
    this.application = application;
    this.metricNamesGenerator = metricNamesGenerator;
  }

  @Override public void initialize(final MetricRegistry registry) {
    application.registerActivityLifecycleCallbacks(new EmptyActivityLifecycleCallback() {

      private Map<String, Timer.Context> onCreateTimers = new HashMap<>();
      private Map<String, Timer.Context> onStartTimers = new HashMap<>();
      private Map<String, Timer.Context> onResumeTimers = new HashMap<>();
      private Map<String, Timer.Context> activityVisibleTimers = new HashMap<>();
      private Map<String, Timer.Context> onPauseTimers = new HashMap<>();
      private Map<String, Timer.Context> onStopTimers = new HashMap<>();
      private Map<String, Timer.Context> onDestroyTimers = new HashMap<>();

      @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        getOnCreateTimer(activity);
      }

      @Override public void onActivityStarted(Activity activity) {
        stopOnCreateTimer(activity);
        getOnStartTimer(activity);
      }

      @Override public void onActivityResumed(final Activity activity) {
        getOnStartTimer(activity).stop();
        getOnResumeTimer(activity);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
          @Override public void run() {
            stopOnResumeTimer(activity);
          }
        });
        getActivityVisibleTimer(activity);
      }

      @Override public void onActivityPaused(Activity activity) {
        stopActivityVisibleTimer(activity);
        getOnPauseTimer(activity);
      }

      @Override public void onActivityStopped(Activity activity) {
        stopOnPauseTimer(activity);
        getOnStopTimer(activity);
      }

      @Override public void onActivityDestroyed(final Activity activity) {
        stopOnStopTimer(activity);
        getOnDestroyTimer(activity);
        new Handler(Looper.myLooper()).post(new Runnable() {
          @Override public void run() {
            stopOnDestroyTimer(activity);
          }
        });
      }

      private Timer.Context getOnCreateTimer(Activity activity) {
        return initializeTimer(activity, onCreateTimers, new CreateTimer() {
          @Override Timer create(Activity activity) {
            return registry.timer(metricNamesGenerator.getOnActivityCreatedMetricName(activity));
          }
        });
      }

      private void stopOnCreateTimer(Activity activity) {
        stopTimer(activity, onCreateTimers);
      }

      private Timer.Context getOnStartTimer(Activity activity) {
        return initializeTimer(activity, onStartTimers, new CreateTimer() {
          @Override Timer create(Activity activity) {
            return registry.timer(metricNamesGenerator.getOnActivityStartedMetricName(activity));
          }
        });
      }

      private void stopOnStartTimer(Activity activity) {
        stopTimer(activity, onStartTimers);
      }

      private Timer.Context getOnResumeTimer(Activity activity) {
        return initializeTimer(activity, onResumeTimers, new CreateTimer() {
          @Override Timer create(Activity activity) {
            return registry.timer(metricNamesGenerator.getOnActivityResumedMetricName(activity));
          }
        });
      }

      private void stopOnResumeTimer(Activity activity) {
        stopTimer(activity, onResumeTimers);
      }

      private Timer.Context getActivityVisibleTimer(Activity activity) {
        return initializeTimer(activity, activityVisibleTimers, new CreateTimer() {
          @Override Timer create(Activity activity) {
            return registry.timer(metricNamesGenerator.getActivityVisibleMetricName(activity));
          }
        });
      }

      private void stopActivityVisibleTimer(Activity activity) {
        stopTimer(activity, activityVisibleTimers);
      }

      private Timer.Context getOnPauseTimer(Activity activity) {
        return initializeTimer(activity, onPauseTimers, new CreateTimer() {
          @Override Timer create(Activity activity) {
            return registry.timer(metricNamesGenerator.getOnActivityPausedMetricName(activity));
          }
        });
      }

      private void stopOnPauseTimer(Activity activity) {
        stopTimer(activity, onPauseTimers);
      }

      private Timer.Context getOnStopTimer(Activity activity) {
        return initializeTimer(activity, onStopTimers, new CreateTimer() {
          @Override Timer create(Activity activity) {
            return registry.timer(metricNamesGenerator.getOnActivityStoppedMetricName(activity));
          }
        });
      }

      private void stopOnStopTimer(Activity activity) {
        stopTimer(activity, onStopTimers);
      }

      private Timer.Context getOnDestroyTimer(Activity activity) {
        return initializeTimer(activity, onDestroyTimers, new CreateTimer() {
          @Override Timer create(Activity activity) {
            return registry.timer(metricNamesGenerator.getOnActivityDestroyedMetricName(activity));
          }
        });
      }

      private void stopOnDestroyTimer(Activity activity) {
        stopTimer(activity, onDestroyTimers);
      }

      private Timer.Context initializeTimer(Activity activity, Map<String, Timer.Context> map,
          CreateTimer createTimer) {
        String activityClassName = activity.getClass().getName();
        Timer.Context context = map.get(activityClassName);
        if (context == null) {
          context = createTimer.create(activity).time();
          map.put(activityClassName, context);
        }
        return context;
      }

      private void stopTimer(Activity activity, Map<String, Timer.Context> map) {
        String activityName = activity.getClass().getName();
        Timer.Context context = map.get(activityName);
        if (context != null) {
          context.stop();
          map.remove(activityName);
        }
      }

      abstract class CreateTimer {
        abstract Timer create(Activity activity);
      }
    });
  }
}
