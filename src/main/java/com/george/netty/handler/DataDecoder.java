package com.george.netty.handler;

import cn.hutool.core.convert.Convert;
import com.george.message.WristbandDataProtocol;
import com.george.model.enums.MessageTypeEnum;
import com.george.utils.NettyUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 消息解码器
 */
@Slf4j
public class DataDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> list) throws Exception {
        log.info("执行解码器....");

        int index = 0;

        int length = in.readableBytes();// 消息长度
        byte[] bytes = new byte[length];
        in.readBytes(bytes);
        String hex1 = Convert.toHex(bytes);
        log.info("\n接收到消息: {}\n", hex1);

        // 消息头
        byte[] head = NettyUtil.getByte(bytes, index, 4);
        String hex = Convert.toHex(head);
        index += head.length;

        // 报文标示符
        byte[] messageIdBytes = NettyUtil.getByte(bytes, index, 1);
        byte messageId = bytes[index];
        String messageIdHex = Convert.toHex(messageIdBytes);
        index += messageIdBytes.length;

        // 有效负载
        byte[] payloadBytes = NettyUtil.getByte(bytes, index, length - (index + 1));
        index += payloadBytes.length;

        // 校验和
        byte[] checksumBytes = NettyUtil.getByte(bytes, index, 1);
        byte checksum = bytes[index];
        String checksumHex = Convert.toHex(checksumBytes);

        WristbandDataProtocol dataProtocol = new WristbandDataProtocol();
        dataProtocol.setHeader(head);
        dataProtocol.setMessageId(MessageTypeEnum.getMessageTypeEnum(messageId));
        dataProtocol.setPayload(payloadBytes);
        dataProtocol.setChecksum(checksum);
        list.add(dataProtocol);

        // 释放内存
//        ReferenceCountUtil.release(in);

        /*
        // 小端模式转大端模式
        byte[] reverse = ArrayUtil.reverse(bytes);
        // 转十六进制
        String hex1 = Convert.toHex(reverse);
        // 转十进制
        String imei = HexUtil.toBigInteger(hex1).toString();
        int anInt = NumberUtil.toInt(reverse);
        System.out.println(111);

        int i = ByteUtil.bytesToInt(bytes, 0, ByteOrder.LITTLE_ENDIAN);
        System.out.println(i);
        byte[] aaa = new byte[]{(byte) 0xbd, (byte) 0xbd, (byte) 0xbd, (byte) 0xbd};
        System.out.println(hex);
*/
    }
}
