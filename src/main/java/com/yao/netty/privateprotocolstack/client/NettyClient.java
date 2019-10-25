package com.yao.netty.privateprotocolstack.client;

import com.yao.netty.privateprotocolstack.codec.NettyMessageDecoder;
import com.yao.netty.privateprotocolstack.codec.NettyMessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NettyClient {

    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    private EventLoopGroup group = new NioEventLoopGroup();

    public void connect(String host,int port) throws InterruptedException {
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    //第一个参数是指数据包的最大值，第二个是协议中“长度”字段的偏移地址，第三个是“长度字段”的长度，第四个是针对2和3的一个容量修正，因为源码里面会会把参数2和3加起来，会使frameLength过大而抛异常
                                    .addLast(new NettyMessageDecoder(1024*1024,4,4,-8 ,0))
                                    .addLast(new NettyMessageEncoder())
                                    .addLast(new ReadTimeoutHandler(50))
                                    .addLast(new LoginAuthReqHandler())
                                    .addLast(new HeartbeatReqHandler());
                        }
                    });
            ChannelFuture future = bootstrap.connect(new InetSocketAddress(host,port),new InetSocketAddress("127.0.0.1", 7777)).sync();
            future.channel().closeFuture().sync();
        }finally {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        TimeUnit.SECONDS.sleep(5);
                        connect(host,port);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new NettyClient().connect("127.0.0.1",8888);
    }
}
