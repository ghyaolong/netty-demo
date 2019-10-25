package com.yao.netty.privateprotocolstack.client;

import com.yao.netty.privateprotocolstack.Header;
import com.yao.netty.privateprotocolstack.MessageType;
import com.yao.netty.privateprotocolstack.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import sun.plugin2.main.server.HeartbeatThread;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class HeartbeatReqHandler extends ChannelInboundHandlerAdapter {

    private volatile ScheduledFuture<?> scheduledFuture;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
        if (message.getHeader() != null && message.getHeader().getType() == MessageType.LOGIN_RESP.value()) {
            ctx.executor().scheduleAtFixedRate(new HeartbeatTask(ctx),0,5000, TimeUnit.MILLISECONDS);
        }else if(message.getHeader()!=null && message.getHeader().getType() == MessageType.HEATBEAT_RESP.value()){
            System.out.println("client receive server heartbeat message:"+message);
        }else{
            ctx.fireChannelRead(ctx);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause!=null){
            scheduledFuture.cancel(true);
        }else {
            scheduledFuture = null;
        }
    }

    private class HeartbeatTask implements Runnable {
        private ChannelHandlerContext ctx;

        public HeartbeatTask(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {
            NettyMessage message = buildMessage();
            System.out.println("client send sever heartbeat message:" + message);
            ctx.writeAndFlush(message);
        }
    }

    private NettyMessage buildMessage() {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.HEATBEAT_REQ.value());
        message.setHeader(header);
        return message;
    }
}
