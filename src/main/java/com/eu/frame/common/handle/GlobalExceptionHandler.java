package com.eu.frame.common.handle;

import com.eu.frame.common.exception.GlobalException;
import com.eu.frame.common.exception.GlobalExceptionCode;
import com.eu.frame.common.wrapper.GlobalResponseWrapper;
import com.eu.frame.common.wrapper.NotWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

/**
 * 全局异常拦截
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 判断该接口的方法是否需要被进场拦截处理
     *
     * @param request
     * @return
     */
    private boolean methodNotWrapper(HttpServletRequest request) {
        HandlerMethod handlerMethod = (HandlerMethod) request.getAttribute("org.springframework.web.servlet.HandlerMapping.bestMatchingHandler");
        return handlerMethod.hasMethodAnnotation(NotWrapper.class);
    }

    /**
     * 自定义异常处理
     *
     * @param request
     * @param exception
     * @return
     * @throws Exception
     */
    @ExceptionHandler(GlobalException.class)
    @ResponseBody
    public GlobalResponseWrapper customExceptionHandler(HttpServletRequest request, GlobalException exception) {
        if (this.methodNotWrapper(request)) {
            throw exception;
        }
        log.warn("CUSTOM EXCEPTION:[{}:{}]", exception.getCode().getCode(), exception.getMessage());
        return new GlobalResponseWrapper(exception.getCode()).msg(exception.getMessage());
    }

    /**
     * 注解参数异常处理
     * 将全部的异常参数拼接返回
     *
     * @param request
     * @param exception
     * @return
     * @throws Exception
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public GlobalResponseWrapper methodArgumentNotValidExceptionHandler(HttpServletRequest request, MethodArgumentNotValidException exception) throws Exception {
        if (this.methodNotWrapper(request)) {
            throw exception;
        }
        //获取全部异常
        BindingResult bindingResult = exception.getBindingResult();
        //获取第一条异常进行返回
        String message = bindingResult.getAllErrors().get(0).getDefaultMessage();

        log.warn("REQUEST PARAM EXCEPTION:[{}:{}]", message, bindingResult);
        return new GlobalResponseWrapper(GlobalExceptionCode.REQUEST_ARGUMENT_EXCEPTION).msg(message);
    }

    /**
     * 处理集合中的注解异常
     *
     * @param request
     * @param exception
     * @return
     * @throws Exception
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public GlobalResponseWrapper constraintViolationExceptionHandler(HttpServletRequest request, ConstraintViolationException exception) {
        if (this.methodNotWrapper(request)) {
            throw exception;
        }
        String message = exception.getConstraintViolations().iterator().next().getMessage();
        log.warn("REQUEST PARAM EXCEPTION:[{}]", message);
        return new GlobalResponseWrapper(GlobalExceptionCode.REQUEST_ARGUMENT_EXCEPTION).msg(message);

    }

    /**
     * 其他的异常拦截处理
     * 拦截但不处理这些异常，只记录相应的日志以追踪这些异常的发生并进行处理
     * 这些异常应该被当作实际可能发生的一种情况来进行处理，比如说前端进行一些友好的提示等
     *
     * @param request
     * @param exception
     * @return
     * @throws Exception
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public GlobalResponseWrapper defaultExceptionHandler(HttpServletRequest request, Exception exception) {
        // 404错误
        if (exception instanceof NoHandlerFoundException) {
            return new GlobalResponseWrapper(GlobalExceptionCode.NOT_FOUND).data(exception.getMessage());
        }

        if (exception instanceof HttpRequestMethodNotSupportedException) {
            return new GlobalResponseWrapper(GlobalExceptionCode.METHOD_NOT_SUPPORT).data(exception.getMessage());
        }

        // 记录异常信息
        log.error("UNKNOWN EXCEPTION:{}", this.getExceptionContent(exception));

        // 全部跑出系统异常，不对外暴露异常信息
        return new GlobalResponseWrapper(GlobalExceptionCode.ERROR).msg(exception.getMessage());
    }

    /**
     * 遍历打印异常
     *
     * @param exception
     * @return
     */
    private String getExceptionContent(Exception exception) {
        StringBuilder sb = new StringBuilder();
        sb.append(exception.getMessage()).append("\n");
        for (StackTraceElement element : exception.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }

}
