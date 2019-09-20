package com.yao.nio;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;

/**
 * @author tututu
 * @description
 * @date 2019/9/20 21:53
 * @email 289911401@qq.com
 * @since V1.0.0
 */
public class TestNonBlockingNIO {

    @Test
    public void client() throws IOException {

        SocketChannel cChannel = SocketChannel.open(new InetSocketAddress(9999));
        cChannel.configureBlocking(true);

        ByteBuffer buf = ByteBuffer.allocate(1024);

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()){
            buf.put((new Date().toString()+scanner.next()).getBytes());
            buf.flip();
            cChannel.write(buf);
            buf.clear();
        }

        cChannel.close();

    }

    @Test
    public void server() throws IOException {
        ServerSocketChannel sChannel = ServerSocketChannel.open();
        sChannel.configureBlocking(true);
        sChannel.bind(new InetSocketAddress(9999));
        Selector selector = Selector.open();

        sChannel.register(selector, SelectionKey.OP_CONNECT);

        //轮训式的获取选择器上已经“准备就绪”的事件
        while (selector.select()>0){
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();

                /**
                 *在判断具体是什么事件
                 */
                //接受就绪事件
                if(key.isAcceptable()){
                    //服务端获取接收过来的客户端通道，并切换成横非阻塞模式
                    SocketChannel cChannel = sChannel.accept();
                    cChannel.configureBlocking(true);

                    //在将改客户端通道注册到Selector上
                    cChannel.register(selector,SelectionKey.OP_READ);
                }else if (key.isReadable()){
                    SocketChannel cChannel = (SocketChannel) key.channel();

                    //读取数据
                    ByteBuffer buf = ByteBuffer.allocate(1024);
                    int len = 0;
                    while ((len = cChannel.read(buf))>0){
                        buf.flip();
                        System.out.println(new String(buf.array(),0,len));
                        buf.clear();
                    }
                }

                iterator.remove();
            }

        }

    }
}
