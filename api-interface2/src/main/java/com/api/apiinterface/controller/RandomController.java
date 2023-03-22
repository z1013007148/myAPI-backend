package com.api.apiinterface.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@RestController
@RequestMapping("/random")
@Slf4j
@Scope("singleton")
public class RandomController {
    Random random = new Random();
    @Value("${interfaces_number}")
    private String number;

    @GetMapping()
    public String getRandomByGet() throws InterruptedException {
        int i = random.nextInt(10000);
        return "GET 随机数是" + i + " ---->来自服务器" + number + " ";
    }


}
