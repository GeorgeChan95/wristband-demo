package com.george.model.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName StatusParamsDto
 * @Description 手环状态参数
 * @Author George
 * @Date 2024/8/9 11:41
 */
@Data
@Builder
public class StatusParamsDto implements Serializable {
    private static final long serialVersionUID = 1l;

    /**
     * 类型编码
     */
    private String typeCode;

    /**
     * 类型名称
     */
    private String typeName;
}
