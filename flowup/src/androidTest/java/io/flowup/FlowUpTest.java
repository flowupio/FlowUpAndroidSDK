package io.flowup;

import android.app.Application;
import java.util.concurrent.CountDownLatch;
import org.junit.Before;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getInstrumentation;

public class FlowUpTest {

  private static final int NUMBER_OF_INITIALIZATIONS = 100;
  private static final String ANY_API_KEY = "flow_up_api_key";

  private FlowUp.Builder flowUpBuilder;

  @Before public void setUp() {
    Application application =
        (Application) getInstrumentation().getContext().getApplicationContext();
    flowUpBuilder = FlowUp.Builder.with(application).apiKey(ANY_API_KEY);
  }

  @Test public void doesNotCrashIfItsInitializedMultipleTimes() {
    for (int i = 0; i < NUMBER_OF_INITIALIZATIONS; i++) {
      flowUpBuilder.start();
    }
  }

  @Test public void doesNotCrashIfItsInitializedMultipleTimesEvenFromDifferentThreads()
      throws Exception {
    final CountDownLatch lath = new CountDownLatch(NUMBER_OF_INITIALIZATIONS);
    for (int i = 0; i < NUMBER_OF_INITIALIZATIONS; i++) {
      new Thread(new Runnable() {
        @Override public void run() {
          flowUpBuilder.start();
          lath.countDown();
        }
      }).start();
    }
    lath.await();
  }
}
