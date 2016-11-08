/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.config;

import io.flowup.apiclient.ApiClientResult;
import io.flowup.config.apiclient.ConfigApiClient;
import io.flowup.config.storage.ConfigStorage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class) public class FlowUpConfigTest {

  @Mock ConfigStorage storage;
  @Mock ConfigApiClient apiClient;

  private FlowUpConfig flowUpConfig;

  @Before public void setUp() throws Exception {
    when(storage.getConfig()).thenReturn(new Config());
    flowUpConfig = new FlowUpConfig(storage, apiClient);
  }

  @Test public void returnsFalseIfTheApiClientReturnsAnErrorDuringTheUpdate() throws Exception {
    givenThereIsNoConnection();

    boolean result = flowUpConfig.updateConfig();

    assertFalse(result);
  }

  @Test public void returnsTrueIfTheApiClientObtainsTheConfigProperlyDuringTheUpdate()
      throws Exception {
    givenTheApiReturnsANewConfig();

    boolean result = flowUpConfig.updateConfig();

    assertTrue(result);
  }

  @Test public void updatesTheConfigPersistedDuringTheUpdateProcess() throws Exception {
    Config newConfig = givenTheApiReturnsANewConfig();

    flowUpConfig.updateConfig();

    verify(storage).updateConfig(newConfig);
  }

  @Test public void returnsTheConfigPersisted() throws Exception {
    Config persistedConfig = givenAnAlreadyPersistedConfig();

    Config config = flowUpConfig.getConfig();

    assertEquals(persistedConfig, config);
  }

  @Test public void disableConfigShouldPersistAConfigDisabled() throws Exception {
    flowUpConfig.disableClient();

    verify(storage).updateConfig(new Config(false));
  }

  private Config givenTheApiReturnsANewConfig() {
    Config config = new Config(false);
    when(apiClient.getConfig()).thenReturn(new ApiClientResult<Config>(config));
    return config;
  }

  private Config givenAnAlreadyPersistedConfig() {
    Config persistedConfig = new Config(false);
    when(storage.getConfig()).thenReturn(persistedConfig);
    return persistedConfig;
  }

  private void givenThereIsNoConnection() {
    when(apiClient.getConfig()).thenReturn(
        new ApiClientResult<Config>(ApiClientResult.Error.NETWORK_ERROR));
  }
}