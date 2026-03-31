package com.buildmybridge;

import org.mybatis.spring.annotation.MapperScan;
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
@MapperScan("com.buildmybridge.mapper")
public class BuildMyBridgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(BuildMyBridgeApplication.class, args);
    }
}
