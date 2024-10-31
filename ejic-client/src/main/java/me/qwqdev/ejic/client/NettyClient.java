package me.qwqdev.ejic.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import me.qwqdev.ejic.client.handler.ClientHandler;
import me.qwqdev.ejic.code.LengthHeaderFrameDecoder;

import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * The type Netty client.
 *
 * @author : qwq-dev InkerBot InkerBot
 * @since : 2024-10-30 23:45
 */
public class NettyClient implements Closeable, AutoCloseable {
    private final String host;
    private final int port;
    private final int reconnectMilliseconds;
    private final int initialConnectionMillisecondsDelay;
    private final boolean asyncConnection;

    private EventLoopGroup eventLoopGroup;
    private Bootstrap bootstrap;
    private ChannelFuture channelFuture;

    /**
     * Instantiates a new Netty client.
     *
     * @param host                               the host
     * @param port                               the port
     * @param reconnectMilliseconds              the reconnect milliseconds
     * @param initialConnectionMillisecondsDelay the initial connection milliseconds delay
     * @param asyncConnection                    the async connection
     */
    public NettyClient(String host, int port, int reconnectMilliseconds, int initialConnectionMillisecondsDelay, boolean asyncConnection) {
        this.host = host;
        this.port = port;
        this.reconnectMilliseconds = reconnectMilliseconds;
        this.initialConnectionMillisecondsDelay = initialConnectionMillisecondsDelay;
        this.asyncConnection = asyncConnection;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException Connection disconnected
     */
    @Override
    public void close() throws IOException {
        if (!isConnected()) {
            throw new IOException("Connection disconnected.");
        }

        this.channelFuture.channel().close();
    }

    /**
     * Start.
     */
    public void start() {
        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup();

        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        ChannelPipeline channelPipeline = socketChannel.pipeline();
                        socketChannel.pipeline().addLast(new LengthHeaderFrameDecoder());
                        channelPipeline.addLast(new ClientHandler());
                    }
                });

        connect();
    }

    /**
     * Connect.
     */
    private void connect() {
        Runnable runnable = () -> {
            // if u want to limit the reconnections, add it here
            try {
                this.channelFuture = bootstrap.connect(host, port).sync();
                channelFuture.channel().closeFuture().sync();
            } catch (Exception exception) {
                // TODO: handling exceptions and log
            } finally {
                try {
                    Thread.sleep(reconnectMilliseconds);
                } catch (InterruptedException exception) {
                    // TODO: handling exceptions and log
                } finally {
                    connect();
                }
            }
        };

        if (asyncConnection) {
            eventLoopGroup.schedule(runnable, initialConnectionMillisecondsDelay, TimeUnit.MILLISECONDS);
        } else {
            runnable.run();
        }
    }

    /**
     * Check if the client is connected.
     *
     * @return true if connected, false otherwise.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isConnected() {
        return channelFuture != null && channelFuture.channel().isActive();
    }

    /**
     * Send message.
     *
     * @param msg the msg
     * @throws IOException if connection disconnect
     */
    public void sendMessage(String msg) throws IOException {
        if (!this.isConnected()) {
            throw new IOException("Connection disconnected.");
        }

        byte[] msgBytes = msg.getBytes(StandardCharsets.UTF_8);
        int msgLength = msgBytes.length;

        ByteBuf byteBuf = this.channelFuture.channel().alloc().directBuffer(4 + msgLength);
        byteBuf.writeInt(msgLength);
        byteBuf.writeBytes(msgBytes);

        this.channelFuture.channel().writeAndFlush(byteBuf);
    }
}