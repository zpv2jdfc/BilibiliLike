package com.bilibili.service;


import com.bilibili.common.utils.JWTUtil;
import com.bilibili.common.utils.ReturnData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Slf4j
@Aspect
@Component
@Order(1)
public class AuthorizationService {
    private static final String authorization = "Authorization";


    @Pointcut("execution(* com.bilibili.controller.SubmitController.*())")
    private void point01(){}

    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    private void postMapping(){};

    @Pointcut("@annotation(com.bilibili.annotation.PerimissionAnnotation)")
    private void permissionCheck() {
    }


    @Around("permissionCheck()")
    public Object permissionCheckFirst(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        String token = request.getHeader(authorization);
        long id = JWTUtil.getUID(token);
        if (id < 0) {
            return false;
        }
        return joinPoint.proceed();
    }

//    根据bvCode获取id
    public Object modifyParameter(ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();
        Object result = pjp.proceed(args);
        return result;
    }
}
