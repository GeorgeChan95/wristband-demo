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

    REQUEST_CONNECTION_F0((byte) 0xf0, "请求连接"),
    CONNECTION_REPLY_F1((byte) 0xf1, "连接回复"),
    SIM_ICCID_F3((byte) 0xf3, "SIM卡的ICCID上传"),
    STATUS_PARAMS_A9((byte) 0xa9, "状态参数上报"),
    CELL_INFO_A4((byte) 0xa4, "wifi和基站信息上传"),
    HEARTBEAT_UP_F9((byte) 0xf9, "新心跳包协议"),
    GPS_UPL_03((byte) 0x03, "定位数据上报"),
    ALARM_DATA_02((byte) 0x02, "报警数据上传"),
    ALARM_DATA_16((byte) 0x16, "报警数据上传, 对0x02的补充"),
    ALARM_DATA_21((byte) 0x21, "报警数据上传, 对0x02的补充"),
    HEALTH_DATA((byte) 0x32, "健康数据"),
    CYCLE_UPLOAD((byte) 0x17, "设置周期上传"),
    SETTING_FEEDBACK((byte) 0xc0, "下行反馈"),
    MESSAGE_SEND((byte) 0x28, "消息下发"),
    GPS_FIRST((byte) 0xce, "定位GPS优先")
    ;

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
