package com.api.project.factory;

import com.api.project.myinterface.ApiStrategy;
import com.api.project.strategy.*;

import java.util.HashMap;
import java.util.Map;

public class ApiStrategyFactory {
    private static final Map<String, ApiStrategy> strategies;

    static{
        // 在构造函数中初始化不同的策略
        strategies = new HashMap<>();
        strategies.put("/api/name/user", new UserApiStrategy());
        strategies.put("/api/random", new RandomApiStrategy());
        strategies.put("/api/word", new WordApiStrategy());
        strategies.put("/api/ip", new IpApiStrategy());
        strategies.put("/api/avatar", new AvatarApiStrategy());
    }

    public  static ApiStrategy getStrategy(String interface_type) {
        return strategies.get(interface_type);
    }

}
