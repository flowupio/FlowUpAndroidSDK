/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.sampling;

import java.util.Random;

public class SamplingGroup {

  private final Random random;
  private final double configuredSampling;

  public SamplingGroup(Random random, double configuredSampling) {
    validateConfiguredSampling(configuredSampling);
    this.random = random;
    this.configuredSampling = configuredSampling;
  }

  public boolean isIn() {
    return random.nextDouble() < configuredSampling;
  }

  private void validateConfiguredSampling(double configuredSampling) {
    if (configuredSampling < 0 || configuredSampling > 1) {
      throw new IllegalArgumentException("The sampling configured should be between 0 and 1");
    }
  }
}
