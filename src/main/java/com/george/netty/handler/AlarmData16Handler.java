package com.george.netty.handler;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.george.message.Protocol;
import com.george.message.WristbandDataProtocol;
import com.george.model.enums.MessageTypeEnum;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @ClassName AlarmData16Handler
 * @Description 手环告警, 0x16
 * 手环没有设置阈值的功能,所以没有体征告警数据
 * @Author George
 * @Date 2024/8/12 16:43
 */
@Slf4j
@Component
public class AlarmData16Handler implements DataHandler {
    private static final MessageTypeEnum messageTypeEnum = MessageTypeEnum.ALARM_DATA_16;

    @Override
    public boolean matches(MessageTypeEnum messageId) {
        return ObjectUtil.equal(messageTypeEnum, messageId);
    }

    @Override
    public void handler(ChannelHandlerContext ctx, Protocol protocol) throws JsonProcessingException {
        log.info("开始处理告警数据上传0x16请求......");
        Channel channel = ctx.channel(); // 当前请求的连接通道

        // 消息负载
        WristbandDataProtocol wristbandData = (WristbandDataProtocol) protocol;
        byte[] payload = wristbandData.payload();


    }
}
