package com.example.test;

public class Dependency {

    double throwException = 0.9;
    public String getValue() {
        double value = Math.random();
        System.out.println("value " + value);
        if (value < throwException) {
            throw new RuntimeException("Dependency Down");
        }
        return "Hello";
    }
}
