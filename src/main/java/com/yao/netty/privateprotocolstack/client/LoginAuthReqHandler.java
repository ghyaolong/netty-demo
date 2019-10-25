package com.yao.netty.privateprotocolstack.client;

import com.yao.netty.privateprotocolstack.Header;
import com.yao.netty.privateprotocolstack.base.MessageType;
import com.yao.netty.privateprotocolstack.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class LoginAuthReqHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
        if (message.getHeader() != null && message.getHeader().getType() == MessageType.LOGIN_RESP.value()) {
            byte body = (byte) message.getBody();
            //如果应答结果不是0说明认证失败,关闭链路,重新发起链接
            if (body != (byte) 0) {
                System.out.println("握手失败");
                ctx.close();
            } else {
                System.out.println("login is OK: " + message);
                ctx.fireChannelRead(msg);
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(cause.getMessage());
        ctx.close();
    }

    /**
     * 当客户端与服务器TCP三次握手成功以后由客户端构造握手请求消息发送给服务端
     * 由于IP采用白名单认证机制,因此不需要携带消息体,消息题为空
     * 消息类型为"3:握手请求消息".握手请求发送后按照协议规范服务端需要返回握手应答消息
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(buildMessage());
    }

    private NettyMessage buildMessage() {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.LOGIN_REQ.value());
        message.setHeader(header);
        return message;
    }
}
