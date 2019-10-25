package com.yao.netty.privateprotocolstack.server;

import com.yao.netty.privateprotocolstack.Header;
import com.yao.netty.privateprotocolstack.base.MessageType;
import com.yao.netty.privateprotocolstack.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LoginAuthRespHandler extends ChannelInboundHandlerAdapter {

    private Map<String, Boolean> nodeCheck = new ConcurrentHashMap<String, Boolean>();

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //删除缓存，以便客户端重连
        ctx.close();
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //删除缓存，以便客户端重连
        nodeCheck.remove(ctx.channel().remoteAddress().toString());
        super.channelInactive(ctx);
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
        //如果是握手请求,处理,其他消息透传
        if (message.getHeader() != null && message.getHeader().getType() == MessageType.LOGIN_REQ.value()) {

            System.out.println("Login is OK");
            String nodeIndex = ctx.channel().remoteAddress().toString();
            System.out.println("nodeIndex:[" + nodeIndex + "]");
            //定义响应体
            NettyMessage resp = null;

            //验证
            if (nodeCheck.containsKey(nodeIndex)) {
                //重复登陆,拒绝
                resp = buildLoginResp((byte) -1);
            } else {
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                String ip = address.getAddress().getHostAddress();
                //IP验证.白名单我们就省略
                nodeCheck.put(nodeIndex,true);

                resp = buildLoginResp((byte) 0);
            }
            String body = (String) message.getBody();
            System.out.println("Recevied message body from client is" + body);
            //
            ctx.writeAndFlush(resp);
        } else {
            //透传到下层Handler处理
            ctx.fireChannelRead(msg);
        }
    }

    //创建消息响应体
    private NettyMessage buildLoginResp(byte response) {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.LOGIN_RESP.value());
        message.setBody(response);
        message.setHeader(header);
        return message;
    }
}
