package com.eu.frame.common.rabbitmq;

import com.eu.frame.common.utils.GsonUtil;
import com.eu.frame.system.pojo.po.User;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class RabbitConsumer {


    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void receive(String msg, Message message, Channel channel) {
        User user = GsonUtil.getObject(msg, User.class);
        System.out.println(user);

        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            try {
                channel.basicNack(deliveryTag, false, true);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            e.printStackTrace();
        }
    }

//        @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
//    @RabbitListener(bindings = {
//            @QueueBinding(
//                    value = @Queue(value = RabbitConfig.QUEUE_NAME_1),
//                    exchange = @Exchange(value = RabbitConfig.EXCHANGE_NAME, type = "topic"),
//                    key = "route_key_msg.*"
//            )
//    })
//    public void receive2(String msg, Message message, Channel channel) {
//        System.out.println(msg + 11);
//
//        long deliveryTag = message.getMessageProperties().getDeliveryTag();
//        try {
//            channel.basicAck(deliveryTag, false);
//        } catch (IOException e) {
//            try {
//                channel.basicNack(deliveryTag, false, true);
//            } catch (IOException ioException) {
//                ioException.printStackTrace();
//            }
//            e.printStackTrace();
//        }
//    }
}
