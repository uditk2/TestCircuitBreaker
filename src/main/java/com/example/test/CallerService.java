package com.example.test;

import org.apache.commons.lang3.concurrent.EventCountCircuitBreaker;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CallerService implements Runnable {

    private EventCountCircuitBreaker eventCountCircuitBreaker;
    private Dependency dependency;
    private int open_counter = 0;
    private int closed_counter = 0;
    private boolean closed = true;
    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private long startTime = System.currentTimeMillis();
    public CallerService(EventCountCircuitBreaker eventCountCircuitBreaker,
                         Dependency dependency) {
        this.eventCountCircuitBreaker = eventCountCircuitBreaker;
        this.dependency = dependency;
    }


    private void callDependendency() {
        long timeElapsed = System.currentTimeMillis() - startTime;
        System.out.println(String.format("Time elapsed %s", timeElapsed));
        if (eventCountCircuitBreaker.checkState()) {
            if (!closed) {
                closed = true;
                open_counter = 0;
                System.out.println("Circuit back in closed state with closed_counter " + closed_counter);
                closed_counter = 0;

            }
            try {
               dependency.getValue();
            } catch (RuntimeException e) {
                eventCountCircuitBreaker.incrementAndCheckState();
                open_counter++;
                System.out.println("Breaker state " + open_counter + " closed " + closed);
            }
        } else {
            open_counter++;
            closed_counter++;
            closed = false;
            System.out.println("Open state with breaker " + open_counter + " and closed " + closed);
        }
    }

    public void makeCall() {
        executorService.scheduleWithFixedDelay(this, 1, 500, TimeUnit.MILLISECONDS);
    }

    public void run() {
        callDependendency();
    }
}
