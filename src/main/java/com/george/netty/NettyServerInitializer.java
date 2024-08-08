package com.george.netty;

import com.george.model.constant.WristbandConstant;
import com.george.netty.handler.DataDecoder;
import com.george.netty.handler.DataHandler;
import com.george.netty.handler.UnpackHandler;
import com.george.netty.handler.WristbandDataHandler;
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

        // 拆包处理器
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(4);
        buffer.writeBytes(WristbandConstant.header);
        pipeline.addLast("unpackHandler", new UnpackHandler(buffer));
        // 解码器
        pipeline.addLast("decoder", new DataDecoder());
        // 处理器
        pipeline.addLast("dataHandler", new WristbandDataHandler(dataHandlerList));
    }
}
