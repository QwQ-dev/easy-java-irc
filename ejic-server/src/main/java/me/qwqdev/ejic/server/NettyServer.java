package me.qwqdev.ejic.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import me.qwqdev.ejic.code.LengthHeaderFrameDecoder;
import me.qwqdev.ejic.server.data.ServiceData;

/**
 * The type Netty msg server.
 *
 * @author : qwq-dev InkerBot
 * @since : 2024-10-30 20:16
 */
public class NettyServer {
    private final ServiceData serviceData;

    /**
     * Instantiates a new Netty server.
     *
     * @param serviceData the service data
     */
    public NettyServer(ServiceData serviceData) {
        this.serviceData = serviceData;
    }

    /**
     * Start.
     */
    public void start() {
        String host = serviceData.getAddress();
        int port = serviceData.getPort();

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            socketChannel.pipeline().addLast(new LengthHeaderFrameDecoder());
                            socketChannel.pipeline().addLast(serviceData.createServiceHandler());
                        }
                    });

            ChannelFuture channelFuture = serverBootstrap.bind(host, port).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException exception) {
            throw new RuntimeException(exception);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
