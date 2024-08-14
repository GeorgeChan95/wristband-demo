package com.george.netty.handler;

import cn.hutool.core.convert.Convert;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName OutBoundHandler
 * @Description 输出处理器
 * @Author George
 * @Date 2024/8/14 11:17
 */
@Slf4j
public class OutBoundHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        super.read(ctx);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        int readableBytes = byteBuf.readableBytes();
        byte[] bytes = new byte[readableBytes];
        byteBuf.readBytes(bytes);

        String hex = Convert.toHex(bytes);
        log.info("\nOutBoundHandler写出数据: {}\n", hex);

        byteBuf.resetReaderIndex();
        super.write(ctx, byteBuf, promise);
    }
}
