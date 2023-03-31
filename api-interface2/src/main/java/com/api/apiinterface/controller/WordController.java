package com.api.apiinterface.controller;

import com.api.apiinterface.service.WordsService;
import com.api.common.model.entity.Words;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/word")
@Slf4j
public class WordController {
    // 每日一言的外部接口地址
    final String url = "https://v.api.aa1.cn/api/yiyan/index.php";
    @Value("${interfaces_number}")
    private String number;

//    @GetMapping()
//    public String getWordByGet(HttpServletResponse httpServletResponse) {
//        String result = null;
//        try{
//            HttpResponse res = HttpRequest.get(url)
//                    .timeout(1000)
//                    .execute();
//            if(res.getStatus()!=HttpServletResponse.SC_OK){
//                throw new Exception();
//            }
//            result = res.body();
//        }catch(Exception e){
//            httpServletResponse.setHeader("status","error");
//            return "接口网络异常，请稍后再试";
//        }
//        result = result.replace("<p>","").replace("</p>", "");
//        return "GET 每日一言:" + result+" ---->来自服务器"+number+" ";
//    }

    @Autowired
    private WordsService wordsService;

    /**
     * 使用内部数据库
     * @param
     * @return String
     */
    @GetMapping()
    public String getWordByGet() {
        Words words = wordsService.getRandom();
        String result = words.getContent();
        return "GET 每日一言:" + result+" ---->来自服务器"+number+" ";
    }

}
