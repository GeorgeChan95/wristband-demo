package com.george.binary;

import cn.hutool.core.util.HexUtil;

/**
 * @ClassName TestHexToBinary
 * @Description TODO
 * @Author George
 * @Date 2024/8/13 9:41
 */
public class TestHexToBinary {

    public static void main(String[] args) {
        byte a = (byte) 0x0a;

        byte b = (byte) (a >> 3); // 前五位

        int c = (((byte)(a << 5)) >> 5); // 后3位

        System.out.println();
    }


    /**
     * 打印一个int类型的数字，16位进制的状态
     * 左侧是高位，右侧是低位
     * 这里打印的是num的二进制补码。
     * @param num 十进制数值
     */
    public static String binaryStr(int num) {
        StringBuilder sb = new StringBuilder();
        for (int i = 7; i >= 0; i--) {
            // 下面这句写法，可以改成 :
            // System.out.print((a & (1 << i)) != 0 ? "1" : "0");
            // 但不可以改成 :
            // System.out.print((a & (1 << i)) == 1 ? "1" : "0");
            // 因为a如果第i位有1，那么(a & (1 << i))是2的i次方，而不一定是1
            // 比如，a = 0010011
            // a的第0位是1，第1位是1，第4位是1
            // (a & (1<<4)) == 16（不是1），说明a的第4位是1状态

            sb.append((num & (1 << i)) == 0 ? "0" : "1");
        }
        return sb.toString();
    }
}
