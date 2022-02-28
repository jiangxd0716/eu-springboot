package com.eu.frame.common.config;

import cn.hutool.core.util.StrUtil;
import com.eu.frame.common.exception.GlobalException;
import com.eu.frame.common.exception.GlobalExceptionCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Jedis 连接池配置
 *
 * @author jiangxd
 */
@Slf4j
@Configuration
public class JedisConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.password}")
    private String password;

    @Value("${spring.redis.timeout}")
    private int timeout;


    /**
     * 初始化 JedisPool
     * 使用默认配置
     *
     * @return
     */
    @Bean
    public JedisPool getJedisPool() {
        try {
            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            if (StrUtil.isBlank(this.password)) {
                return new JedisPool(jedisPoolConfig, this.host, this.port, this.timeout);
            } else {
                return new JedisPool(jedisPoolConfig, this.host, this.port, this.timeout, this.password);
            }
        } catch (Throwable cause) {
            log.error("Jedis Pool 初始化失败");
            cause.printStackTrace();
            throw new GlobalException(GlobalExceptionCode.JEDIS_POOL_INIT_FAIL);
        }

    }

}
