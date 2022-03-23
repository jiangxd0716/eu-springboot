package com.eu;

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
@MapperScan(basePackages = "com.eu.system.*.dao")
@ServletComponentScan
@EnableTransactionManagement
public class EuApplication {

    public static void main(String[] args) {
        SpringApplication.run(EuApplication.class, args);
        log.info("============================ Eu * System startup completed ===================================================");
    }

}
