package com.eu.frame.common.filter;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SQL注入过滤器
 *
 * @author CL
 */
@Slf4j
@Order(2)
@WebFilter(filterName = "SqlInjectFilter", urlPatterns = "/*")
public class SqlInjectFilter implements Filter {

    /**
     * 忽略的URL
     */
    private List<String> excludes = new ArrayList<>();


    /**
     * 初始化
     */
    @Override
    public void init(FilterConfig filterConfig) {
        log.info("------------ sqlInject filter init ------------");
    }

    /**
     * 拦截
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        // 不启用或者已忽略的URL不拦截
        if (isExcludeUrl(request.getServletPath())) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        SqlInjectHttpServletRequestWrapper sqlInjectHttpServletRequestWrapper = new SqlInjectHttpServletRequestWrapper(request);
        filterChain.doFilter(sqlInjectHttpServletRequestWrapper, servletResponse);
    }

    /**
     * 销毁
     */
    @Override
    public void destroy() {
    }

    /**
     * 判断是否为忽略的URL
     *
     * @param url URL路径
     * @return true-忽略，false-过滤
     */
    private boolean isExcludeUrl(String url) {
        if (excludes == null || excludes.isEmpty()) {
            return false;
        }
        return excludes.stream().map(pattern -> Pattern.compile(StringPool.HAT + pattern)).map(p -> p.matcher(url))
                .anyMatch(Matcher::find);
    }
}
