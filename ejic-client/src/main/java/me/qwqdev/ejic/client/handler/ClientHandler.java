package me.qwqdev.ejic.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.StandardCharsets;

/**
 * The type Client handler.
 *
 * @author : qwq-dev InkerBot
 * @since : 2024-10-30 22:45
 */
public class ClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
    /**
     * {@inheritDoc}
     *
     * @param channelHandlerContext {@inheritDoc}
     * @param rawMsg                {@inheritDoc}
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf rawMsg) {
        byte[] msgBytes = new byte[rawMsg.readableBytes()];
        rawMsg.readBytes(msgBytes);
        String msg = new String(msgBytes, StandardCharsets.UTF_8);
        // TODO: display msg to client
    }

    /**
     * {@inheritDoc}
     *
     * @param channelHandlerContext {@inheritDoc}
     * @param cause                 {@inheritDoc}
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) {
        // TODO: handling exceptions or close
        // channelHandlerContext.close();
    }
}