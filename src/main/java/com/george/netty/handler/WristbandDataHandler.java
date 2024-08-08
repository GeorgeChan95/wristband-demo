package com.george.netty.handler;

import com.george.message.Protocol;
import com.george.model.enums.MessageTypeEnum;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.List;

/**
 * @ClassName SwtichDataHandler
 * @Description TODO
 * @Author George
 * @Date 2024/8/8 9:32
 */
@Slf4j
@RequiredArgsConstructor
public class WristbandDataHandler extends SimpleChannelInboundHandler<Protocol> {

    private final List<DataHandler> dataHandlerList;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Protocol msg) throws Exception {
        MessageTypeEnum messageTypeEnum = msg.messageId();

        boolean isMatch = false;
        Iterator<DataHandler> iterator = dataHandlerList.iterator();
        while (iterator.hasNext()) {
            DataHandler dataHandler = iterator.next();
            if (dataHandler.matches(messageTypeEnum)) {
                log.info("\n手环数据开始处理, 匹配类型: {}\n", dataHandler.getClass().getSimpleName());
                // 执行具体数据处理
                try {
                    dataHandler.handler(ctx, msg);
                } catch (Exception e) {
                    log.info("\n手环数据处理异常, 匹配类型: {},\n异常信息: ", dataHandler.getClass().getSimpleName(), e);
                }
                isMatch = true;
            }
        }

        if (!isMatch) {
            log.error("\n手环数据处理异常,没有找匹配类型, messageTypeEnum: {} \n", messageTypeEnum);
        }
    }
}
