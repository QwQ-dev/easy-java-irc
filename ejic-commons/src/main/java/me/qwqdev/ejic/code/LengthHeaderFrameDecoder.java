package me.qwqdev.ejic.code;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * The type Length header frame decoder.
 *
 * @author : qwq-dev
 * @since : 2024-10-31 11:21
 */
public class LengthHeaderFrameDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        while (msg.readableBytes() >= 4) {
            msg.markReaderIndex();
            int length = msg.readInt();

            if (msg.readableBytes() < length) {
                msg.resetReaderIndex();
                break;
            }

            ByteBuf frame = msg.readBytes(length);
            out.add(frame);
        }
    }
}