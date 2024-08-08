package com.george;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class WristbandDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(WristbandDemoApplication.class, args);
        log.info("SwitchDemoApplication 启动成功");
    }
}
