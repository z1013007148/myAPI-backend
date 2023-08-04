package com.api.apiinterface.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @ClassName AvatarController
 * @Description
 */
@RestController
@RequestMapping("/avatar")
public class AvatarController {


    @GetMapping("/avatarUrl")
    public String getAvatarUrlByPost( HttpServletRequest request) throws Exception {
        //https://restapi.amap.com/v3/weather/weatherInfo?
        String avatarUrl = "https://www.loliapi.com/acg/pp/";
        return getRedirectUrl(avatarUrl);
    }


    /**
     * 获取重定向地址
     * @param path
     * @return
     * @throws Exception
     */
    private String getRedirectUrl(String path) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(path)
                .openConnection();
        conn.setInstanceFollowRedirects(false);
        conn.setConnectTimeout(5000);
        return conn.getHeaderField("Location");
    }

}
