/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.sampling;

import java.util.Random;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class) public class SamplingGroupTest {

  private static final double ANY_CONFIGURED_SAMPLING = 0.2;

  @Mock private Random random;

  @Test(expected = IllegalArgumentException.class)
  public void doesNotAcceptConfiguredSamplingIfIsLowerThanZero() {
    givenASamplingGroup(-1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void doesNotAcceptConfiguredSamplingIfIsGreaterThanOne() {
    givenASamplingGroup(1.1d);
  }

  @Test public void zeroAsSamplingDoesNotGenerateValidUsers() {
    SamplingGroup samplingGroup = givenASamplingGroup(0);
    for (double i = 0; i <= 1; i += 0.1d) {
      givenTheRandomGenerates(i);

      boolean result = samplingGroup.isIn();

      assertFalse(result);
    }
  }

  @Test public void oneAsSamplingGeneratesAlwaysValidUsers() {
    SamplingGroup samplingGroup = givenASamplingGroup(1);
    for (double i = 0; i <= 1; i += 0.1d) {
      givenTheRandomGenerates(i);

      boolean result = samplingGroup.isIn();

      assertTrue(result);
    }
  }

  @Test
  public void anySamplingConfiguredOnlyAllowsUsersWithARandomValueLowerOrEqualToTheSampling() {
    SamplingGroup samplingGroup = givenASamplingGroup(ANY_CONFIGURED_SAMPLING);
    for (double i = 0; i <= 1; i += 0.1d) {
      givenTheRandomGenerates(i);

      boolean result = samplingGroup.isIn();

      if (i < 0.2) {
        assertTrue(result);
      }
    }
  }

  @Test
  public void anySamplingConfiguredDoesNotAllowUsersIfTheRandomValueIsGreaterThanTheSampling() {
    SamplingGroup samplingGroup = givenASamplingGroup(ANY_CONFIGURED_SAMPLING);
    for (double i = 0; i <= 1; i += 0.1d) {
      givenTheRandomGenerates(i);

      boolean result = samplingGroup.isIn();

      if (i > ANY_CONFIGURED_SAMPLING) {
        assertFalse(result);
      }
    }
  }

  private void givenTheRandomGenerates(double nextDouble) {
    when(random.nextDouble()).thenReturn(nextDouble);
  }

  private SamplingGroup givenASamplingGroup(double configuredSampling) {
    return new SamplingGroup(random, configuredSampling);
  }
}