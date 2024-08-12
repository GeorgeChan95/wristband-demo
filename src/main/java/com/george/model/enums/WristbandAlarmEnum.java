package com.george.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @ClassName WristbandAlarm02Enum
 * @Description 手环告警标识枚举 0x02 , 0x16, 0x21
 * @Author George
 * @Date 2024/8/12 13:47
 */
@AllArgsConstructor
@Getter
public enum WristbandAlarmEnum {
    LOW_BATTERY(0x0001, "1", "低电量"),
    SOS_START(0x0002, "2", "SOS发起"),
    SHUT_DOWN(0x0004, "3", "关机"),
    OPEN_BOX(0x0008, "4", "开箱报警"),
    CAST_OFF(0x0010, "5", "摘掉报警"),
    SITTING_LONG_TIME(0x0020, "6", "久坐报警"),
    SHOCK(0x0040, "7", "震动"),
    SOS_CANCEL(0x0080, "8", "SOS取消"),
    WEAR(0x0100, "9", "设备佩戴"),
    HEART_RATE_ALARM(null, "10", "心率报警"),
    TEMPERATURE_ALARM(null, "11", "温度告警"),
    CHARGING_SHUT_DOWN(0x0004, "12", "设备充电中，已关机"),
    LOW_BATTERY_SHUT_DOWN(0x0002, "13", "设备电量低，已关机"),
    PROACTIVELY_SHUT_DOWN(0x0001, "14", "设备主动关机");

    /**
     * 消息标识符值
     */
    private final Integer value;

    /**
     * 告警类型编码
     */
    private final String alarmType;

    /**
     * 消息标识描述
     */
    private final String desc;
}
