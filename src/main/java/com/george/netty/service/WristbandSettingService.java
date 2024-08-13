package com.george.netty.service;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ByteUtil;
import cn.hutool.core.util.HexUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.george.model.constant.WristbandConstant;
import com.george.model.enums.MessageTypeEnum;
import com.george.utils.NettyUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
