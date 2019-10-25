package com.yao.netty.privateprotocolstack.codec.marshalling;

import io.netty.handler.codec.marshalling.DefaultMarshallerProvider;
import io.netty.handler.codec.marshalling.DefaultUnmarshallerProvider;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;

public class MarshallingCodecFactory {

    /**
     * 创建JBoss Marshlling解码器
     * @return
     */
    public static NettyMarshallingDecoder buildMarshallingDecoder() {
        MarshallerFactory factory = Marshalling.getProvidedMarshallerFactory("serial");
        MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);

        DefaultUnmarshallerProvider provider = new DefaultUnmarshallerProvider(factory, configuration);
        NettyMarshallingDecoder decoder = new NettyMarshallingDecoder(provider, 2014);
        return decoder;
    }

    /**
     * 创建JBoss Marshlling编码器
     * @return
     */
    public static NettyMarshallingEncoder buildMarshallingEncoder() {
        MarshallerFactory factory = Marshalling.getProvidedMarshallerFactory("serial");
        MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);

        DefaultMarshallerProvider provider = new DefaultMarshallerProvider(factory, configuration);
        NettyMarshallingEncoder encoder = new NettyMarshallingEncoder(provider);
        return encoder;
    }
}
