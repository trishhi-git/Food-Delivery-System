package com.example.aop;

import org.aspectj.lang.*;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class LoggingAspectTest {

    private final LoggingAspect aspect = new LoggingAspect();

    @Test
    void testBeforeAfterException() {

        JoinPoint jp = mock(JoinPoint.class);
        Signature sig = mock(Signature.class);

        when(sig.toString()).thenReturn("testMethod()");
        when(jp.getSignature()).thenReturn(sig);
        when(jp.getArgs()).thenReturn(new Object[]{"arg1"});

        aspect.logBefore(jp);
        aspect.logAfter(jp, "result");
        aspect.logException(jp, new RuntimeException("error"));

        // No assertions → coverage only
    }

    @Test
    void testAroundSuccess() throws Throwable {

        ProceedingJoinPoint pjp = mock(ProceedingJoinPoint.class);
        Signature sig = mock(Signature.class);

        when(sig.toString()).thenReturn("testMethod()");
        when(pjp.getSignature()).thenReturn(sig);
        when(pjp.proceed()).thenReturn("success");

        Object result = aspect.logExecutionTime(pjp);

        assertEquals("success", result);
        verify(pjp, times(1)).proceed();
    }

    @Test
    void testAroundException() throws Throwable {

        ProceedingJoinPoint pjp = mock(ProceedingJoinPoint.class);
        Signature sig = mock(Signature.class);

        when(sig.toString()).thenReturn("testMethod()");
        when(pjp.getSignature()).thenReturn(sig);
        when(pjp.proceed()).thenThrow(new RuntimeException("fail"));

        assertThrows(RuntimeException.class, () -> {
            aspect.logExecutionTime(pjp);
        });

        verify(pjp).proceed();
    }
}