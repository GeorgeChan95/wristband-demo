package com.george.netty;

import com.george.model.constant.WristbandConstant;
import com.george.netty.handler.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {

    private final List<DataHandler> dataHandlerList;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // 出栈处理器
        pipeline.addLast("outHandler", new OutBoundHandler());
        // 消息预处理器
        pipeline.addLast("preHandler", new InBoundPreHandler());
        // 拆包处理器
        ByteBuf delimiter = ByteBufAllocator.DEFAULT.buffer(4).writeBytes(WristbandConstant.header);
        pipeline.addLast("unpackHandler", new UnpackHandler(delimiter));
        // 解码器
        pipeline.addLast("decoder", new DataDecoder());
        // 处理器
        pipeline.addLast("dataHandler", new WristbandDataHandler(dataHandlerList));
    }
}
