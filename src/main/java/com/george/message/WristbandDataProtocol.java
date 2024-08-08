package com.george.message;

import com.george.model.enums.MessageTypeEnum;
import lombok.Data;

/**
 * 手环数据对象
 */
@Data
public class WristbandDataProtocol implements Protocol {

    /**
     * 消息头, 长度4字节
     */
    private byte[] header;

    /**
     * 报文标示符, 长度1字节
     * @return
     */
    private MessageTypeEnum messageId;

    /**
     * 有效负载, 长度不固定
     * @return
     */
    private byte[] payload;

    /**
     * 消息校验和, 长度1字节
     * @return
     */
    private byte checksum;


    @Override
    public byte[] header() {
        return this.header;
    }

    @Override
    public MessageTypeEnum messageId() {
        return this.messageId;
    }

    @Override
    public byte[] payload() {
        return this.payload;
    }

    @Override
    public byte checksum() {
        return this.checksum;
    }
}
