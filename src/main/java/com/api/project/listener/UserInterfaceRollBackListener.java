package com.api.project.listener;

import com.api.common.config.RabbitMQConfig;
import com.api.common.vo.UserInterfaceRollBackInfoMessage;
import com.api.project.service.UserInterfaceInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.rabbitmq.client.Channel;
import javax.annotation.Resource;
import org.springframework.amqp.core.Message;

import java.io.IOException;


@Component
@Slf4j
public class UserInterfaceRollBackListener {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @RabbitListener(queues = RabbitMQConfig.ROUTING)
    public void ListenerQueue(UserInterfaceRollBackInfoMessage userInterfaceRollBackInfoMessage, Message message, Channel channel) throws IOException {
        log.info("监听到消息："+userInterfaceRollBackInfoMessage);

        Long userId = userInterfaceRollBackInfoMessage.getUserId();
        Long interfaceInfoId = userInterfaceRollBackInfoMessage.getInterfaceInfoId();
        int num = userInterfaceRollBackInfoMessage.getNum();

        boolean result = false;
        try {
            result = userInterfaceInfoService.rollBackCount(interfaceInfoId, userId, num);
        }catch (Exception e){
            log.error("接口统计次数回滚失败！！！");
            e.printStackTrace();
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true); // 如果回滚失败，消息重新放回队列排队
            return;
        }
        if (!result){
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }else {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }

    }

}
