package com.george.netty.service;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ByteUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.RandomUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.george.model.constant.WristbandConstant;
import com.george.model.enums.MessageTypeEnum;
import com.george.utils.NettyUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;

/**
 * @ClassName WristbandSettingService
 * @Description 给手环下发数据, 设置参数
 * @Author George
 * @Date 2024/8/13 17:14
 */
@Slf4j
@Service
public class WristbandSettingService {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 设置手环周期上传
     * @param channel
     * @param time 上传间隔时长(分钟)
     */
    public void cycleUpload(Channel channel, int time) {
        ByteBuf byteBuf = channel.alloc().buffer();
        byteBuf.writeBytes(WristbandConstant.header); // token
        byteBuf.writeByte(MessageTypeEnum.CYCLE_UPLOAD.getCode()); // 消息标识
        byteBuf.writeByte(0x01); // 0x01表示设置启用
        // 上传间隔时长
        byte cycleByte = ByteUtil.intToByte(time);
        byte[] timeBytes = new byte[2];
        timeBytes[0] = cycleByte;
        timeBytes[1] = 0;
        byteBuf.writeBytes(timeBytes);
        // 时间段1的: 开始时,开始分,结束时,结束分
        byteBuf.writeByte(0x00); // 0点
        byteBuf.writeByte(0x00); // 0分
        byteBuf.writeByte(0x17); // 23点
        byteBuf.writeByte(0x3b); // 59分

        // 时间段2的, 是否启用,时间间隔, 开始时,开始分,结束时,结束分
        byteBuf.writeByte(0x00); // 不启用
        byteBuf.writeBytes(new byte[]{0x00, 0x00});// 间隔0
        byteBuf.writeByte(0x00); // 0点
        byteBuf.writeByte(0x00); // 0分
        byteBuf.writeByte(0x00); // 0点
        byteBuf.writeByte(0x00); // 0分

        // 时间段3的, 是否启用,时间间隔, 开始时,开始分,结束时,结束分
        byteBuf.writeByte(0x00); // 不启用
        byteBuf.writeBytes(new byte[]{0x00, 0x00});// 间隔0
        byteBuf.writeByte(0x00); // 0点
        byteBuf.writeByte(0x00); // 0分
        byteBuf.writeByte(0x00); // 0点
        byteBuf.writeByte(0x00); // 0分

        // 时间段4的, 是否启用,时间间隔, 开始时,开始分,结束时,结束分
        byteBuf.writeByte(0x00); // 不启用
        byteBuf.writeBytes(new byte[]{0x00, 0x00});// 间隔0
        byteBuf.writeByte(0x00); // 0点
        byteBuf.writeByte(0x00); // 0分
        byteBuf.writeByte(0x00); // 0点
        byteBuf.writeByte(0x00); // 0分

        byte checksum = NettyUtil.getChecksum(byteBuf);
        String checksumHex = Convert.toHex(new byte[]{checksum});
        byteBuf.writeByte(checksum);

        byteBuf.resetReaderIndex();
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        log.info("\n服务端设置手环周期上传定位, 设置信息如下: {}\n", Convert.toHex(bytes));

        // 数据写入通道
        byteBuf.resetReaderIndex();
        channel.writeAndFlush(byteBuf);
    }

    /**
     * 信息下发
     * @param channel
     * @param msg 消息内容
     */
    public void messageSend(Channel channel, String msg) {
        ByteBuf byteBuf = channel.alloc().buffer();
        byteBuf.writeBytes(WristbandConstant.header); // header
        byteBuf.writeByte(MessageTypeEnum.MESSAGE_SEND.getCode()); // 消息标识
        byteBuf.writeByte(0x03); // 消息类型，如果是下行信息，固定值为03
        byte[] randomBytes = RandomUtil.randomBytes(4); // 信息的id，唯一性
        byteBuf.writeBytes(randomBytes);

        String gb2312 = HexUtil.encodeHexStr(msg, Charset.forName("GB2312"));
        byte[] msgBytes = HexUtil.decodeHex(gb2312); // 消息内容, 中文使用 GB2312编码后, 再转字节
        byteBuf.writeByte(Integer.valueOf(msgBytes.length).byteValue()); // 内容长度
        byteBuf.writeBytes(msgBytes); // 写入内容

        // 写入校验和
        byte checksum = NettyUtil.getChecksum(byteBuf);
        byteBuf.writeByte(checksum);

        byteBuf.resetReaderIndex();
        channel.writeAndFlush(byteBuf);
    }

    /**
     * 设置GPS上传优先
     * wifi定位优先（wifi>蓝牙>gps）：BDBDBDBDCE0100030002030133
     * gps定位优先（gps>wifi>蓝牙）： BDBDBDBDCE0100030001020333
     * 蓝牙定位优先（需部署蓝牙信标，蓝牙>wifi>gps）:BDBDBDBDCE0100030003020133
     * @param channel
     */
    public void setGpsFirst(Channel channel) {
        ByteBuf byteBuf = channel.alloc().buffer();
        byteBuf.writeBytes(WristbandConstant.header);
        byteBuf.writeByte(MessageTypeEnum.GPS_FIRST.getCode());
        byte[] gpsBytes = new byte[] {0x01, 0x00, 0x03, 0x00, 0x02, 0x03, 0x01, 0x33};
        byteBuf.writeBytes(gpsBytes);

        channel.writeAndFlush(byteBuf);
    }
}
