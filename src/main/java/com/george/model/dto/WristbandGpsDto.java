package com.george.model.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName WristbandGpsDto
 * @Description 手环GPS数据上传
 * @Author George
 * @Date 2024/8/13 16:40
 */
@Data
@Builder
public class WristbandGpsDto implements Serializable {
    private static final long serialVersionUID = 1l;

    /**
     * 手环IMEI
     */
    private String imei;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 南北向 N or S
     */
    private String northSouth;

    /**
     * 东西向 E or W
     */
    private String eastWest;

    /**
     * 时间戳(10位:秒)
     */
    private Integer timestamp;
}
