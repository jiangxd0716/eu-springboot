package com.eu.frame.common.filter;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Xss攻击拦截器
 *
 * @author jiangxd
 */
@Slf4j
@Order(1)
@WebFilter(filterName = "xssFilter", urlPatterns = "/*")
public class XssFilter implements Filter {

    // 是否过滤富文本内容
    private boolean flag = false;

    private List<String> excludes = new ArrayList<>();


    @Override
    public void init(FilterConfig filterConfig) {
        log.info("------------ xss filter init ------------");

        String isIncludeRichText = filterConfig.getInitParameter("isIncludeRichText");
        if (StringUtils.isNotBlank(isIncludeRichText)) {
            flag = BooleanUtils.toBoolean(isIncludeRichText);
        }
        String temp = filterConfig.getInitParameter("excludes");
        if (temp != null) {
            String[] url = temp.split(",");
            excludes.addAll(Arrays.asList(url));
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;

        if (isExcludeUrl(req.getServletPath())) {
            chain.doFilter(request, response);
            return;
        }

        XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper((HttpServletRequest) request, flag);
        chain.doFilter(xssRequest, response);

    }

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
