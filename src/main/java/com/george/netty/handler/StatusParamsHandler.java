package com.george.netty.handler;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.george.message.Protocol;
import com.george.message.WristbandDataProtocol;
import com.george.model.dto.StatusParamsDto;
import com.george.model.enums.MessageTypeEnum;
import com.george.utils.NettyUtil;
import com.google.common.collect.Lists;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.List;

/**
 * @ClassName StatusParamsHandler
 * @Description 状态参数上传数据处理
 * @Author George
 * @Date 2024/8/9 11:15
 */
@Slf4j
@Component
public class StatusParamsHandler implements DataHandler {

    @Autowired
    private ObjectMapper objectMapper;

    private static final MessageTypeEnum messageTypeEnum = MessageTypeEnum.STATUS_PARAMS_A9;

    @Override
    public boolean matches(MessageTypeEnum messageId) {
        return ObjectUtil.equal(messageTypeEnum, messageId);
    }

    @Override
    public void handler(ChannelHandlerContext ctx, Protocol protocol) throws JsonProcessingException {
        log.info("开始处理手环状态参数上传请求......");
        WristbandDataProtocol wristbandData = (WristbandDataProtocol) protocol;
        byte[] payload = wristbandData.payload();

        // 类型数
        int typeNum = getTypeNum(payload);

        int index=2;
        List<StatusParamsDto> statusParamsList = Lists.newArrayList();
        for (int i = 0; i < typeNum; i++) {
            // 类型编码
            String typeCode = getTypeCode(payload, index);
            index++;
            // 类型名称长度
            int nameLength = getTypeNameLength(payload, index);
            index++;
            // 类型名称
            String typeName = getTypeName(payload, index, nameLength);
            index += nameLength;

            StatusParamsDto statusParamsDto = StatusParamsDto.builder().typeCode(typeCode).typeName(typeName).build();
            statusParamsList.add(statusParamsDto);
        }

        log.info("\n手环状态参数上传, 类型数: {}, 具体数据: {}\n", typeNum, objectMapper.writeValueAsString(statusParamsList));
    }

    /**
     * 获取类型数
     * @param payload 消息有效负载
     * @return
     */
    private int getTypeNum(byte[] payload) {
        byte[] typeNumBytes = NettyUtil.getByte(payload, 0, 1);
        String hex = Convert.toHex(typeNumBytes);
        int typeNum = HexUtil.hexToInt(hex);
        return typeNum;
    }

    /**
     * 获取类型编码
     * @param payload 消息有效负载
     * @param start 开始读取的字节索引
     * @return
     */
    private String getTypeCode(byte[] payload, int start) {
        byte[] typeNumBytes = NettyUtil.getByte(payload, start, 1);
        String typeCode = Convert.toHex(typeNumBytes);
        return typeCode;
    }

    /**
     * 获取类型名称长度
     * @param payload 消息有效负载
     * @param start 开始读取的字节索引
     * @return
     */
    private int getTypeNameLength(byte[] payload, int start) {
        byte[] typeNameLengthBytes = NettyUtil.getByte(payload, start, 1);
        String hex = Convert.toHex(typeNameLengthBytes);
        int typeNameLength = HexUtil.hexToInt(hex);
        return typeNameLength;
    }

    /**
     * 获取类型名称
     * @param payload 消息有效负载
     * @param start 开始读取的字节索引
     * @param nameLength 名称长度
     * @return
     */
    private String getTypeName(byte[] payload, int start, int nameLength) {
        byte[] typeNameBytes = NettyUtil.getByte(payload, start, nameLength);
        String typeName = new String(typeNameBytes, Charset.forName("utf-8"));
        return typeName;
    }
}
