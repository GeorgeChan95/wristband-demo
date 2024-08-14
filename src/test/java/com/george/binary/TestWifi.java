package com.george.binary;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ByteUtil;
import cn.hutool.core.util.HexUtil;

import java.nio.ByteOrder;

/**
 * @ClassName TestWifi
 * @Description TODO
 * @Author George
 * @Date 2024/8/13 19:11
 */
public class TestWifi {
    public static void main(String[] args) {
        // byte 在java中是一个有符号整数,

        // wifi信号强度
        byte[] bytes = new byte[] {(byte) 0xdb, (byte) 0xff, (byte) 0xff, (byte) 0xff};
//        byte[] bytes = new byte[] {(byte) 0xd6, (byte) 0xff, (byte) 0xff, (byte) 0xff};
//        byte[] bytes = new byte[] {(byte) 0xca, (byte) 0xff, (byte) 0xff, (byte) 0xff};
//        byte[] bytes = new byte[] {(byte) 0xc4, (byte) 0xff, (byte) 0xff, (byte) 0xff};
//        byte[] bytes = new byte[] {(byte) 0xb5, (byte) 0xff, (byte) 0xff, (byte) 0xff};
        int num = ByteUtil.bytesToInt(bytes, ByteOrder.LITTLE_ENDIAN);
        String rssi = num + "";
        System.out.println(rssi);

        int b = 0xff & 0xff;
        printBinary(b);
        int c = 0xdb & 0xff;
        printBinary(c);

        byte[] bytes2 = new byte[] {(byte) 0b11111111, (byte) 0b11111111, (byte) 0b11111111, (byte) 0b10110101};
        String hex = Convert.toHex(bytes2);
        int d = HexUtil.toBigInteger(hex).intValue();
        System.out.println(d);

        String hex1 = Convert.toHex(ArrayUtil.reverse(bytes));
        int e = HexUtil.toBigInteger(hex1).intValue();
        System.out.println(e);

        System.out.println("======================");
        byte x = (byte) 0b11011011; // -37
        int z = 0b11011011; // 219
        System.out.println(x);
        printBinary(219);
        byte y = (byte) 0b00000000000000000000000011011011;
        System.out.println(y);


        int w = 219;
        byte b1 = ByteUtil.intToByte(w); // -37
        System.out.println(b1);

        byte q = (byte) 0b10000000000000000000000000100101; // 37, byte 只保留低位的8位
        System.out.println(q);

        /**
         *
         在Java中，byte类型是一个8位的有符号整数，取值范围是从 -128 到 127。当你将二进制数 0b11011011 转换为 byte 时，会发生以下情况：

         二进制表示: 0b11011011 对应的是二进制数 11011011。

         范围解释:

         Java中的byte是有符号的，最高位（最左边的位）是符号位。
         如果最高位是1，则这个数是负数，表示为补码形式。
         补码解释:

         11011011 被解释为负数。
         要获得该负数的绝对值，先将其取反（即每一位取反），然后再加1。
         取反后的数是：00100100（即36）。
         加1后得到：00100101（即37）。

         结果:

         因此，11011011 对应的十进制值是 -37。
         当你执行 byte x = (byte) 0b11011011; 并打印它时，Java会将其解释为 -37，这是因为在有符号byte中，11011011是-37的补码表示。

         */
    }

    /**
     * 打印一个int类型的数字，32位进制的状态
     * 左侧是高位，右侧是低位
     * 这里打印的是num的二进制补码。
     * @param num 十进制数值
     */
    public static void printBinary(int num) {
        for (int i = 31; i >= 0; i--) {
            // 下面这句写法，可以改成 :
            // System.out.print((a & (1 << i)) != 0 ? "1" : "0");
            // 但不可以改成 :
            // System.out.print((a & (1 << i)) == 1 ? "1" : "0");
            // 因为a如果第i位有1，那么(a & (1 << i))是2的i次方，而不一定是1
            // 比如，a = 0010011
            // a的第0位是1，第1位是1，第4位是1
            // (a & (1<<4)) == 16（不是1），说明a的第4位是1状态
            System.out.print((num & (1 << i)) == 0 ? "0" : "1");
        }
        System.out.println();
    }
}
