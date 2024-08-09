package com.george.netty.handler;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.george.message.Protocol;
import com.george.message.WristbandDataProtocol;
import com.george.model.constant.WristbandConstant;
import com.george.model.enums.MessageTypeEnum;
import com.george.utils.NettyUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.Map;

import static com.george.model.constant.WristbandConstant.channelMap;

/**
 * @ClassName HeartbeatUpHandler
 * @Description 新心跳包协议数据解析
 * @Author George
 * @Date 2024/8/9 13:33
 */
@Slf4j
@Component
public class HeartbeatUpHandler implements DataHandler{

    private static final MessageTypeEnum messageTypeEnum = MessageTypeEnum.HEARTBEAT_UP;

    @Override
    public boolean matches(MessageTypeEnum messageId) {
        return ObjectUtil.equal(messageTypeEnum, messageId);
    }

    @Override
    public void handler(ChannelHandlerContext ctx, Protocol protocol) throws JsonProcessingException {
        log.info("开始处理手环心跳请求......");
        WristbandDataProtocol wristbandData = (WristbandDataProtocol) protocol;
        byte[] payload = wristbandData.payload();

        int index = 0;
        // 电量类型
        int batType = getBatType(payload, index);
        index++;

        // 电量值
        float batVolt = getBatVolt(payload, index, batType);
        index += 2;

        // 信号类型
        int signalType = getSignalType(payload, index);
        index++;

        // 信号强度
        float signalStrength = getSignalStrength(payload, index, signalType);
        index += 2;

        // 扩展类型
        int otherType = getOtherType(payload, index);
        index++;

        // 扩展类型值
        String otherData = getOtherData(payload, index, otherType);
        index += 4;

        // utc时间戳
        int timestamp = getTimestamp(payload, index);

        String imei = "";
        Iterator<String> iterator = channelMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            boolean flag = channelMap.get(key).equals(ctx.channel());
            if (flag) {
                imei = key;
            }
        }
        log.info("\n手环心跳信息如下, 手环编码:{},\t 电量值: {},\t信号强度: {},\t 时间戳: {}\n", imei, batType, signalStrength, timestamp);
    }

    /**
     * 获取电量类型
     * @param payload 有效负载
     * @param start 从payload中开始读取的字节索引
     * @return
     */
    private int getBatType(byte[] payload, int start) {
        byte[] bytes = NettyUtil.getByte(payload, start, 1);
        String hex = Convert.toHex(bytes);
        int value = HexUtil.hexToInt(hex);
        return value;
    }

    /**
     * 获取电量值
     * @param payload 有效负载
     * @param start 从payload中开始读取的字节索引
     * @param batType 电量类型:  0：4级制  1：5级制  2：百分比  3：电压值
     * @return
     */
    private float getBatVolt(byte[] payload, int start, int batType) {
        byte[] bytes = NettyUtil.getByte(payload, start, 2);
        String hex = Convert.toHex(ArrayUtil.reverse(bytes));
        int value = HexUtil.hexToInt(hex);

        float batVolt = 0f; // 电量值
        if (batType == 0) { // 4级制
            switch (value) {
                case 0:
                    batVolt = 0.25f;
                    break;
                case 1:
                    batVolt = 0.5f;
                    break;
                case 2:
                    batVolt = 0.75f;
                    break;
                case 3:
                    batVolt = 1.0f;
                    break;
            }
        } else if (batType == 1) { // 5级制
            switch (value) {
                case 0:
                    batVolt = 0.2f;
                    break;
                case 1:
                    batVolt = 0.4f;
                    break;
                case 2:
                    batVolt = 0.6f;
                    break;
                case 3:
                    batVolt = 0.8f;
                    break;
                case 4:
                    batVolt = 1.0f;
                    break;
            }
        } else if (batType == 2) { // 百分比
            batVolt = Float.valueOf(value);
        }
        return batVolt;
    }

    /**
     * 获取信号类型
     * @param payload 有效负载
     * @param start 从payload中开始读取的字节索引
     * @return
     */
    private int getSignalType(byte[] payload, int start) {
        byte[] bytes = NettyUtil.getByte(payload, start, 1);
        String hex = Convert.toHex(bytes);
        int type = HexUtil.hexToInt(hex);
        return type;
    }

    /**
     * 获取信号强度
     * @param payload 有效负载
     * @param start 从payload中开始读取的字节索引
     * @param signalType 信号类型,  0：百分比  1：5级制   2：CSQ值
     * @return
     */
    private float getSignalStrength(byte[] payload, int start, int signalType) {
        byte[] bytes = NettyUtil.getByte(payload, start, 2);
        String hex = Convert.toHex(ArrayUtil.reverse(bytes));
        int value = HexUtil.hexToInt(hex);

        float signalStrength = 0.0f;
        if (signalType == 0) { // 百分比
            signalStrength = Float.valueOf(value);
        } else if (signalType == 1) { // 5级制
            switch (value) {
                case 0:
                    break;
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    break;
            }
        } else if (signalType == 2) { // CSQ值
            System.out.println(11);
            log.info("手环信号强度CSQ值: {}", value);
        }
        return signalStrength;
    }

    /**
     * 获取手环扩展类型
     * @param payload 有效负载
     * @param start 从payload中开始读取的字节索引
     * @return
     */
    private int getOtherType(byte[] payload, int start) {
        byte[] bytes = NettyUtil.getByte(payload, start, 1);
        String hex = Convert.toHex(bytes);
        int type = HexUtil.hexToInt(hex);
        return type;
    }

    /**
     * 获取手环扩展类型的值
     * @param payload 有效负载
     * @param start 从payload中开始读取的字节索引
     * @param otherType 手环扩展类型, 0：全量记步   1：增量记步   2：震动
     * @return
     */
    private String getOtherData(byte[] payload, int start, int otherType) {
        byte[] bytes = NettyUtil.getByte(payload, start, 4);
        String hex = Convert.toHex(ArrayUtil.reverse(bytes));
        int value = HexUtil.hexToInt(hex);

        String data = "";
        if (otherType == 0) { // 0：全量记步
            data = String.valueOf(value);
        } else if (otherType == 1) { // 1：增量记步
            data = String.valueOf(value);
        } else if (otherType == 2) { // 2：震动
            log.info("手环扩展类型为: 震动, 值为: {}", value);
        }
        return data;
    }

    /**
     * utc时间戳
     * @param payload 有效负载
     * @param start 从payload中开始读取的字节索引
     * @return
     */
    private int getTimestamp(byte[] payload, int start) {
        byte[] bytes = NettyUtil.getByte(payload, start, 4);
        String hex = Convert.toHex(ArrayUtil.reverse(bytes));
        int timestamp = HexUtil.hexToInt(hex); // 秒(10位长度)
        return timestamp;
    }
}
