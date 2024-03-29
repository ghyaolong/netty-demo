package com.yao.netty.privateprotocolstack.base;

public enum MessageType {

    /**
     * 业务请求
     */
    BUSINESS_REQ((byte)0),
    /**
     * 业务应答
     */
    BUSINESS_RESP((byte)1),
    /**
     * 业务one_way请求(既是请求也是响应)
     */
    BUSINESS_REQ_RESP((byte)2),
    /**
     * 握手请求
     */
    LOGIN_REQ((byte)3),
    /**
     * 握手应答
     */
    LOGIN_RESP((byte)4),

    /**
     * 心跳请求
     */
    HEATBEAT_REQ((byte)5),

    /**
     * 心跳应答
     */
    HEATBEAT_RESP((byte)6),;


    private final byte value;

    private MessageType(byte v){
        value = v;
    }

    public byte value(){
        return value;
    }
}
