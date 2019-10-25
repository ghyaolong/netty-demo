package com.yao.netty.privateprotocolstack.server;

import com.yao.netty.privateprotocolstack.Header;
import com.yao.netty.privateprotocolstack.MessageType;
import com.yao.netty.privateprotocolstack.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HeartbeatRespHandler extends ChannelInboundHandlerAdapter {

    private static int MAXIDLECOUNT = 5;
    private volatile Map<String, Integer> idleCount = new ConcurrentHashMap<>();

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;

            if (event.state() == IdleState.ALL_IDLE) {
                System.out.println("空闲");
                String nodeIndex = ctx.channel().remoteAddress().toString();
                int i = 1;
                if (idleCount.containsKey(nodeIndex)) {
                    i = idleCount.get(nodeIndex);
                    if (++i == MAXIDLECOUNT) {
                        ctx.close();
                    } else {
                        idleCount.put(nodeIndex, i);
                    }
                } else {
                    idleCount.put(nodeIndex, i);
                }
                System.out.println("空闲:" + i);
            }else{
                ctx.fireUserEventTriggered(evt);
            }

        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
        String nodeIndex = ctx.channel().remoteAddress().toString();
        if (idleCount.containsKey(nodeIndex)) {
            idleCount.remove(nodeIndex);
        }
        if (message.getHeader() != null && message.getHeader().getType() == MessageType.HEATBEAT_REQ.value()) {
            System.out.println("server receive client heartbeat message:" + message);
            ctx.writeAndFlush(buildMessage());
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    private NettyMessage buildMessage() {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.HEATBEAT_RESP.value());
        message.setHeader(header);
        return message;
    }
}
