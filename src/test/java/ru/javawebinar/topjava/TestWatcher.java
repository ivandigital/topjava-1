package ru.javawebinar.topjava;

import org.junit.AssumptionViolatedException;
import org.junit.runner.Description;

import java.util.HashMap;
import java.util.Map;

public class TestWatcher extends org.junit.rules.TestWatcher {

    private static int cntTestsTotal = 0;
    private static int cntTestsOK = 0;
    private static int cntTestsFailed = 0;

    private static Map<String, Long> testsTiming = new HashMap<>();

    private long startTime;

    @Override
    protected void starting(Description description) {
//        System.out.println(">>> Starting...");
        cntTestsTotal ++;
        super.starting(description);
        startTime = System.nanoTime();
    }

    @Override
    protected void finished(Description description) {
        long endTime = System.nanoTime();
        long timing = (endTime - startTime) / 1000 / 1000;
        String testName = description.getDisplayName();
        testsTiming.put(testName, timing);
        System.out.println(">>> Test " + testName + " finished in " + timing + " ms");
        super.finished(description);
    }

    @Override
    protected void failed(Throwable e, Description description) {
//        System.out.println(">>> Failed !");
        super.failed(e, description);
        cntTestsFailed ++;
    }

    @Override
    protected void skipped(AssumptionViolatedException e, Description description) {
//        System.out.println(">>> Skipped.");
        super.skipped(e, description);
    }

    @Override
    protected void succeeded(Description description) {
//        System.out.println(">>> Succeded.");
        super.succeeded(description);
        cntTestsOK ++;
    }

    @Override
    protected void finalize() throws Throwable {
//        System.out.println(">>> Finalize");
        super.finalize();
    }

    public static int getTestsTotal() {
        return cntTestsTotal;
    }

    public static int getTestsOK() {
        return cntTestsOK;
    }

    public static int getTestsFailed() {
        return cntTestsFailed;
    }

    public static Map<String,Long> getTestsTiming() {
        return testsTiming;
    }

}
