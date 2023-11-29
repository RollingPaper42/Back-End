package com.strcat.aop;

import lombok.Getter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class TimeTraceAop {
    private Pair<Long, String> fetchMilliSecond() {
        return new Pair<>(System.currentTimeMillis(), "ms");
    }

    private Pair<Long, String> fetchMicroSecond() {
        return new Pair<>(System.nanoTime() / 1000, "μs");
    }

    private Pair<Long, String> fetchNanoSecond() {
        return new Pair<>(System.nanoTime(), "ns");
    }

    @Around("execution(* com.strcat.controller..*(..)) || execution(* com.strcat.service..*(..))")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        final String PREFIX = "\n-------> ";
        // 함수 호출로 원하는 단위의 시간을 얻어올 수 있습니다.
        Pair<Long, String> start = fetchMicroSecond();

        System.out.println(PREFIX + "호출 함수: " + joinPoint.toString());

        try {
            return joinPoint.proceed();
        } finally {
            // 함수 호출로 원하는 단위의 시간을 얻어올 수 있습니다.
            Pair<Long, String> end = fetchMicroSecond();
            long timeDifference = end.getKey() - start.getKey();

            System.out.println(PREFIX + joinPoint.toString() + ", 수행 시간: " + timeDifference + end.getValue());
        }
    }
}
@Getter
class Pair<K, V> {
    private final K key;
    private final V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }
}
