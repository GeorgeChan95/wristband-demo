package com.george.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WristbandDataServer {

    @Autowired
    private ChannelInitializer<SocketChannel> channelInitializer;

    // CPU内核数
    public static final int cpuNum = Runtime.getRuntime().availableProcessors();

    public void init(Integer port) {

        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workGroup = new NioEventLoopGroup(2 * cpuNum);

        ServerBootstrap serverBootstrap = new ServerBootstrap().group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128) // 设置线程队列等待连接个数
                .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE) // 设置workGroup的活跃状态
                .childHandler(channelInitializer); // 通道初始化对象
        try {
            // 启动服务端并绑定端口,同时将异步改为同步
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("netty服务端启动发生异常, 异常信息: ", e);
            throw new RuntimeException(e);
        } finally {
            // 关闭通道和连接池
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
