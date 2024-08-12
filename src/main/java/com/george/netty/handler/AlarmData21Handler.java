package com.george.netty.handler;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.george.message.Protocol;
import com.george.message.WristbandDataProtocol;
import com.george.model.dto.WristbandAlarmDataDto;
import com.george.model.enums.MessageTypeEnum;
import com.george.model.enums.WristbandAlarmEnum;
import com.george.utils.NettyUtil;
import com.google.common.collect.Lists;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName AlarmData21Handler
 * @Description 手环告警数据上传 0x21
 * @Author George
 * @Date 2024/8/12 17:16
 */
@Slf4j
@Component
public class AlarmData21Handler implements DataHandler {
    private static final MessageTypeEnum messageTypeEnum = MessageTypeEnum.ALARM_DATA_21;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean matches(MessageTypeEnum messageId) {
        return ObjectUtil.equal(messageTypeEnum, messageId);
    }

    @Override
    public void handler(ChannelHandlerContext ctx, Protocol protocol) throws JsonProcessingException {
        log.info("开始处理告警数据上传0x21请求......");
        Channel channel = ctx.channel(); // 当前请求的连接通道

        // 消息负载
        WristbandDataProtocol wristbandData = (WristbandDataProtocol) protocol;
        byte[] payload = wristbandData.payload();

        // 告警类型
        byte[] typeHexBytes = ArrayUtil.reverse(NettyUtil.getByte(payload, 0, 2));
        int alarmType = HexUtil.hexToInt(Convert.toHex(typeHexBytes));

        // 告警标识
        byte[] errorHexBytes = ArrayUtil.reverse(NettyUtil.getByte(payload, 2, 4));
        int errorInt = HexUtil.hexToInt(Convert.toHex(errorHexBytes));

        // 告警时间
        byte[] timeHexBytes = ArrayUtil.reverse(NettyUtil.getByte(payload, 6, 4));
        int timestamp = HexUtil.hexToInt(Convert.toHex(timeHexBytes));

        // 告警数据集
        List<WristbandAlarmDataDto> dataList = Lists.newArrayList();
        if (ObjectUtil.equal(errorInt & WristbandAlarmEnum.CHARGING_SHUT_DOWN.getValue(), WristbandAlarmEnum.CHARGING_SHUT_DOWN.getValue())) { // 设备充电中，已关机
            dataList.add(convertAlarmData(channel, WristbandAlarmEnum.CHARGING_SHUT_DOWN.getAlarmType(), null, timestamp));
        }
        if (ObjectUtil.equal(errorInt & WristbandAlarmEnum.LOW_BATTERY_SHUT_DOWN.getValue(), WristbandAlarmEnum.LOW_BATTERY_SHUT_DOWN.getValue())) { // 设备电量低，已关机
            dataList.add(convertAlarmData(channel, WristbandAlarmEnum.LOW_BATTERY_SHUT_DOWN.getAlarmType(), null, timestamp));
        }
        if (ObjectUtil.equal(errorInt & WristbandAlarmEnum.PROACTIVELY_SHUT_DOWN.getValue(), WristbandAlarmEnum.PROACTIVELY_SHUT_DOWN.getValue())) { // 设备主动关机
            dataList.add(convertAlarmData(channel, WristbandAlarmEnum.PROACTIVELY_SHUT_DOWN.getAlarmType(), null, timestamp));
        }

        log.info("\n接收到手环0x21告警数据: {}\n", objectMapper.writeValueAsString(dataList));
    }

    /**
     * 手环告警数据组装
     * @param channel 通道号
     * @param alarmType 告警类型
     * @param value 告警值
     * @param timestamp 时间戳(10位秒)
     * @return
     */
    private WristbandAlarmDataDto convertAlarmData(Channel channel, String alarmType, String value, int timestamp) {
        // 手环设备号
        String imei = NettyUtil.getImei(channel);
        WristbandAlarmDataDto alarmData = WristbandAlarmDataDto.builder().imei(imei).alarmType(alarmType).timestamp(timestamp).build();
        return alarmData;
    }
}
