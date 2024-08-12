package com.george.netty.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.george.message.Protocol;
import com.george.model.enums.MessageTypeEnum;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @ClassName GpsDataHandler
 * @Description GPS数据解析Handler
 * @Author George
 * @Date 2024/8/12 8:51
 */
@Slf4j
@Component
public class GpsDataHandler implements DataHandler {

    private static final MessageTypeEnum messageTypeEnum = MessageTypeEnum.GPS_UPL_03;

    @Override
    public boolean matches(MessageTypeEnum messageId) {
        return false;
    }

    @Override
    public void handler(ChannelHandlerContext ctx, Protocol protocol) throws JsonProcessingException {

    }
}
