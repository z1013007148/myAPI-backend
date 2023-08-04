package com.api.project.controller;

import cn.hutool.core.util.RandomUtil;
import cn.api.sdk.client.ApiClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static cn.api.sdk.utils.SignUtils.genSign;

@SpringBootTest
public class InterfaceInfoControllerTest {

    String accessKey = "81ae7b9ed182dfcc69f4f073de758f00";
    String secretKey = "1bf657a225411d81e74cbedac0356be2";
    ApiClient client = new ApiClient(accessKey, secretKey);
    @Test
    public void testClient(){
//        client.getRandomByGet("/api/random");
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("accessKey", accessKey);
        // 一定不能直接发送, 要通过加密转成sign
        // hashMap.put("secretKey", secretKey);
        hashMap.put("nonce", RandomUtil.randomNumbers(4));
        hashMap.put("body", "");
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        hashMap.put("sign", genSign("", secretKey));
        System.out.println(hashMap);


    }




}
