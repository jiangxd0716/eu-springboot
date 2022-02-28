package com.eu.frame.common.handle;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.eu.frame.common.exception.GlobalException;
import com.eu.frame.common.exception.GlobalExceptionCode;
import com.eu.frame.common.constants.RedisKey;
import com.eu.frame.common.thread.CurrentUser;
import com.eu.frame.common.utils.GsonUtil;
import com.eu.frame.common.utils.JWTUtil;
import com.eu.frame.common.utils.SignUtil;
import com.eu.frame.common.wrapper.Authority;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.ExpiredObjectListener;
import org.redisson.api.RSetCache;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Nullable;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 全局请求拦截器
 *
 * @author jiangxd
 */
@Slf4j
@Component
public class GlobalRequestHandler implements HandlerInterceptor {

    /**
     * JWT Token 在请求头中的 KEY
     */
    @Value("${jwt.key}")
    private String jwtKey;

    /**
     * JWT Token 加密签名
     */
    @Value("${jwt.sign}")
    private String jwtSign;

    /**
     * 是否启用接口授权
     */
    @Value("${security.authority}")
    private boolean isAuthority;

    /**
     * 是否启用接口签名校验
     */
    @Value("${security.sign}")
    private boolean sign;

    /**
     * 时间戳有效期
     */
    @Value("${security.tstimeout}")
    private int tstimeout;

    /**
     * 签名有效期
     */
    @Value("${security.signtimeout}")
    private int signtimeout;

    @Autowired
    private RedissonClient redissonClient;


    /**
     * 请求前置拦截
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        // 处理跨域所产生的 OPETIONS 请求
        if (request.getMethod().equals(HttpMethod.OPTIONS.name())) {
            return Boolean.TRUE;
        }

        // 从请求头中获取前端通过 header 传递过来的 token
        String jwtToken = request.getHeader(this.jwtKey);

        // 但是只要存在 TOKEN 则必须进行解析并保证 TOKEN 正常
        if (StrUtil.isNotBlank(jwtToken)) {
            JWTUtil.JWT jwt = JWTUtil.INSTANCE.check(jwtToken, this.jwtSign);
            if (jwt.getStatus() == JWTUtil.JWT.NORMAL) {     //TOKEN 解析正常

                // 配置文件开启接口授权
                if (isAuthority) {
                    // 判断类方法是否有权限注解，如果没有则跳过授权
                    Authority annotation = null;
                    if (handler instanceof HandlerMethod) {
                        annotation = ((HandlerMethod) handler).getMethodAnnotation(Authority.class);
                    }

                    // 类方法有权限注解，需要判断用户是否有该权限
                    if (null != annotation && !this.authority(annotation.mark(), jwt)) {
                        response.setStatus(HttpStatus.UNAUTHORIZED.value());
                        return Boolean.FALSE;
                    }
                }

                // 请求签名校验
                if (sign) {
                    this.vaildSign(request);
                }

                CurrentUser.init(Long.parseLong(jwt.getUserId()), jwt.getUsername());
                // 若本次请求合法，记录本次请求的各项参数及请求人
                log.info("REQUEST:[{}:{}][{}:{}]", request.getMethod(), request.getRequestURI(), CurrentUser.getId(), CurrentUser.getUsername());
                return Boolean.TRUE;
            }
        }

        // 其它情况, 如不存在 token 或 token 解析异常的均提示未授权
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        return Boolean.FALSE;

    }

    /**
     * 请求相应拦截
     * 不论接口内部是否发生异常，相应都会进入该方法
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) {
        // 请求结束，记录相应日志
        log.info("RESPONSE:[{}:{}][{}:{}]", request.getMethod(), request.getRequestURI(), CurrentUser.getId(), CurrentUser.getUsername());
        //当前请求结束需要销毁线程中存储的内容，否则线程池的作用会导致这些缓存的数据无法被虚拟机销毁
        CurrentUser.destroy();
    }

    /**
     * 判断用户是否有该接口权限
     *
     * @param mark 接口类方法的权限标识
     * @param jwt  用户权限
     * @return
     */
    private boolean authority(String mark, JWTUtil.JWT jwt) {
        // 接口权限标识
        String[] userAuthoritys = mark.split(StringPool.COMMA);

        // 从jwt中取出用户权限标识，判断用户是否有该接口的访问权限
        for (String userAuthority : userAuthoritys) {
            // 用户有权限且有该接口权限标识，则允许访问
            if (CollUtil.isNotEmpty(jwt.getAuthorityMark()) && jwt.getAuthorityMark().contains(userAuthority)) {
                return Boolean.TRUE;
            }
        }
        // 接口有权限，但是该用户没有权限 返回401未授权
        return Boolean.FALSE;
    }

