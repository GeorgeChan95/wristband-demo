package com.george.netty;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Netty服务端启动引导类
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NettyServerStart implements ApplicationRunner {


    @Value("${netty.server.port:8825}")
    private Integer serverPort; // netty服务端端口

    private final WristbandDataServer wristbandDataServer;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 启动netty服务端
        wristbandDataServer.init(serverPort);
    }
}
