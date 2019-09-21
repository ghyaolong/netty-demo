package com.yao.netty.second;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class MyClient  {

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup eventExecutors = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventExecutors).channel(NioSocketChannel.class).handler(new MyClientInitializer());

        ChannelFuture channelFuture = bootstrap.connect("localhost", 9999).sync();
        channelFuture.channel().closeFuture().sync();

        eventExecutors.shutdownGracefully();
    }
}
