package com.george.netty.handler;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.george.message.Protocol;
import com.george.message.WristbandDataProtocol;
import com.george.model.enums.MessageTypeEnum;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @ClassName CellInfoHandler
 * @Description wifi和基站信息上传
 * @Author George
 * @Date 2024/8/9 13:16
 */
@Slf4j
@Component
public class CellInfoHandler implements DataHandler{
    private static final MessageTypeEnum messageTypeEnum = MessageTypeEnum.CELL_INFO_A4;

    @Override
    public boolean matches(MessageTypeEnum messageId) {
        return ObjectUtil.equal(messageTypeEnum, messageId);
    }

    @Override
    public void handler(ChannelHandlerContext ctx, Protocol protocol) throws JsonProcessingException {
        log.info("开始处理手环wifi和基站信息上传请求......");
        WristbandDataProtocol wristbandData = (WristbandDataProtocol) protocol;
        byte[] payload = wristbandData.payload();
        String hex = Convert.toHex(payload);
        log.info("\nwifi和基站信息上传信息有效负载: {}\n", hex);
    }
}
