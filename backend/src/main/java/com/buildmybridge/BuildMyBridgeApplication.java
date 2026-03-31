package com.buildmybridge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * BuildMyBridge 后端主程序入口
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class BuildMyBridgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(BuildMyBridgeApplication.class, args);
    }
}
