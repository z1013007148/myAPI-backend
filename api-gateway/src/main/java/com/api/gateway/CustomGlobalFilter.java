package com.api.gateway;

import com.api.common.config.RabbitMQConfig;
import com.api.common.model.dto.RequestParamsField;
import com.api.common.model.entity.InterfaceInfo;
import com.api.common.model.entity.User;
import com.api.common.service.InnerInterfaceInfoService;
import com.api.common.service.InnerUserInterfaceInfoService;
import com.api.common.service.InnerUserService;
import cn.api.sdk.utils.SignUtils;
import com.api.common.vo.UserInterfaceRollBackInfoMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * 全局过滤
 */
@Slf4j
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    @DubboReference
    private InnerUserService innerUserService;

    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;

    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RabbitTemplate rabbitTemplate;

    private static final List<String> IP_WHITE_LIST = Arrays.asList("127.0.0.1");

    private static final String INTERFACE_HOST = "http://localhost:8123";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 请求日志
        ServerHttpRequest request = exchange.getRequest();
        // interface和网关在一个内网，就不要前面的地址了
        String path = request.getPath().value();
        String method = request.getMethod().toString();
        log.info("请求唯一标识：" + request.getId());
        log.info("请求路径：" + path);
        log.info("请求方法：" + method);
        log.info("请求参数：" + request.getQueryParams());
        String sourceAddress = request.getLocalAddress().getHostString();
        log.info("请求来源地址：" + sourceAddress);
        log.info("请求来源地址：" + request.getRemoteAddress());
        ServerHttpResponse response = exchange.getResponse();
        
        // 2. 限流
        Boolean rateLimit = stringRedisTemplate.opsForValue().setIfAbsent(path, "1", 1, TimeUnit.SECONDS);
        if(Boolean.FALSE.equals(rateLimit)){
            log.info("限流，拒绝访问 "+path);
            return handleRateLimit(response);
        }
        // 3. 访问控制 - 黑白名单
        if (!IP_WHITE_LIST.contains(sourceAddress)) {
            return handleNoAuth(response);
        }

        // 4. 用户鉴权（判断 ak、sk 是否合法）
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");
        String body = headers.getFirst("body");

        // 5. 去数据库中查是否已分配给用户
        User invokeUser = null;
        try {
            invokeUser = innerUserService.getInvokeUser(accessKey);
        } catch (Exception e) {
            log.error("getInvokeUser error", e);
        }
        if (invokeUser == null) {
            return handleNoAuth(response);
        }

        // 6. 验证apiClient的随机数nonce（最长为4的）,并用Redis防重放
        assert nonce != null;
        Boolean success = stringRedisTemplate.opsForValue().setIfAbsent(nonce, "1", 5, TimeUnit.MINUTES);
        if (Long.parseLong(nonce) > 10000L || Boolean.FALSE.equals(success)) {
            return handleNoAuth(response);
        }

        // 7. 时间和当前时间不能超过 5 分钟
        long currentTime = System.currentTimeMillis() / 1000;
        final long FIVE_MINUTES = 60 * 5L;
        if (timestamp != null && (currentTime - Long.parseLong(timestamp)) >= FIVE_MINUTES) {
            return handleNoAuth(response);
        }

        // 8. 从数据库中查出 secretKey
        String secretKey = invokeUser.getSecretKey();
        String serverSign = SignUtils.genSign(body, secretKey);
        if (sign == null || !sign.equals(serverSign)) {
            return handleNoAuth(response);
        }

        // 9. 查询数据库，模拟接口是否存在，以及请求方法是否匹配
        InterfaceInfo interfaceInfo = null;
        try {
            log.info("path:" + path);
            log.info("method:" + method);
            interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(path, method);
        } catch (Exception e) {
            log.error("getInterfaceInfo error", e);
        }

        log.info("过滤完成,准备处理响应,查询到的用户信息:" + invokeUser);
        log.info("过滤完成,准备处理响应,查询到的接口信息:" + interfaceInfo);

        if (interfaceInfo == null) {
            return handleNoAuth(response);
        }

        // 10. 参数校验
        String requestParams = interfaceInfo.getRequestParams();
        MultiValueMap<String, String> queryParams = request.getQueryParams();

        List<RequestParamsField> requestParamFieldList = new Gson().fromJson(requestParams, new Gson().fromJson(requestParams, new TypeToken<List<RequestParamsField>>() {
        }.getType()));
        for(RequestParamsField requestParamField:requestParamFieldList){
            if("true".equals(requestParamField.getRequired())){
                if(StringUtils.isBlank(queryParams.getFirst(requestParamField.getFieldName())) || !queryParams.containsKey(requestParamField.getFieldName())){
                    return handleNoAuth(response);
                }
            }
        }

        // 11. 调用计数
        Integer leftNum;
        try{
            // 原子操作，查接口调用次数和调用放在一个事务里，保证一致性
            leftNum = innerUserInterfaceInfoService.invokeCount(interfaceInfo.getId(), invokeUser.getId());
            // 调用成功，接口调用次数 + 1 invokeCount
        }catch (Exception e){
            log.error("调用失败", e);
            e.printStackTrace();
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            return response.setComplete();
        }

        if(null == leftNum){
            return handleNoAuth(response);
        }

        // 12. 请求转发，调用模拟接口 + 响应日志 + 回滚
        return handleResponse(exchange, chain, interfaceInfo.getId(), invokeUser.getId(), leftNum);
    }

    /**
     * 处理响应
     */
    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, long interfaceInfoId, long userId, int leftNum) {
        try {
            ServerHttpResponse originalResponse = exchange.getResponse();
            // 缓存数据的工厂
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            // 拿到响应码
            HttpStatus statusCode = originalResponse.getStatusCode();
            // 装饰，增强能力
            ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                // 等调用完转发的接口后才会执行
                @Override
                public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
//                    log.info("body instanceof Flux: {}", (body instanceof Flux));
                    if (body instanceof Flux) {
                        Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                        // 往返回值里写数据
                        // 拼接字符串
                        boolean success = (statusCode == HttpStatus.OK) && (null == originalResponse.getHeaders().get("status"));
                        return super.writeWith(fluxBody.map(dataBuffer -> {
                            byte[] content = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(content);
                            DataBufferUtils.release(dataBuffer);//释放掉内存
                            String additionString;

                            if (success) {
                                // 调用成功
                                additionString = new String(content, StandardCharsets.UTF_8) + " ---->剩余调用次数: " + leftNum;
                            } else {
                                // 调用失败，使用消息队列实现接口统计次数回滚，消息队列可靠性较高
                                log.info("回滚信息放入消息队列");
                                UserInterfaceRollBackInfoMessage vo = new UserInterfaceRollBackInfoMessage(userId, interfaceInfoId,1);
                                rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING, vo);
                                additionString = new String(content, StandardCharsets.UTF_8) + " ---->调用失败，未扣次数，剩余调用次数: " + (leftNum+1);
                            }

                            byte[] newContent = additionString.getBytes(StandardCharsets.UTF_8);

                            // 构建日志
//                                        StringBuilder sb2 = new StringBuilder(200);
//                                        List<Object> rspArgs = new ArrayList<>();
//                                        rspArgs.add(originalResponse.getStatusCode());
//                                        String data = new String(content, StandardCharsets.UTF_8); //data
//                                        sb2.append(data);
                            // 打印日志
                            log.info("响应结果：" + additionString);
                            // 更新body长度
                            originalResponse.getHeaders().setContentLength(newContent.length);
                            return bufferFactory.wrap(newContent);
                        }));
                    } else {
                        // 8. 调用失败，返回一个规范的错误码
                        log.error("<--- {} 响应code异常", getStatusCode());
                    }
                    return super.writeWith(body);
                }
            };
            // 设置 response 对象为装饰过的
            return chain.filter(exchange.mutate().response(decoratedResponse).build());

//            return chain.filter(exchange); // 降级处理返回数据
        } catch (Exception e) {
            log.error("网关处理响应异常" + e);
            return chain.filter(exchange);
        }
    }

    @Override
    public int getOrder() {
        return -2;
    }

    public Mono<Void> handleRateLimit(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        return response.setComplete();
    }

    public Mono<Void> handleNoAuth(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    public Mono<Void> handleInvokeError(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }
}