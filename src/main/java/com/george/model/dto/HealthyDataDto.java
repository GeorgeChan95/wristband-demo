package com.george.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName HealthyDataDto
 * @Description 体征数据对象
 * @Author George
 * @Date 2024/8/13 9:05
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthyDataDto implements Serializable {
    private static final long serialVersionUID = 1l;

    /**
     * 设备号
     */
    private String imei;

    /**
     * 步数
     */
    private Integer stepCount;

    /**
     * 心率
     */
    private Integer heartRate;

    /**
     * 舒张压
     */
    private Integer diastolicPressure;

    /**
     * 收缩压
     */
    private Integer systolicPressure;

    /**
     * 体温
     */
    private Float bodyTemp;

    /**
     * 腕温
     */
    private Float wristTemp;

    /**
     * 采集时间戳:秒
     */
    private Integer timestamp;
}
