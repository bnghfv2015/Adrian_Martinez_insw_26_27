package com.uem;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        Calculator calculator = new Calculator();
        int result = calculator.multiply(2, 3);
        System.out.println("Result of multiplication: " + result);
    }
}