package com.george.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;

/**
 * @ClassName UnpackHandler
 * @Description 自定义netty拆包工具
 * @Author George
 * @Date 2024/8/8 15:59
 */
public class UnpackHandler extends DelimiterBasedFrameDecoder {
    public UnpackHandler(ByteBuf buffer) {
        this(1024, buffer);
    }

    public UnpackHandler(int maxFrameLength, ByteBuf delimiter) {
        // 单条消息最大长度,
        // 对解码后的消息去掉分隔符,
        // 帧的长度将超过maxFrameLength，就会立即抛出TooLongFrameException,
        // 自定义分隔符
        super(maxFrameLength, true, true, delimiter);
    }

}
