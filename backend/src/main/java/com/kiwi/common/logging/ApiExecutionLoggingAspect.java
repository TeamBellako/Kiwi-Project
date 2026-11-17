package com.kiwi.common.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ApiExecutionLoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(ApiExecutionLoggingAspect.class);

    @Around("@within(org.springframework.web.bind.annotation.RestController) && " +
            "!within(com.kiwi.common.controllers.PingController)")
    public Object logControllerExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        return logExecution(joinPoint, true);
    }

    @Around("@within(org.springframework.stereotype.Service)")
    public Object logServiceExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        return logExecution(joinPoint, false);
    }

    private Object logExecution(ProceedingJoinPoint joinPoint, boolean controllerExecution) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        long startTime = System.nanoTime();

        String executionType = controllerExecution ? "Controller" : "Service";
        logger.info("{} execution started: {}.{}()", executionType, className, methodName);
        try {
            Object result = joinPoint.proceed();
            long durationMs = (System.nanoTime() - startTime) / 1_000_000;
            String resultType = result == null ? "void" : result.getClass().getSimpleName();
            logger.info("{} execution completed: {}.{}() resultType={} durationMs={}",
                    executionType, className, methodName, resultType, durationMs);
            return result;
        } catch (Throwable ex) {
            long durationMs = (System.nanoTime() - startTime) / 1_000_000;
            if (controllerExecution) {
                logger.error("Controller execution failed: {}.{}() durationMs={}",
                        className, methodName, durationMs, ex);
            } else {
                logger.error("Service execution failed: {}.{}() exception={} durationMs={}",
                        className, methodName, ex.getClass().getSimpleName(), durationMs);
            }
            throw ex;
        }
    }
}
