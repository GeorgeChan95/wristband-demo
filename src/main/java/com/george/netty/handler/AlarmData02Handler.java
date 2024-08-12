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
 * @ClassName AlarmData02Handler
 * @Description 告警数据上传
 * @Author George
 * @Date 2024/8/12 10:24
 */
@Slf4j
@Component
public class AlarmData02Handler implements DataHandler {
    private static final MessageTypeEnum messageTypeEnum = MessageTypeEnum.ALARM_DATA_02;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean matches(MessageTypeEnum messageId) {
        return ObjectUtil.equal(messageTypeEnum, messageId);
    }

    @Override
    public void handler(ChannelHandlerContext ctx, Protocol protocol) throws JsonProcessingException {
        log.info("开始处理告警数据上传0x02请求......");
        Channel channel = ctx.channel(); // 当前请求的连接通道

        // 消息负载
        WristbandDataProtocol wristbandData = (WristbandDataProtocol) protocol;
        byte[] payload = wristbandData.payload();

        // 告警值
        byte[] errorHexBytes = ArrayUtil.reverse(NettyUtil.getByte(payload, 0, 2));
        int errorInt = HexUtil.hexToInt(Convert.toHex(errorHexBytes));

        // 告警时间
        byte[] timeHexBytes = ArrayUtil.reverse(NettyUtil.getByte(payload, 2, payload.length - 2));
        int timestamp = HexUtil.hexToInt(Convert.toHex(timeHexBytes));

        // 告警数据集
        List<WristbandAlarmDataDto> dataList = Lists.newArrayList();
        if (ObjectUtil.equal(errorInt & WristbandAlarmEnum.LOW_BATTERY.getValue(), WristbandAlarmEnum.LOW_BATTERY.getValue())) { // 低电量告警
            dataList.add(convertAlarmData(channel, WristbandAlarmEnum.LOW_BATTERY.getAlarmType(), null, timestamp));
        }
        if (ObjectUtil.equal(errorInt & WristbandAlarmEnum.SOS_START.getValue(), WristbandAlarmEnum.SOS_START.getValue())) { // 发起SOS告警
            dataList.add(convertAlarmData(channel, WristbandAlarmEnum.SOS_START.getAlarmType(), null, timestamp));
        }
        if (ObjectUtil.equal(errorInt & WristbandAlarmEnum.SHUT_DOWN.getValue(), WristbandAlarmEnum.SHUT_DOWN.getValue())) { // 关机告警
            dataList.add(convertAlarmData(channel, WristbandAlarmEnum.SHUT_DOWN.getAlarmType(), null, timestamp));
        }
        if (ObjectUtil.equal(errorInt & WristbandAlarmEnum.OPEN_BOX.getValue(), WristbandAlarmEnum.OPEN_BOX.getValue())) { // 开箱告警
            dataList.add(convertAlarmData(channel, WristbandAlarmEnum.OPEN_BOX.getAlarmType(), null, timestamp));
        }
        if (ObjectUtil.equal(errorInt & WristbandAlarmEnum.CAST_OFF.getValue(), WristbandAlarmEnum.CAST_OFF.getValue())) { // 摘掉告警
            dataList.add(convertAlarmData(channel, WristbandAlarmEnum.CAST_OFF.getAlarmType(), null, timestamp));
        }
        if (ObjectUtil.equal(errorInt & WristbandAlarmEnum.SITTING_LONG_TIME.getValue(), WristbandAlarmEnum.SITTING_LONG_TIME.getValue())) { // 久坐告警
            dataList.add(convertAlarmData(channel, WristbandAlarmEnum.SITTING_LONG_TIME.getAlarmType(), null, timestamp));
        }
        if (ObjectUtil.equal(errorInt & WristbandAlarmEnum.SHOCK.getValue(), WristbandAlarmEnum.SHOCK.getValue())) { // 震动告警
            dataList.add(convertAlarmData(channel, WristbandAlarmEnum.SHOCK.getAlarmType(), null, timestamp));
        }
        if (ObjectUtil.equal(errorInt & WristbandAlarmEnum.SOS_CANCEL.getValue(), WristbandAlarmEnum.SOS_CANCEL.getValue())) { // SOS取消
            dataList.add(convertAlarmData(channel, WristbandAlarmEnum.SOS_CANCEL.getAlarmType(), null, timestamp));
        }
        if (ObjectUtil.equal(errorInt & WristbandAlarmEnum.WEAR.getValue(), WristbandAlarmEnum.WEAR.getValue())) { // 设备佩戴
            dataList.add(convertAlarmData(channel, WristbandAlarmEnum.WEAR.getAlarmType(), null, timestamp));
        }

        log.info("\n接收到手环0x02告警数据: {}\n", objectMapper.writeValueAsString(dataList));
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
