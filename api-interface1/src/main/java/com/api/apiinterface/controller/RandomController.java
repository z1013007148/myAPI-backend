package com.api.apiinterface.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping("/random")
@Slf4j
//@Scope("singleton")
public class RandomController {
    Random random = new Random();
    @Value("${interfaces_number}")
    private String number;

    @GetMapping()
    public String getRandomByGet()  {
        int i = random.nextInt(10000);
        String result = String.format("%04d",i);
        return "GET 验证码是" + result + " ---->来自服务器" + number + " ";
    }


}
