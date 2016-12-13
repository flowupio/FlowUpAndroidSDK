/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.config.apiclient;

import io.flowup.android.Device;
import io.flowup.apiclient.ApiClient;
import io.flowup.apiclient.ApiClientResult;
import io.flowup.config.Config;
import java.io.IOException;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

public class ConfigApiClient extends ApiClient {

  private final HttpUrl getConfigUrl;

  public ConfigApiClient(String apiKey, Device device, String scheme, String host, int port,
      boolean forceReportsEnabled) {
    super(apiKey, device, scheme, host, port, forceReportsEnabled);
    getConfigUrl = baseUrl.newBuilder("/config").build();
  }

  public ApiClientResult<Config> getConfig() {
    Request request = new Request.Builder().url(getConfigUrl).get().build();
    Config config = null;
    try {
      Response response = httpClient.newCall(request).execute();
      if (response.isSuccessful()) {
        String jsonBody = response.body().string();
        config = jsonParser.fromJson(jsonBody, Config.class);
        response.close();
      }
    } catch (IOException e) {
      return new ApiClientResult<>(ApiClientResult.Error.NETWORK_ERROR);
    }
    if (config != null) {
      return new ApiClientResult<>(config);
    } else {
      return new ApiClientResult<>(ApiClientResult.Error.UNKNOWN);
    }
  }
}
