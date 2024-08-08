package com.george.model.enums;

import cn.hutool.core.util.ObjectUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息标识符枚举
 */
@AllArgsConstructor
@Getter
public enum MessageTypeEnum {

    REQUEST_CONNECTION((byte) 0xf0, "请求连接"),
    CONNECTION_REPLY((byte) 0xf1, "连接回复"),
    SIM_ICCID((byte) 0xf3, "SIM卡的ICCID上传"),
    STATUS_PARAMS((byte) 0xa9, "状态参数上报"),
    CELL_INFO((byte) 0xa4, "wifi和基站信息上传"),
    HEARTBEAT_UP((byte) 0xf9, "新心跳包协议");

    /**
     * 消息标识符编码
     */
    private final byte code;

    /**
     * 消息标识描述
     */
    private final String desc;

    public static MessageTypeEnum getMessageTypeEnum(byte code) {
        for (MessageTypeEnum type : MessageTypeEnum.values()) {
            if (ObjectUtil.equal(type.code, code)) {
                return type;
            }
        }
        return null;
    }
}
