package com.huey.thread.reentrantlock;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 描述：     演示ReentrantLock的基本用法
 */
public class LockDemo {


    public static void main(String[] args) {
        new LockDemo().init();
    }
    private ExecutorService executorService =new ThreadPoolExecutor(
            4,8,0L, TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>());

    private void init() {
        final Outputer outputer = new Outputer();
        executorService.submit(()->{
            while (true) {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                outputer.output("悟空");
            }
        });

        executorService.submit(()->{
            while (true) {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                outputer.output("大师兄");
            }
        });

    }

    static class Outputer {

        Lock lock = new ReentrantLock();

        //字符串打印方法，一个个字符的打印
        public void output(String name) {

            int len = name.length();
            lock.lock();
            try {
                for (int i = 0; i < len; i++) {
                    System.out.print(name.charAt(i));
                }
                System.out.println("");
            } finally {
                lock.unlock();
            }
        }
    }
}
