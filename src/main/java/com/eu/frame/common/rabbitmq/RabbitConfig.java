package com.eu.frame.common.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * rabbit mq配置文件
 *
 * @author jiangxd
 */
@Slf4j
@Component
public class RabbitConfig {

    public static final String QUEUE_NAME = "queue_msg";
    public static final String EXCHANGE_NAME = "exchange_msg";
    public static final String ROUTING_KEY = "route_key_msg";

    private final CachingConnectionFactory connectionFactory;

    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    public RabbitConfig(CachingConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());

        // confirm机制只保证消息到达exchange，不保证消息可以路由到正确的queue,如果exchange错误，就会触发confirm机制
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            log.info("消息唯一标识 correlationData = {}", correlationData);
            if (ack) {
                log.info("消息成功发送到Exchange");
            } else {
                log.info("消息发送到Exchange失败, {}", correlationData);
                log.info("失败原因 cause = {}", cause);
            }
        });

        // Return 消息机制用于处理一个不可路由的消息。
        // 在某些情况下，如果我们在发送消息的时候，当前的 exchange 不存在或者指定路由 key 路由不到，这个时候我们需要监听这种不可达的消息就需要这种return机制。
        rabbitTemplate.setReturnsCallback(returned -> {
            //  可以进行消息入库操作
            log.info("消息从Exchange路由到Queue失败");
            log.info("消息主体 message = {}", returned.getMessage());
            log.info("回复码 replyCode = {}", returned.getReplyCode());
            log.info("回复描述 replyText = {}", returned.getReplyText());
            log.info("交换机名字 exchange = {}", returned.getExchange());
            log.info("路由键 routingKey = {}", returned.getRoutingKey());
        });

        // Mandatory为true时,消息通过交换器无法匹配到队列会返回给生产者，为false时匹配不到会直接被丢弃
        rabbitTemplate.setMandatory(true);
        return rabbitTemplate;
    }

    public Queue msgQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    public DirectExchange msgExchange() {
        return new DirectExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public Binding msgBinding() {
        return BindingBuilder.bind(msgQueue()).to(msgExchange()).with(ROUTING_KEY);
    }

}
