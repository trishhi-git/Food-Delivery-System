package com.example.aop;

import org.aspectj.lang.*;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class LoggingAspectTest {

    LoggingAspect aspect = new LoggingAspect();

    @Test
    void testAllAdvices() throws Throwable {

        JoinPoint jp = mock(JoinPoint.class);
        Signature sig = mock(Signature.class);

        when(sig.toString()).thenReturn("test()");
        when(jp.getSignature()).thenReturn(sig);
        when(jp.getArgs()).thenReturn(new Object[]{"arg"});

        aspect.logBefore(jp);
        aspect.logAfter(jp, "result");
        aspect.logException(jp, new RuntimeException("error"));

        ProceedingJoinPoint pjp = mock(ProceedingJoinPoint.class);
        when(pjp.getSignature()).thenReturn(sig);
        when(pjp.proceed()).thenReturn("ok");

        aspect.logExecutionTime(pjp);
    }
}