package me.qwqdev.ejic.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;

import java.nio.charset.StandardCharsets;

/**
 * The service handler.
 *
 * @author : qwq-dev InkerBot
 * @since : 2024-10-30 19:13
 */
public class ServiceHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private final ChannelGroup channelGroup;

    /**
     * Instantiates a new Server service handler.
     *
     * @param channelGroup the channel group
     */
    public ServiceHandler(ChannelGroup channelGroup) {
        this.channelGroup = channelGroup;
    }

    /**
     * {@inheritDoc}
     *
     * @param channelHandlerContext {@inheritDoc}
     * @throws Exception {@inheritDoc}
     */
    @Override
    @SuppressWarnings("RedundantThrows")
    public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {
        channelGroup.add(channelHandlerContext.channel());
    }

    /**
     * Send to all channels except own.
     * <p>
     * {@inheritDoc}
     *
     * @param channelHandlerContext {@inheritDoc}
     * @param {@inheritDoc}
     * @throws Exception {@inheritDoc}
     */
    @Override
    @SuppressWarnings("RedundantThrows")
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf rawMsg) throws Exception {
        byte[] msgBytes = new byte[rawMsg.readableBytes()];
        rawMsg.readBytes(msgBytes);
        String msg = new String(msgBytes, StandardCharsets.UTF_8);

        channelGroup.forEach(channel -> {
            channel.writeAndFlush(rawMsg.resetReaderIndex().copy());
            if (channel != channelHandlerContext.channel()) {
                // TODO: send to other client
            }
        });
    }

    /**
     * {@inheritDoc}
     *
     * @param channelHandlerContext {@inheritDoc}
     * @throws Exception {@inheritDoc}
     */
    @Override
    @SuppressWarnings("RedundantThrows")
    public void channelInactive(ChannelHandlerContext channelHandlerContext) throws Exception {
        channelGroup.remove(channelHandlerContext.channel());
    }

    /**
     * {@inheritDoc}
     *
     * @param channelHandlerContext {@inheritDoc}
     * @param cause                 {@inheritDoc}
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) {
        // TODO: log cause
        channelGroup.remove(channelHandlerContext.channel());
        channelHandlerContext.close();
    }

    /**
     * Gets channel group.
     *
     * @return the channel group
     */
    public ChannelGroup getChannelGroup() {
        return channelGroup;
    }
}