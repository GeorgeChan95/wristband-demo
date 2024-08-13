package com.george.netty.handler;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.george.message.Protocol;
import com.george.message.WristbandDataProtocol;
import com.george.model.dto.HealthyDataDto;
import com.george.model.enums.MessageTypeEnum;
import com.george.utils.NettyUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @ClassName HealthyInfoHandler
 * @Description TODO
 * @Author George
 * @Date 2024/8/13 8:46
 */
@Slf4j
@Component
public class HealthyInfoHandler implements DataHandler {
    private static final MessageTypeEnum messageTypeEnum = MessageTypeEnum.HEALTH_DATA;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean matches(MessageTypeEnum messageId) {
        return ObjectUtil.equal(messageTypeEnum, messageId);
    }

    @Override
    public void handler(ChannelHandlerContext ctx, Protocol protocol) throws JsonProcessingException {
        log.info("开始处理体征健康数据上传0x32请求......");
        Channel channel = ctx.channel(); // 当前请求的连接通道

        WristbandDataProtocol wristbandData = (WristbandDataProtocol) protocol;
        byte[] payload = wristbandData.payload();

        int index = 1; // 跳过 0x00(混合类型标识符)

        // 采集时间戳(10位,秒)
        byte[] timeBytes = ArrayUtil.reverse(NettyUtil.getByte(payload, index, 4));
        int timestamp = HexUtil.hexToInt(Convert.toHex(timeBytes));
        index += 4;

        // 体征数据有效字节长度
        String lengthCode = Convert.toHex(NettyUtil.getByte(payload, index, 2));
        byte[] lengthBytes = ArrayUtil.reverse(NettyUtil.getByte(payload, index, 2));
        int dataLength = HexUtil.hexToInt(Convert.toHex(lengthBytes));
        index += 2;

        // 校验数据有效长度与校验码是否一致
        if ((payload.length - index) != dataLength) {
            log.error("\n解析体征健康数据上传0x32请求出现异常, 体征数据校验码长度与实际长度不一致, 长度校验码: {}, 原消息有效负载为: {}\n", lengthCode, Convert.toHex(payload));
            return;
        }

        HealthyDataDto healthyData = new HealthyDataDto();
        healthyData.setTimestamp(timestamp);

        // 读取体征数据
        byte[] healthBytes = NettyUtil.getByte(payload, index, dataLength);
        int flag = 0;
        while (flag < healthBytes.length-1) {
            // 数据类型
            byte type = NettyUtil.getByte(healthBytes, flag, 1)[0];
            flag++;

            // 数据ID
            byte dataId = (byte) (type >> 3);
            // 数据值的字节长度
            int length = (((byte)(type << 5)) >> 5);
            // 数据值
            byte[] dataBytes = ArrayUtil.reverse(NettyUtil.getByte(healthBytes, flag, length));

            // 将值转成数字, 并设置到对应的属性中
            convertData(healthyData, dataId, dataBytes);
            flag += length;
        }

        String imei = NettyUtil.getImei(channel);
        healthyData.setImei(imei);

        log.info("\n解析体征健康数据如下: {}\n", objectMapper.writeValueAsString(healthyData));
    }

    /**
     *
     * @param healthyData 填充对象
     * @param dataId 数据ID
     * @param dataBytes 数据值
     */
    private void convertData(HealthyDataDto healthyData, byte dataId, byte[] dataBytes) {
        switch (dataId) {
            case 0x01: // 计步
                int step = HexUtil.hexToInt(Convert.toHex(dataBytes));
                healthyData.setStepCount(step);
                break;
            case 0x02: // 心率
                int heartRate = HexUtil.hexToInt(Convert.toHex(dataBytes));
                healthyData.setHeartRate(heartRate);
                break;
            case 0x03: // 体温
                int bodyTemp = HexUtil.hexToInt(Convert.toHex(dataBytes));
                float bodyT = BigDecimal.valueOf(bodyTemp).divide(BigDecimal.valueOf(10)).setScale(1).floatValue();
                healthyData.setBodyTemp(bodyT);
                break;
            case 0x04: // 腕温
                int wristTemp = HexUtil.hexToInt(Convert.toHex(dataBytes));
                float wristT = BigDecimal.valueOf(wristTemp).divide(BigDecimal.valueOf(10)).setScale(1).floatValue();
                healthyData.setWristTemp(wristT);
                break;
            case 0x06: // 舒张压
                int diastolic = HexUtil.hexToInt(Convert.toHex(dataBytes));
                healthyData.setDiastolicPressure(diastolic);
                break;
            case 0x07: // 收缩压
                int systolic = HexUtil.hexToInt(Convert.toHex(dataBytes));
                healthyData.setSystolicPressure(systolic);
                break;
        }
    }

    /**
     * 解析并获取步数
     * @param bytes 字节数组
     * @return
     */
    private int getStep(byte[] bytes) {

        return 0;
    }
}
