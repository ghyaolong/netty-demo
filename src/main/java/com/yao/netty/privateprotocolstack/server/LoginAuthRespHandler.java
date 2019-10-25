package com.yao.netty.privateprotocolstack.server;

import com.yao.netty.privateprotocolstack.Header;
import com.yao.netty.privateprotocolstack.MessageType;
import com.yao.netty.privateprotocolstack.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LoginAuthRespHandler extends ChannelInboundHandlerAdapter {

    private Map<String, Boolean> nodeCheck = new ConcurrentHashMap<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
        if (message.getHeader() != null && message.getHeader().getType() == MessageType.LOGIN_REQ.value()) {
            System.out.println("握手信息正确");
            String nodeIndex = ctx.channel().remoteAddress().toString();
            System.out.println("当前nodeIndex：" + nodeIndex);
            NettyMessage resp = null;

            if (nodeCheck.containsKey(nodeIndex)) {
                resp = buildMessage((byte) -1);
            } else {
                nodeCheck.put(nodeIndex, true);
                resp = buildMessage((byte) 0);
            }
            String body = (String) message.getBody();

            System.out.println("recieved client message :" + body);
            ctx.writeAndFlush(resp);

        } else {
            ctx.fireChannelRead(msg);
        }

    }

    private NettyMessage buildMessage(byte resp) {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.LOGIN_RESP.value());
        message.setHeader(header);
        message.setBody(resp);
        return message;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(buildMessage());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        nodeCheck.remove(ctx.channel().remoteAddress().toString());
        super.channelInactive(ctx);
    }

    private NettyMessage buildMessage() {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.LOGIN_REQ.value());
        message.setHeader(header);
        return message;
    }
}
