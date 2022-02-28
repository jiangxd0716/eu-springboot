package com.eu.frame.common.aspect;

import com.eu.frame.common.thread.CurrentUser;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LogAspect {
    /**
     * 以 controller 包下定义的所有请求为切入点
     */
    @Pointcut("execution(public * com.eu.frame.system.controller..*.*(..))")
    public void webLog() {
    }

    /**
     * 在切点之前织入
     *
     * @param joinPoint
     * @throws Throwable
     */
    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {

    }

    /**
     * 在切点之后织入
     *
     * @throws Throwable
     */
    @After("webLog()")
    public void doAfter() throws Throwable {
    }

    /**
     * 环绕
     *
     * @param proceedingJoinPoint
     * @return
     * @throws Throwable
     */
    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        // 获取当前线程名称
        String currentThreadName = Thread.currentThread().getName();

        StringBuffer sbf = new StringBuffer("\n");
        // 打印请求相关参数
        sbf.append("========================================== Start ==========================================").append("\n");
        sbf.append("Currnet Thread Name     :").append(currentThreadName).append("\n");
        // 打印调用 controller 的全路径以及执行方法
        sbf.append("Class Method            :").append(proceedingJoinPoint.getSignature().getDeclaringTypeName()).append(".").append(proceedingJoinPoint.getSignature().getName()).append("\n");
        // 打印请求的 userNum
        sbf.append("CurrentUser             :").append(CurrentUser.getId()).append(":").append(CurrentUser.getUsername()).append("\n");
        // 打印请求入参
        String requestArgs = this.printRequestArgs(proceedingJoinPoint);
        sbf.append("Request Args            :").append(requestArgs).append("\n");

        Object result = proceedingJoinPoint.proceed();

        // 打印出参
        sbf.append("Response Args           :").append(new Gson().toJson(result)).append("\n");
        // 执行耗时
        sbf.append("Time-Consuming          :").append(System.currentTimeMillis() - startTime).append("ms").append("\n");
        sbf.append("=========================================== End ===========================================");

        log.info(sbf.toString());
        return result;
    }

    /**
     * 打印请求参数
     *
     * @param joinPoint
     * @return
     */
    public String printRequestArgs(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs(); // 参数值
        String[] argNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames(); // 参数名

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < argNames.length; i++) {
            sb.append(argNames[i]).append("=").append(args[i]);
            if (i + 1 < argNames.length) {
                sb.append("&");
            }
        }

        return sb.toString();
    }

}