package com.huey.thread.reentrantlock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: huey
 * @Desc: 线程预定电影院座位
 */
public class CinemaBookSeat {

    private static Lock  lock=new ReentrantLock();


    public static void main(String[] args) {
        new Thread(()->CinemaBookSeat.getSeat()).start();
        new Thread(()->CinemaBookSeat.getSeat()).start();
        new Thread(()->CinemaBookSeat.getSeat()).start();
        new Thread(()->CinemaBookSeat.getSeat()).start();
    }

    public static void getSeat(){
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName()+"开始预定");
            Thread.sleep(1000);
            System.out.println(Thread.currentThread().getName()+"完成预定");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
