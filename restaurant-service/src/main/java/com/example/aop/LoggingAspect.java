package com.example.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    // 🔹 BEFORE method execution
    @Before("execution(* com.example..*(..))")
    public void logBefore(JoinPoint joinPoint) {
        log.info("Entering method: {} with args: {}",
                joinPoint.getSignature(),
                joinPoint.getArgs());
    }

    // 🔹 AFTER returning
    @AfterReturning(pointcut = "execution(* com.example..*(..))", returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        log.info("Method completed: {} with result: {}",
                joinPoint.getSignature(),
                result);
    }

    // 🔹 EXCEPTION logging
    @AfterThrowing(pointcut = "execution(* com.example..*(..))", throwing = "ex")
    public void logException(JoinPoint joinPoint, Exception ex) {
        log.error("Exception in method: {} message: {}",
                joinPoint.getSignature(),
                ex.getMessage());
    }

    // 🔹 EXECUTION TIME
    @Around("execution(* com.example..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long end = System.currentTimeMillis();

        log.info("Execution time of {} : {} ms",
                joinPoint.getSignature(),
                (end - start));

        return result;
    }
}