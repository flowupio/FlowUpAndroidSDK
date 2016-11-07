/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.config;

import io.flowup.apiclient.ApiClient;
import io.flowup.apiclient.ApiClientResult;
import java.io.IOException;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

public class ConfigApiClient extends ApiClient {

  private final HttpUrl getConfigUrl;

  public ConfigApiClient(String apiKey, String scheme, String host, int port) {
    super(apiKey, scheme, host, port);
    getConfigUrl = baseUrl.newBuilder("/config").build();
  }

  public ApiClientResult<Config> getConfig() {
    Request request = new Request.Builder().url(getConfigUrl).build();
    Config config = null;
    try {
      Response response = httpClient.newCall(request).execute();
      config = jsonParser.fromJson(response.body().string(), Config.class);
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
