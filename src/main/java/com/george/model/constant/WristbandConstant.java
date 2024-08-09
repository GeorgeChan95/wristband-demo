package com.george.model.constant;

import com.google.common.collect.Maps;
import io.netty.channel.Channel;

import java.util.Map;

/**
 * @ClassName WristbandConstant
 * @Description 常量
 * @Author George
 * @Date 2024/8/8 16:02
 */
public interface WristbandConstant {

    /**
     * 手环上传消息的消息头
     * BDBDBDBD
     */
    byte[] header = new byte[]{(byte) 0xbd, (byte) 0xbd, (byte) 0xbd, (byte) 0xbd};

    /**
     * 手环心跳回复
     * BDBDBDF301
     */
    byte[] heartbeatReply = new byte[]{(byte) 0xbd, (byte) 0xbd, (byte) 0xbd, (byte) 0xbd, (byte) 0xf3, 0x01};

    Map<String, Channel> channelMap = Maps.newConcurrentMap();
}
