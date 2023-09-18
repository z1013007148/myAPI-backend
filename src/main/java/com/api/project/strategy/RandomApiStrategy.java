package com.api.project.strategy;

import cn.api.sdk.client.ApiClient;
import com.api.project.myinterface.ApiStrategy;

public class RandomApiStrategy implements ApiStrategy {
    @Override
    public String execute(String userRequestParams, ApiClient client) {
        return client.getRandomByGet("/api/random");
    }
}
