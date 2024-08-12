package com.george.netty.handler;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.ObjectUtil;
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

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

/**
 * @ClassName RequestConnectionHandler
 * @Description 请求连接数据处理
 * @Author George
 * @Date 2024/8/8 10:00
 */
@Slf4j
@Component
public class RequestConnectionHandler implements DataHandler {
    private static final MessageTypeEnum messageTypeEnum = MessageTypeEnum.REQUEST_CONNECTION_F0;

    @Override
    public boolean matches(MessageTypeEnum messageId) {
        return ObjectUtil.equal(messageTypeEnum, messageId);
    }

    @Override
    public void handler(ChannelHandlerContext ctx, Protocol protocol) {
        log.info("开始处理手环连接请求......");
        Channel channel = ctx.channel(); // 当前请求的连接通道

        WristbandDataProtocol wristbandData = (WristbandDataProtocol) protocol;
        byte[] payload = wristbandData.payload();

        // 小端模式转大端模式
        byte[] reverse = ArrayUtil.reverse(payload);
        // 设备版本
        byte[] versionBytes = NettyUtil.getByte(reverse, 0, 2);
        String versionHex = Convert.toHex(versionBytes);

        // 设备编码
        byte[] imeiBytes = NettyUtil.getByte(reverse, 2, payload.length - 2);
        String imeiHex = Convert.toHex(imeiBytes);
        // 转十进制
        String imei = HexUtil.toBigInteger(imeiHex).toString();

        log.info("\n开始处理手环连接数据: " +
                "\n\t有效负载(小端模式): {},\t有效负载(大端模式): {}," +
                "\n\t设备版本字节: {},\t设备版本十六进制: {}," +
                "\n\t设备imei字节: {},\t设备imei十六进制: {},\t设备imei十进制: {}\n",
                payload, reverse,
                versionBytes, versionHex,
                imeiBytes, imeiHex, imei);

        // 将手环的连接信息放到map中存储
        Map<String, Channel> channelMap = WristbandConstant.channelMap;
        channelMap.put(imei, channel);

        // 回复连接请求消息内容
        ByteBuf byteBuf = connectionReply();
        byteBuf.resetReaderIndex();// 重置读索引

        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        log.info("\n服务端响应手环连接信息: {}\n", Convert.toHex(bytes));

        byteBuf.resetReaderIndex();
        ctx.channel().writeAndFlush(byteBuf);
    }

    /**
     * 回复手环连接请求, 获取回复内容
     */
    private ByteBuf connectionReply() {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(4);

        // 当前时间秒
        long second = LocalDateTime.now().toInstant(ZoneOffset.ofHours(8)).getEpochSecond();
        // 转十六进制
        String hex = HexUtil.toHex(second);
        // 转字节,大端模式
        byte[] bytes = HexUtil.decodeHex(hex);
        // 转小端模式
        bytes = ArrayUtil.reverse(bytes);

        // head
        buffer.writeBytes(bytes);;

        // messageId
        buffer.writeByte(MessageTypeEnum.CONNECTION_REPLY_F1.getCode());

        // payload
        buffer.writeBytes(new byte[]{(byte) 0xbd, (byte) 0xbd,(byte) 0xbd,(byte) 0xbd});

        // 校验和
        byte checksum =  NettyUtil.getChecksum(buffer);
        String checksumHex = Convert.toHex(new byte[]{checksum});
        buffer.writeByte(checksum);

        return buffer;
    }
}
