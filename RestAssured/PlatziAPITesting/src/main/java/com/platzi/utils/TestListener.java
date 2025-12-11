package com.platzi.utils;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

    @Override
    public void onStart(ITestContext context) {
        System.out.println("========================================");
        System.out.println("Test Suite Started: " + context.getName());
        System.out.println("========================================");
    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.println("========================================");
        System.out.println("Test Suite Finished: " + context.getName());
        System.out.println("Passed: " + context.getPassedTests().size());
        System.out.println("Failed: " + context.getFailedTests().size());
        System.out.println("Skipped: " + context.getSkippedTests().size());
        System.out.println("========================================");
    }

    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("----------------------------------------");
        System.out.println("Starting Test: " + result.getMethod().getMethodName());
        System.out.println("----------------------------------------");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("✓ TEST PASSED: " + result.getMethod().getMethodName());
        System.out.println("Duration: " + (result.getEndMillis() - result.getStartMillis()) + " ms");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.err.println("✗ TEST FAILED: " + result.getMethod().getMethodName());
        System.err.println("Reason: " + result.getThrowable().getMessage());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("○ TEST SKIPPED: " + result.getMethod().getMethodName());
    }
}
