package me.qwqdev.ejic.server.data;

import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import me.qwqdev.ejic.server.handler.ServiceHandler;

/**
 * The service data.
 *
 * @author : qwq-dev InkerBot
 * @since : 2024-10-30 18:57
 */
public class ServiceData {
    private final String address;
    private final int port;
    private final ChannelGroup channelGroup;

    /**
     * Instantiates a new service data.
     *
     * @param address the address
     * @param port    the port
     */
    public ServiceData(String address, int port) {
        this.address = address;
        this.port = port;
        this.channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    }

    /**
     * Instantiates a new service data.
     *
     * @param address      the address
     * @param port         the port
     * @param channelGroup the channel group
     */
    public ServiceData(String address, int port, ChannelGroup channelGroup) {
        this.address = address;
        this.port = port;
        this.channelGroup = channelGroup;
    }

    /**
     * Gets address.
     *
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Gets port.
     *
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * Gets service handler.
     *
     * @return the service handler
     */
    public ChannelInboundHandlerAdapter createServiceHandler() {
        return new ServiceHandler(channelGroup);
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
