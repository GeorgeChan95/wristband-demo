package com.george.config;

import com.george.netty.NettyServerInitializer;
import com.george.netty.handler.DataHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * netty服务端配置类
 */
@Configuration
public class NettyServerConfiguration {

    /**
     * netty初始化
     * @return
     */
    @Bean
    public ChannelInitializer<SocketChannel> iecClientInitializer(List<DataHandler> dataHandlerList) {
        return new NettyServerInitializer(dataHandlerList);
    }
}
