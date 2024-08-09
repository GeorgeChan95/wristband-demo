package com.george.netty.handler;

import com.george.model.constant.WristbandConstant;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @ClassName InBoundPreHandler
 * @Description 手环上传数据预处理Handler, 给每个消息尾部添加 [0xBD, 0xBD, 0xBD, 0xBD], 避免在后面做拆包时丢弃最后一个消息.
 * @Author George
 * @Date 2024/8/9 10:48
 */
public class InBoundPreHandler extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        ByteBuf data = in.readBytes(in.readableBytes());
        data.writeBytes(WristbandConstant.header);
        out.add(data);
    }
}
