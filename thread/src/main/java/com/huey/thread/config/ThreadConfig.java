package com.huey.thread.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: huey
 * @Desc: 线程池参数配置
 */
@ConfigurationProperties("thread.config")
@Configuration
@Data
public class ThreadConfig {

    private Integer coreThreadNum;

    private Integer maxThreadNum;

    private long keepLiveTime;

}