    /**
     * 接口参数签名校验
     *
     * @param request
     */
    public void vaildSign(HttpServletRequest request) {

        try {
            // 参数转为key-value
            Map<String, Object> paramMap = this.getParam(request);

            // 获取客户端时间戳、并校验
            String clientTs = String.valueOf(paramMap.get("ts"));
            if (StrUtil.isBlank(clientTs)) {
                throw new GlobalException(GlobalExceptionCode.ILLEGAL_REQUEST, "请传入时间戳ts");
            }

            // 获取取客户端签名
            String clientSign = String.valueOf(paramMap.get("sign"));
            if (StrUtil.isBlank(clientSign)) {
                throw new GlobalException(GlobalExceptionCode.ILLEGAL_REQUEST, "请传入签名sign");
            }

            this.vaildTimetamp(Long.valueOf(clientTs));
            log.info("时间戳校验通过");

            // 签名校验
            log.info("客户端签名==》【{}】", clientSign);

            // 服务端签名==》参数排序->md5盐加密->转为16进制大写
            String serverSign = SignUtil.signTopRequest(paramMap, paramMap.get("salt") != null ? String.valueOf(paramMap.get("salt")) : "");
            log.info("服务端签名==》【{}】", serverSign);
            if (!serverSign.equals(clientSign)) {
                throw new GlobalException(GlobalExceptionCode.SIGN_FAILED);
            } else {

                // setCache 存储签名
                RSetCache<Object> signSet = this.redissonClient.getSetCache(RedisKey.KEY_API_SIGN);
                // 添加监听事件==》过期则删除
                signSet.addListener((ExpiredObjectListener) name -> signSet.remove(serverSign));

                // 从redis中获取签名,若存在，则说明重复请求
                if (signSet.contains(serverSign)) {
                    throw new GlobalException(GlobalExceptionCode.REPEAT_REQUEST);
                } else {
                    // 不存在，则把签名缓存到redis，且设置过期时间
                    signSet.add(serverSign, signtimeout, TimeUnit.MILLISECONDS);
                }
            }

        } catch (GlobalException e) {
            throw e;
        } catch (Exception e) {
            throw new GlobalException(GlobalExceptionCode.SIGN_FAILED);
        }

    }

    /**
     * 校验客户端时间戳是否合法
     *
     * @param timetamp 客户端时间戳
     */
    public void vaildTimetamp(Long timetamp) {
        // 服务端时间戳
        long currentTimeMillis = System.currentTimeMillis();
        // 服务端时间戳 - 客户端时间戳 》= 5分钟有效期
        log.info("服务端时间戳==》【{}】", currentTimeMillis);
        log.info("客户端时间戳==》【{}】", timetamp);
        log.info("时间差值==》【{}】s", (currentTimeMillis - timetamp) / 1000);
        if (currentTimeMillis - timetamp >= this.tstimeout || timetamp - currentTimeMillis >= this.tstimeout) {
            throw new GlobalException(GlobalExceptionCode.ILLEGAL_REQUEST);
        }
    }

    /**
     * 从body中获取请求参数
     *
     * @param request
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public Map<String, Object> getParam(HttpServletRequest request) throws IOException {
        Map<String, Object> paramMap = new HashMap<>();

        // get | delete 请求从url中获取参数
        if (HttpMethod.GET.name().equals(request.getMethod()) || HttpMethod.DELETE.name().equals(request.getMethod())) {
            // 请求参数
            String queryString = request.getQueryString();
            String[] split = queryString.split(StringPool.AMPERSAND);

            //参数转为map结构
            for (String s : split) {
                String[] paramSpilt = s.split(StringPool.EQUALS);
                paramMap.put(paramSpilt[0], paramSpilt.length == 2 ? paramSpilt[1] : "");
            }
        }

        // post | put | patch 请求从body中获取参数
        if (HttpMethod.POST.name().equals(request.getMethod()) || HttpMethod.PUT.name().equals(request.getMethod()) || HttpMethod.PATCH.name().equals(request.getMethod())) {
            // body参数转为key-value
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder responseStrBuilder = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null) {
                responseStrBuilder.append(inputStr);
            }
            paramMap = GsonUtil.getObject(responseStrBuilder.toString(), HashMap.class);
        }

        return paramMap;

    }

}
