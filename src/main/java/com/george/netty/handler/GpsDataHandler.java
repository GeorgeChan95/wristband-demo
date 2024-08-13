package com.george.netty.handler;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.george.message.Protocol;
import com.george.message.WristbandDataProtocol;
import com.george.model.dto.WristbandGpsDto;
import com.george.model.enums.MessageTypeEnum;
import com.george.utils.NettyUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean matches(MessageTypeEnum messageId) {
        return ObjectUtil.equal(messageTypeEnum, messageId);
    }

    @Override
    public void handler(ChannelHandlerContext ctx, Protocol protocol) throws JsonProcessingException {
        log.info("开始处理手环GPS数据上传请求......");
        Channel channel = ctx.channel(); // 当前请求的连接通道

        WristbandDataProtocol wristbandData = (WristbandDataProtocol) protocol;
        byte[] payload = wristbandData.payload();

        int index = 0;
        // 经度
        byte[] lonBytes = ArrayUtil.reverse(NettyUtil.getByte(payload, index, 8));
        double lon = Double.longBitsToDouble(Long.parseLong(Convert.toHex(lonBytes), 16));
        index += 8;

        // 纬度
        byte[] latBytes = ArrayUtil.reverse(NettyUtil.getByte(payload, index, 8));
        double lat = Double.longBitsToDouble(Long.parseLong(Convert.toHex(latBytes), 16));
        index += 8;

        // 南北向
        byte[] nsBytes = NettyUtil.getByte(payload, index, 1);
        String ns = HexUtil.encodeHexStr(Convert.toHex(nsBytes));
        index++;

        // 东西向
        byte[] ewBytes = NettyUtil.getByte(payload, index, 1);
        String ew = HexUtil.encodeHexStr(Convert.toHex(ewBytes));
        index++;

        // Status =A 表示信息内容准确。可以解析 为V可以放弃
        byte[] statusBytes = NettyUtil.getByte(payload, index, 1);
        String status = HexUtil.encodeHexStr(Convert.toHex(statusBytes));
        index++;

        // 时间戳(10位:秒)
        byte[] timeHexBytes = ArrayUtil.reverse(NettyUtil.getByte(payload, index, 4));
        int timestamp = HexUtil.hexToInt(Convert.toHex(timeHexBytes));

        // 手环IMEI
        String imei = NettyUtil.getImei(channel);

        if (StringUtils.equals(status, "A")) {
            WristbandGpsDto gpsData = WristbandGpsDto.builder()
                    .imei(imei)
                    .longitude(lon)
                    .latitude(lat)
                    .northSouth(ns)
                    .eastWest(ew)
                    .timestamp(timestamp)
                    .build();
            log.info("\n接收到手环GPS定位信息, 内容如下: {}\n", objectMapper.writeValueAsString(gpsData));
        } else {
            log.info("\n接收到手环GPS定位信息, 但是标记为不准确,已忽略, longitude: {}, latitude: {}\n", lon, lat);
        }

    }
}
