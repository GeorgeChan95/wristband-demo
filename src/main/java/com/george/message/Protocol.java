package com.george.message;

import com.george.model.enums.MessageTypeEnum;

/**
 * 自定义协议接口, 定义数据结构, 手环数据的收发对象需实现该接口
 */
public interface Protocol {

    /**
     * 消息头, 长度4字节
     * @return
     */
    byte[] header();

    /**
     * 报文标示符, 长度1字节
     * @return
     */
    MessageTypeEnum messageId();

    /**
     * 有效负载, 长度不固定
     * @return
     */
    byte[] payload();

    /**
     * 消息校验和, 长度1字节
     * @return
     */
    byte checksum();

}
