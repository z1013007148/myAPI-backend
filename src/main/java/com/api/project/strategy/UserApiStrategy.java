package com.api.project.strategy;

import cn.api.sdk.client.ApiClient;
import com.api.project.myinterface.ApiStrategy;
import com.google.gson.Gson;

public class UserApiStrategy implements ApiStrategy {
    @Override
    public String execute(String userRequestParams, ApiClient client) {
        // 把前端过来的userRequestParams转换成user.class
        Gson gson = new Gson();
        cn.api.sdk.model.User user = gson.fromJson(userRequestParams, cn.api.sdk.model.User.class);

        return client.getUserNameByPost("/api/name/user", user);
    }
}
