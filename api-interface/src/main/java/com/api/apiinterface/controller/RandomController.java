package com.api.apiinterface.controller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.Random;

@RestController
@RequestMapping("/random")
@Slf4j
public class RandomController {
    Random random = new Random();
    @GetMapping()
    public String getRandomByGet() {
        int i = random.nextInt(10000);

        return "GET 随机数是" + i;
    }


}
