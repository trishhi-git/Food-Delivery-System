package com.example.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class LoggingAspectTest {

    private final LoggingAspect loggingAspect = new LoggingAspect();

    @Test
    void testLogBefore() {
        JoinPoint joinPoint = mock(JoinPoint.class);
        Signature signature = mock(Signature.class);

        when(signature.toString()).thenReturn("testMethod()");
        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getArgs()).thenReturn(new Object[]{"arg1", 123});

        loggingAspect.logBefore(joinPoint);
    }

    @Test
    void testLogAfter() {
        JoinPoint joinPoint = mock(JoinPoint.class);
        Signature signature = mock(Signature.class);

        when(signature.toString()).thenReturn("testMethod()");
        when(joinPoint.getSignature()).thenReturn(signature);

        loggingAspect.logAfter(joinPoint, "result");
    }

    @Test
    void testLogException() {
        JoinPoint joinPoint = mock(JoinPoint.class);
        Signature signature = mock(Signature.class);

        when(signature.toString()).thenReturn("testMethod()");
        when(joinPoint.getSignature()).thenReturn(signature);

        Exception ex = new RuntimeException("Test exception");

        loggingAspect.logException(joinPoint, ex);
    }

    @Test
    void testLogExecutionTime() throws Throwable {
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        Signature signature = mock(Signature.class);

        when(signature.toString()).thenReturn("testMethod()");
        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.proceed()).thenReturn("success");

        Object result = loggingAspect.logExecutionTime(joinPoint);

        assert result.equals("success");
        verify(joinPoint, times(1)).proceed();
    }

    @Test
    void testLogExecutionTimeThrowsException() throws Throwable {
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        Signature signature = mock(Signature.class);

        when(signature.toString()).thenReturn("testMethod()");
        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.proceed()).thenThrow(new RuntimeException("Error"));

        try {
            loggingAspect.logExecutionTime(joinPoint);
        } catch (Exception e) {
            assert e.getMessage().equals("Error");
        }

        verify(joinPoint, times(1)).proceed();
    }
}