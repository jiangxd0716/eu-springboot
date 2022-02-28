package com.eu.frame;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * SpringBoot 启动类
 *
 * @author jiangxd
 */
@Slf4j
@SpringBootApplication(scanBasePackages = "com.eu")
@MapperScan(basePackages = "com.eu.frame.*.dao")
@ServletComponentScan
@EnableTransactionManagement
public class FrameApplication {

    public static void main(String[] args) {
        SpringApplication.run(FrameApplication.class, args);
        log.info("============================ TEST * System startup completed ===================================================");
    }

}
