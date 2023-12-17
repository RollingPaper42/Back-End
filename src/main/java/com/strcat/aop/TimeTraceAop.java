package com.strcat.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.springframework.data.util.Pair;

@Aspect
public class TimeTraceAop {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(TimeTraceAop.class);

    /**
     * 밀리 세컨드의 단위로 시간을 측정하는 함수입니다.
     * @return  Pair<Long, String>  시간과 단위를 반환합니다.
     */
    private Pair<Long, String> fetchMilliSecond() {
        return Pair.of(System.currentTimeMillis(), "ms");
    }

    /**
     * 마이크로 세컨드의 단위로 시간을 측정하는 함수입니다.
     * @return  Pair<Long, String>  시간과 단위를 반환합니다.
     */
    private Pair<Long, String> fetchMicroSecond() {
        return Pair.of(System.nanoTime() / 1000, "μs");
    }

    /**
     * 나노 세컨드의 단위로 시간을 측정하는 함수입니다.
     * @return  Pair<Long, String>  시간과 단위를 반환합니다.
     */
    private Pair<Long, String> fetchNanoSecond() {
        return Pair.of(System.nanoTime(), "ns");
    }

    /**
     * AOP를 이용하여 함수의 수행 시간을 측정하는 함수입니다.
     * @param joinPoint
     * @return AOP에서 Around로 실행할 함수를 반환합니다.
     * @throws Throwable
     */
    @Around("execution(* com.strcat.controller..*(..))"
            + "|| execution(* com.strcat.service..*(..))")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        final String PREFIX = "->";
        final String CALLED = "Called";
        final String BACK = "Back";
        final String FUNCTION_NAME = joinPoint.getSignature().getName() + "()";
        Pair<Long, String> start = fetchMicroSecond();

        log.info("{}{}{}", PREFIX, String.format("%-7s", CALLED), FUNCTION_NAME);

        try {
            return joinPoint.proceed();
        } finally {
            Pair<Long, String> end = fetchMicroSecond();
            long timeDifference = end.getFirst() - start.getFirst();

            log.info("{}{}{}/time: {}{}", PREFIX, String.format("%-7s", BACK)
                    , String.format("%-30s", FUNCTION_NAME), timeDifference, end.getSecond());
        }
    }
}
