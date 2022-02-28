package com.eu.frame.common.config;

import com.eu.frame.common.handle.GlobalRequestHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * Web 配置
 *
 * @author Jxd
 */
@Slf4j
@Configuration
public class WebAppConfig extends WebMvcConfigurationSupport {

    private final GlobalRequestHandler globalRequestHandler;


    public WebAppConfig(GlobalRequestHandler globalRequestHandler) {
        this.globalRequestHandler = globalRequestHandler;
    }

    /**
     * 注册拦截器
     *
     * @param registry
     */
    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry
                .addInterceptor(this.globalRequestHandler)
                .addPathPatterns("/**")
                .excludePathPatterns("/login", "/register", "/upload", "/captcha", "/actuator/**");
    }

    /**
     * 配置允许跨域访问
     *
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedHeaders("*")
                .allowedMethods("*")
                .maxAge(3600)
                .allowCredentials(true);
    }

}
