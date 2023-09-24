package com.moyucoding.demo;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

@Slf4j
public class Main {


    public static void main(String[] args) {
        stackOverFlowErrorExample();
    }

    private static Thread virtualThread(String name, Runnable runnable) {
        return Thread.ofVirtual()
                .name(name)
                .start(runnable);
    }

    static Thread bathTime() {
        return virtualThread(
                "Bath time",
                () -> {
                    log("I'm going to take a bath");
                    sleep(Duration.ofMillis(500L));
                    log("I'm done with the bath");
                });
    }

    static Thread boilingWater() {
        return virtualThread(
                "Boil some water",
                () -> {
                    log("I'm going to boil some water");
                    sleep(Duration.ofSeconds(1L));
                    log("I'm done with the water");
                });
    }

    @SneakyThrows
    static void concurrentMorningRoutine() {
        var bathTime = bathTime();
        var boilingWater = boilingWater();
        bathTime.join();
        boilingWater.join();
    }

    @SneakyThrows
    static void concurrentMorningRoutineUsingExecutors() {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var bathTime =
                    executor.submit(
                            () -> {
                                log("I'm going to take a bath");
                                sleep(Duration.ofMillis(500L));
                                log("I'm done with the bath");
                            });
            var boilingWater =
                    executor.submit(
                            () -> {
                                log("I'm going to boil some water");
                                sleep(Duration.ofSeconds(1L));
                                log("I'm done with the water");
                            });
            bathTime.get();
            boilingWater.get();
        }
    }

    @SneakyThrows
    static void concurrentMorningRoutineUsingExecutorsWithName() {
        final ThreadFactory factory = Thread.ofVirtual().name("routine-", 0).factory();
        try (var executor = Executors.newThreadPerTaskExecutor(factory)) {
            var bathTime =
                    executor.submit(
                            () -> {
                                log("I'm going to take a bath");
                                sleep(Duration.ofMillis(500L));
                                log("I'm done with the bath");
                            });
            var boilingWater =
                    executor.submit(
                            () -> {
                                log("I'm going to boil some water");
                                sleep(Duration.ofSeconds(1L));
                                log("I'm done with the water");
                            });
            bathTime.get();
            boilingWater.get();
        }
    }

    static int numberOfCores() {
        return Runtime.getRuntime().availableProcessors();
    }

    static void viewCarrierThreadPoolSize() {
        final ThreadFactory factory = Thread.ofVirtual().name("routine-", 0).factory();
        try (var executor = Executors.newThreadPerTaskExecutor(factory)) {
            IntStream.range(0, numberOfCores() + 1)
                    .forEach(i -> executor.submit(() -> {
                        log("Hello, I'm a virtual thread number " + i);
                        sleep(Duration.ofSeconds(1L));
                    }));
        }
    }

    static Thread workingHard() {
        return virtualThread(
                "Working hard",
                () -> {
                    log("I'm working hard");
                    while (alwaysTrue()) {
                        // Do nothing
                    }
                    sleep(Duration.ofMillis(100L));
                    log("I'm done with working hard");
                });
    }



    static boolean alwaysTrue(){
        return true;
    }


    static Thread takeABreak() {
        return virtualThread(
                "Take a break",
                () -> {
                    log("I'm going to take a break");
                    sleep(Duration.ofSeconds(1L));
                    log("I'm done with the break");
                });
    }

    @SneakyThrows
    static void workingHardRoutine() {
        var workingHard = workingHard();
        var takeABreak = takeABreak();
        workingHard.join();
        takeABreak.join();
    }

    static Thread workingConsciousness() {
        return virtualThread(
                "Working consciousness",
        () -> {
            log("I'm working hard");
            while (alwaysTrue()) {
                sleep(Duration.ofMillis(100L));
            }
            log("I'm done with working hard");
        });
    }

    private static void log(String message){
        log.info("{} | " + message, Thread.currentThread());
    }

    @SneakyThrows
    private static void sleep(Duration duration) {
        Thread.sleep(duration);
    }

    public static void stackOverFlowErrorExample(){
        for (int i = 0; i < 5_000_00_00; i++) {
        new Thread(() -> {
            try {
                Thread.sleep(Duration.ofSeconds(60L));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
    }

    @SneakyThrows
    static void workingConsciousnessRoutine() {
        var workingConsciousness = workingConsciousness();
        var takeABreak = takeABreak();
        workingConsciousness.join();
        takeABreak.join();
    }

    static class Bathroom {
        synchronized void useTheToilet() {
            log("I'm going to use the toilet");
            sleep(Duration.ofSeconds(1L));
            log("I'm done with the toilet");
        }
    }

    static Bathroom bathroom = new Bathroom();

    static Thread goToTheToilet() {
        return virtualThread(
                "Go to the toilet",
                () -> bathroom.useTheToilet());
    }

    @SneakyThrows
    static void twoEmployeesInTheOffice() {
        var riccardo = goToTheToilet();
        var daniel = takeABreak();
        riccardo.join();
        daniel.join();
    }

    static class BathroomUseLock {
        private final Lock lock = new ReentrantLock();

        @SneakyThrows
        void useTheToiletWithLock() {
            if (lock.tryLock(10, TimeUnit.SECONDS)) {
                try {
                    log("I'm going to use the toilet");
                    sleep(Duration.ofSeconds(1L));
                    log("I'm done with the toilet");
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    static BathroomUseLock bathroomUseLock = new BathroomUseLock();

    static Thread goToTheToiletWithLock() {
        return virtualThread("Go to the toilet", () -> bathroomUseLock.useTheToiletWithLock());
    }

    @SneakyThrows
    static void twoEmployeesInTheOfficeWithLock() {
        var riccardo = goToTheToiletWithLock();
        var daniel = takeABreak();
        riccardo.join();
        daniel.join();
    }


    public static void stackOverFlowErrorExampleWithVirtualThread(){
        for(int i = 0 ; i < 1_000_000;i++){
            try {
                Thread.ofVirtual().name("virtual").start(()->{
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}