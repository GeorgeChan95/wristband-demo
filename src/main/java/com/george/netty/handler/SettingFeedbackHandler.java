package com.george.netty.handler;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.george.message.Protocol;
import com.george.message.WristbandDataProtocol;
import com.george.model.enums.MessageTypeEnum;
import com.george.utils.NettyUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @ClassName SettingFeedbackHandler
 * @Description 下行反馈数据处理
 * @Author George
 * @Date 2024/8/13 18:24
 */
@Slf4j
@Component
public class SettingFeedbackHandler implements DataHandler {
    private static final MessageTypeEnum messageTypeEnum = MessageTypeEnum.SETTING_FEEDBACK;

    @Override
    public boolean matches(MessageTypeEnum messageId) {
        return ObjectUtil.equal(messageTypeEnum, messageId);
    }

    @Override
    public void handler(ChannelHandlerContext ctx, Protocol protocol) throws JsonProcessingException {
        log.info("开始处理手环下行设置反馈请求......");
        Channel channel = ctx.channel(); // 当前请求的连接通道
        String imei = NettyUtil.getImei(channel);

        WristbandDataProtocol wristbandData = (WristbandDataProtocol) protocol;
        byte[] payload = wristbandData.payload();
        String hex = Convert.toHex(payload);

        log.info("\n手环下行设置反馈内容如下, imei: {}, 有效负载: {}\n", imei, hex);
    }
}
