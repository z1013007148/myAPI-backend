package com.api.apiinterface.controller;

import cn.api.sdk.model.Ip;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;


@RestController
@RequestMapping("/ip")
@Slf4j
public class IpController {
    // 查ip的外部接口地址
//    final String url = "http://ip-api.com/json";
    final String url = "http://www.geoplugin.net/json.gp";

    @Value("${interfaces_number}")
    private String number;

    @PostMapping()
    public String getCountryByPost(@RequestBody Ip ip, HttpServletResponse httpServletResponse) {
        // 访问这个网页是GET
        String result = null;
        try {
            HttpResponse res = HttpRequest.get(url + "?ip=" + ip.getIp())
                    .timeout(1000)
                    .execute();
            if(res.getStatus()!=HttpServletResponse.SC_OK){
                throw new Exception();
            }
            result = res.body();
        } catch (Exception e) {
            httpServletResponse.setHeader("status","error");
            return "接口网络异常，请稍后再试";
        }
        JSONObject entries = JSONUtil.parseObj(result);
        Object country = entries.get("geoplugin_countryName");
        return "POST : ip为 " + ip.getIp() + " 的国家或地区为: " + country+" ---->来自服务器"+number+" ";
    }


}
