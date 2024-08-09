package com.george.netty.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.george.message.Protocol;
import com.george.model.enums.MessageTypeEnum;
import io.netty.channel.ChannelHandlerContext;

/**
 * @ClassName DataHandler
 * @Description 手环数据处理公共接口
 * @Author George
 * @Date 2024/8/8 9:44
 */
public interface DataHandler {

    /**
     * 根据消息标识符,匹配应该由哪个Handler处理数据
     * @param messageId 消息头
     * @return
     */
    boolean matches(MessageTypeEnum messageId);

    /**
     * 调用具体的DataHandler处理数据
     * @param ctx 数据通道上下文
     * @param protocol 数据对象
     */
    void handler(ChannelHandlerContext ctx, Protocol protocol) throws JsonProcessingException;
}
