package com.george.hex;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ByteUtil;
import cn.hutool.core.util.HexUtil;
import com.george.utils.NettyUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
public class TestCk {

    /**
     * 计算校验和
     */
    public static void main(String[] args) {
        // 计算校验和
//        getCkeckSum();

        printBinary(0x0180); // 打印: 设备佩戴和SOS取消两个告警同时上传时的十六进制编码的二进制表示
        printBinary(0x0100); // 打印设备佩戴, 十六进制编码的二进制表示
        printBinary(0x0080); // SOS取消十六进制编码的二进制表示

        System.out.println("=====================");
        printBinary(0x0180 & 0x0080); // 多种报警结果值 & 一个报警值, 结果为这个报警结果值
        System.out.println(0x0080);

        printBinary(0x0180 & 0x0100);
        System.out.println(0x0100);

        System.out.println("***************************");
        printBinary(0x0001);
        printBinary(0x0002);

//        textBytesToInt();
    }

    /**
     * 计算校验和
     */
    private static void getCkeckSum() {
        // 28 D4 DE 55 F1 BD BD BD BD EB

        byte[] arr = new byte[]{0x28, (byte)0xd4, (byte) 0xde, 0x55, (byte) 0xf1, (byte) 0xbd, (byte) 0xbd,(byte) 0xbd,(byte) 0xbd};

        byte ck_sum = 0;
        for (int i = 0; i < arr.length; i++ ) {
            ck_sum = (byte) (ck_sum + arr[i]);
            ck_sum = (byte) (ck_sum % 0x100);
        }
        ck_sum = (byte) (0xFF - ck_sum);
        String hex = Convert.toHex(new byte[]{ck_sum});
        System.out.println(hex);
    }


    /**
     * 打印一个int类型的数字，16位进制的状态
     * 左侧是高位，右侧是低位
     * 这里打印的是num的二进制补码。
     * @param num 十进制数值
     */
    public static void printBinary(int num) {
        for (int i = 15; i >= 0; i--) {
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

    /**
     * 字节转int
     */
    public static void textBytesToInt() {
        byte[] array = new byte[] {0x00, 0x01};
        byte[] codeBytes = ArrayUtil.reverse(array);
        String hex = Convert.toHex(codeBytes);
        int intValue = HexUtil.hexToInt(hex);
        System.out.println(intValue);
    }
}
