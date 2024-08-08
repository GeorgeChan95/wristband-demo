package com.george.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.HexUtil;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

@Slf4j
public class NettyUtil {
    /**
     * 返回指定位置的数组
     *
     * @param bytes
     * @param start  开始位置
     * @param length 截取长度
     * @return
     */
    public static byte[] getByte(byte[] bytes, int start, int length) {
        byte[] ruleByte = new byte[length];
        int index = 0;
        while (index < length) {
            ruleByte[index++] = bytes[start++];
        }
        return ruleByte;
    }

    /**
     * 计算校验和
     * @param buffer 要计算的字节ByteBuf
     * @return
     */
    public static byte getChecksum(ByteBuf buffer) {
//        buffer.clear();
        if (ObjectUtils.isEmpty(buffer) || buffer.readableBytes() == 0) {
            log.error("\n计算校验和失败, buffer为空");
            return 0x00;
        }
        // ByteBuf转字节数组
        byte[] bytes = new byte[buffer.readableBytes()];
        buffer.readBytes(bytes);

        byte ck_sum = 0;
        for (int i = 0; i < bytes.length; i++ ) {
            ck_sum = (byte) (ck_sum + bytes[i]);
            ck_sum = (byte) (ck_sum % 0x100);
        }
        ck_sum = (byte) (0xFF - ck_sum);
        String hex = HexUtil.toHex(ck_sum);
        return ck_sum;
    }
}
