package com.huey.thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.huey.thread.config.ThreadConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


@SpringBootApplication
public class ThreadApplication {

    private ExecutorService executorService;

    @Resource
    private ThreadConfig threadConfig;

    public static void main(String[] args) {
        SpringApplication.run(ThreadApplication.class, args);
    }

    @Bean
    public ExecutorService getThreadPoolExecutor(){
        if(executorService==null){
            executorService=new ThreadPoolExecutor(threadConfig.getCoreThreadNum(),threadConfig.getMaxThreadNum(),threadConfig.getKeepLiveTime(),
                    TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
                    new ThreadFactoryBuilder().setNameFormat("xxl-job-%d").build(),new ThreadPoolExecutor.CallerRunsPolicy());
        }
        return executorService;
    }


}
