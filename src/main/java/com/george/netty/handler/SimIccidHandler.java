package com.george.netty.handler;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import com.george.message.Protocol;
import com.george.message.WristbandDataProtocol;
import com.george.model.enums.MessageTypeEnum;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @ClassName SimIccidHandler
 * @Description 手环sim iccid数据上传处理
 * @Author George
 * @Date 2024/8/8 18:02
 */
@Slf4j
@Component
public class SimIccidHandler implements DataHandler {
    private static final MessageTypeEnum messageTypeEnum = MessageTypeEnum.SIM_ICCID;

    @Override
    public boolean matches(MessageTypeEnum messageId) {
        return ObjectUtil.equal(messageTypeEnum, messageId);
    }

    @Override
    public void handler(ChannelHandlerContext ctx, Protocol protocol) {
        WristbandDataProtocol wristbandData = (WristbandDataProtocol) protocol;
        byte[] payload = wristbandData.payload();
        // 小端模式转大端模式
        payload = ArrayUtil.reverse(payload);
        String iccid = Convert.toHex(payload);
        log.info("\n手环SIM ICCID为: {}\n", iccid);
    }
}
