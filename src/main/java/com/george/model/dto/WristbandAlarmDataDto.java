package com.george.model.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName WristbandAlarmDataDto
 * @Description 手环告警数据对象
 * @Author George
 * @Date 2024/8/12 15:21
 */
@Data
@Builder
public class WristbandAlarmDataDto implements Serializable {
    private static final long serialVersionUID = 1l;

    /**
     * 设备号
     */
    private String imei;

    /**
     * 告警类型, 具体见: com.george.model.enums.WristbandAlarmEnum.typeCode
     */
    private String alarmType;

    /**
     * 告警值, 温度告警, 心率报警会有值
     */
    private String alarmValue;

    /**
     * 告警时间
     */
    private Integer timestamp;

}
