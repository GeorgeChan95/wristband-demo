package com.george.netty.handler;

import cn.hutool.core.convert.Convert;
import com.george.model.constant.WristbandConstant;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @ClassName InBoundPreHandler
 * @Description 手环上传数据预处理Handler, 给每个消息尾部添加 [0xBD, 0xBD, 0xBD, 0xBD], 避免在后面做拆包时丢弃最后一个消息.
 * @Author George
 * @Date 2024/8/9 10:48
 */
@Slf4j
public class InBoundPreHandler extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte[] bytes = new byte[in.readableBytes()];
        in.readBytes(bytes);
        String hex = Convert.toHex(bytes);
        log.info("\nInBoundPreHandler 接收到消息: {}\n", hex);

        in.resetReaderIndex();
        ByteBuf data = in.readBytes(in.readableBytes());
        data.writeBytes(WristbandConstant.header);
        out.add(data);
    }
}
