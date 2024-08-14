package com.george.netty.handler;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ByteUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.george.message.Protocol;
import com.george.message.WristbandDataProtocol;
import com.george.model.enums.MessageTypeEnum;
import com.george.utils.NettyUtil;
import com.google.common.collect.Lists;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.ByteOrder;
import java.util.List;

/**
 * @ClassName CellInfoHandler
 * @Description wifi和基站信息上传
 * @Author George
 * @Date 2024/8/9 13:16
 */
@Slf4j
@Component
public class CellInfoHandler implements DataHandler {
    private static final MessageTypeEnum messageTypeEnum = MessageTypeEnum.CELL_INFO_A4;

    @Override
    public boolean matches(MessageTypeEnum messageId) {
        return ObjectUtil.equal(messageTypeEnum, messageId);
    }

    @Override
    public void handler(ChannelHandlerContext ctx, Protocol protocol) throws JsonProcessingException {
        log.info("开始处理手环wifi和基站信息上传请求......");
        Channel channel = ctx.channel(); // 当前请求的连接通道

        WristbandDataProtocol wristbandData = (WristbandDataProtocol) protocol;
        byte[] payload = wristbandData.payload();
        String hex = Convert.toHex(payload);
        log.info("\nwifi和基站信息(0xa4)上传信息有效负载: {}\n", hex);

        int index = 0;

        // 时间戳
        byte[] timeBytes = ArrayUtil.reverse(NettyUtil.getByte(payload, index, 4));
        index += 4;

        // 基站数量
        byte[] lbsNumBytes = NettyUtil.getByte(payload, index, 1);
        int lbsNum = HexUtil.hexToInt(Convert.toHex(lbsNumBytes));
        index++;

        List<String> lbss = Lists.newArrayList();
        // 每个基站
        for (int i = 0; i < lbsNum; i++) {
            byte[] lbsBytes = NettyUtil.getByte(payload, index, 12);
            index += 12;
            String lbs = Convert.toHex(lbsBytes);
            lbss.add(lbs);
        }

        // wifi数量
        byte[] wifiNumBytes = NettyUtil.getByte(payload, index, 1);
        int wifiNum = HexUtil.hexToInt(Convert.toHex(wifiNumBytes));
        index++;

        List<byte[]> wifiBytesList = Lists.newArrayList();
        // 每个wifi
        for (int i = 0; i < wifiNum; i++) {
            byte[] wifiBytes = NettyUtil.getByte(payload, index, 10);
            index += 10;
            wifiBytesList.add(wifiBytes);
        }

        if (wifiBytesList.size() < 2) {
            log.error("\nwifi和基站信息解析(0xa4)异常, 搜索到wifi数量不足2个\n");
            return;
        }

        // 获取url连接中的 macs
        String macs = getMacs(wifiBytesList);

        // 手环IMEI
        String imei = NettyUtil.getImei(channel);

        // url: http://apilocate.amap.com/position?accesstype=1&imei=352315052834187&macs=4c:48:da:25:0b:11,-59,alibaba-inc|4c:48:da:25:1a:11,-77,alibaba-inc&serverip=10.2.166.4&output=

        String url = new StringBuilder()
                .append("http://apilocate.amap.com/position?accesstype=1&")
                .append("imei=" + imei)
                .append("&")
                .append(macs)
                .append("&serverip=10.2.166.4&output=json&amp;key=")
                .append("be8822114837da0812d30f4b215b3ec4")
                .toString();

        HttpResponse response = HttpRequest.get(url)
                .timeout(5000)
                .execute();
        String body = response.body();


        log.info("\nwifi和基站信息解析成功, 请求url:{},\t 接口返回: {}\n", url, body);


    }

    /**
     * 通过检索到的wifi列表解析出 macs
     * macs=4c:48:da:25:0b:11,-59,alibaba-inc|4c:48:da:25:1a:11,-77,alibaba-inc
     * @param wifiBytesList wifi数据集(字节形式)
     * @return
     */
    private String getMacs(List<byte[]> wifiBytesList) {
        StringBuilder macsBuilder = new StringBuilder("macs=");
        int length = wifiBytesList.size() > 30 ? 30 : wifiBytesList.size();
        for (int i = 0; i < length; i++) {
            byte[] macBytes = wifiBytesList.get(i);
            String mac1 = Convert.toHex(NettyUtil.getByte(macBytes, 0, 1));
            String mac2 = Convert.toHex(NettyUtil.getByte(macBytes, 1, 1));
            String mac3 = Convert.toHex(NettyUtil.getByte(macBytes, 2, 1));
            String mac4 = Convert.toHex(NettyUtil.getByte(macBytes, 3, 1));
            String mac5 = Convert.toHex(NettyUtil.getByte(macBytes, 4, 1));
            String mac6 = Convert.toHex(NettyUtil.getByte(macBytes, 5, 1));
            int rssi = ByteUtil.bytesToInt(NettyUtil.getByte(macBytes, 6, 4), ByteOrder.LITTLE_ENDIAN);

            macsBuilder
                    .append(mac1).append(":")
                    .append(mac2).append(":")
                    .append(mac3).append(":")
                    .append(mac4).append(":")
                    .append(mac5).append(":")
                    .append(mac6)
                    .append(",")
                    .append(rssi)
                    .append(",alibaba-inc");
            if (i < length - 1) macsBuilder.append("|");
        }
        return macsBuilder.toString();
    }
}
