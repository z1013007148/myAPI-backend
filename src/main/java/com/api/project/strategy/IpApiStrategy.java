package com.api.project.strategy;

import cn.api.sdk.client.ApiClient;
import com.api.project.myinterface.ApiStrategy;
import com.google.gson.Gson;

public class IpApiStrategy implements ApiStrategy {
    @Override
    public String execute(String userRequestParams, ApiClient client) {
        Gson gson = new Gson();
        cn.api.sdk.model.Ip ip = gson.fromJson(userRequestParams, cn.api.sdk.model.Ip.class);
        return client.getIpCountryByPost("/api/ip", ip);
    }
}
