package com.george.hex;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ByteUtil;
import cn.hutool.core.util.HexUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
public class TestCk {

    /**
     * 计算校验和
     */
    public static void main(String[] args) {
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
}
