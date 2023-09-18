package com.api.project.myinterface;

import cn.api.sdk.client.ApiClient;

public interface ApiStrategy {
    String execute(String userRequestParams, ApiClient client);
}
