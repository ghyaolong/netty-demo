package com.yao.netty.privateprotocolstack.codec.marshalling;

import com.yao.netty.privateprotocolstack.Header;
import com.yao.netty.privateprotocolstack.NettyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.util.HashMap;
import java.util.Map;

public class NettyMessageDecoder extends LengthFieldBasedFrameDecoder {

    private NettyMarshallingDecoder decoder;

    public NettyMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
        this.decoder = MarshallingCodecFactory.buildMarshallingDecoder();
    }

    public NettyMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
        this.decoder = MarshallingCodecFactory.buildMarshallingDecoder();
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            return null;
        }

        System.out.println(ctx.channel().remoteAddress().toString() + ":" + ByteBufUtil.hexDump(frame));
        //新建报文，填充数据
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setCrcCode(frame.readInt());
        header.setLength(frame.readInt());
        header.setSessionID(frame.readLong());
        header.setPriority(frame.readByte());

        int size = frame.readInt();
        if (size > 0) {
            Map<String, Object> attch = new HashMap<>(size);
            int keySize = 0;
            byte[] keyArray = null;
            String key = null;
            for (int i = 0; i < size; i++) {
                keySize = frame.readInt();
                keyArray = new byte[keySize];
                ByteBuf temp = in.readBytes(keyArray);
                key = new String(keyArray, "utf-8");
                attch.put(key, decoder.decode(ctx, in));
            }
            keyArray = null;
            key = null;
            header.setAttachment(attch);
        }

        if (frame.readableBytes() > 0) {
            message.setBody(decoder.decode(ctx, frame));
        }

        message.setHeader(header);


        return message;
    }
}
