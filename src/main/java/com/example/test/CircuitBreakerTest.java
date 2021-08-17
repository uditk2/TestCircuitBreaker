package com.example.test;

import org.apache.commons.lang3.concurrent.EventCountCircuitBreaker;

import java.util.concurrent.TimeUnit;

public class CircuitBreakerTest {

    public static void main(String args[]) {
        EventCountCircuitBreaker eventCountCircuitBreaker =
                new EventCountCircuitBreaker(3, 2, TimeUnit.SECONDS, 20, 3, TimeUnit.SECONDS);
        Dependency dependency = new Dependency();
        CallerService callerService = new CallerService(eventCountCircuitBreaker, dependency);
       callerService.makeCall();
    }
}
