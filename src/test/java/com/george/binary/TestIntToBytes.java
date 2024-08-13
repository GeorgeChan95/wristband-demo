package com.george.binary;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ByteUtil;
import cn.hutool.core.util.HexUtil;

/**
 * @ClassName TestIntToBytes
 * @Description TODO
 * @Author George
 * @Date 2024/8/13 17:51
 */
public class TestIntToBytes {
    public static void main(String[] args) {
        int a = 3;
        byte byte1 = ByteUtil.intToByte(3);

        byte[] bytes = new byte[2];
        bytes[0] = byte1;
        bytes[1] = 0;

        String hex = Convert.toHex(bytes);
        System.out.println(hex);
    }
}
