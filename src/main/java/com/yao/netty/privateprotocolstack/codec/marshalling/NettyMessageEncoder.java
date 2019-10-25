package com.yao.netty.privateprotocolstack.codec.marshalling;

import com.yao.netty.privateprotocolstack.NettyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;
import java.util.Map;

public class NettyMessageEncoder extends MessageToMessageEncoder<NettyMessage> {

    NettyMarshallingEncoder encoder;

    public NettyMessageEncoder() {
        this.encoder = MarshallingCodecFactory.buildMarshallingEncoder();
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, NettyMessage msg, List<Object> out) throws Exception {
        if (msg == null || msg.getHeader() == null) {
            throw new NullPointerException("The encode message is null");
        }

        ByteBuf sendBuf = Unpooled.buffer();
        sendBuf.writeInt(msg.getHeader().getCrcCode());
        sendBuf.writeInt(msg.getHeader().getLength());
        sendBuf.writeLong(msg.getHeader().getSessionID());
        sendBuf.writeByte(msg.getHeader().getType());
        sendBuf.writeByte(msg.getHeader().getPriority());
        sendBuf.writeInt(msg.getHeader().getAttachment().size());

        //遍历附件
        String key = null;
        Object value = null;
        byte[] keyArray = null;
        for (Map.Entry<String, Object> param : msg.getHeader().getAttachment().entrySet()) {
            key = param.getKey();
            keyArray = key.getBytes("utf-8");
            sendBuf.writeInt(keyArray.length);
            sendBuf.writeBytes(keyArray);
            value = param.getValue();
            encoder.encode(ctx, value, sendBuf);
        }

        key = null;
        value = null;
        keyArray = null;

        if (msg.getBody() != null) {
            encoder.encode(ctx, msg.getBody(), sendBuf);
        }

        sendBuf.setInt(4, sendBuf.readableBytes());
        int bytes = sendBuf.readableBytes();
        sendBuf.setInt(4, bytes);
        out.add(sendBuf);

    }
}
