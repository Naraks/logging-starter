package ru.denko.loggingstarter.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

@Aspect
public class LogExecutionAspect {

    private static final Logger log = LoggerFactory.getLogger(LogExecutionAspect.class);

    @Around("@annotation(ru.denko.loggingstarter.annotation.LogExecutionTime)")
    public Object aroundLogExecutionTimeMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        try {
            return joinPoint.proceed();
        } catch (Exception e) {
            throw e.getCause();
        } finally {
            log.info("Время выполнения метода {}: {}", method.getName(), System.currentTimeMillis() - start);
        }

    }

}
