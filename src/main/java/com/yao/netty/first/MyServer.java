package com.yao.netty.first;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author tututu
 * @description
 * @date 2019/9/17 23:59
 * @email 289911401@qq.com
 * @since V1.0.0
 */
public class MyServer {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup,workerGroup).channel(NioServerSocketChannel.class)
                .childHandler(new MyServerInitializer());

        ChannelFuture sync = serverBootstrap.bind(8899).sync();
        sync.channel().closeFuture().sync();

    }
}
