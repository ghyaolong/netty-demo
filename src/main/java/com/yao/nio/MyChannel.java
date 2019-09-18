package com.yao.nio;

import org.junit.Test;
import org.omg.CORBA.PUBLIC_MEMBER;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * NIO Channel Test
 *
 1.ByteBuffer   CharBuffer  ShortBuffer  DoubleBuffer FloatBuffer IntBuffer LongBuffer          通过allocate() 分配缓冲区

 (0 <= mark <= position <= limit <= capacity)

 2.缓冲区 vs 直接缓冲区

 allocate()   allocateDir()


 三. 获取通道 Channel
 1.   getChannel()
 2.   jdk1.7 针对各个通道提供了静态方法open()
 3.   jdk1.7 中的NIO.2的Files工具类的newByteChannel()

 四： 通道之间的数据传输
 transferFrom()
 transferTo()
 *
 *
 * 五：分散(Scatter) 于 聚集(Gather)
 *
 *  分散读取(Scattering Read)：将通道中的数据分散到多个缓冲区中
 *  聚集写入(Gathering Writes): 将多个缓冲区中的数据写入通道中
 *
 * 六：字符集
 */
public class MyChannel {

    /**
     * 通道之间的数据传输(直接缓冲区)
     * @throws IOException
     */
    @Test
    public void test3() throws IOException {
        FileChannel inChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("4.jpg"),StandardOpenOption.WRITE,StandardOpenOption.READ,StandardOpenOption.CREATE_NEW);

        inChannel.transferTo(0,inChannel.size(),outChannel);
        inChannel.close();
        outChannel.close();

    }

    /**
     * 使用直接缓冲区完成文件的复制
     */
    @Test
    public void test2() throws IOException {
        FileChannel inChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("3.jpg"),StandardOpenOption.WRITE,StandardOpenOption.READ,StandardOpenOption.CREATE_NEW);

        //内存映射文件
        MappedByteBuffer inMapperBuffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
        MappedByteBuffer outMapperBuffer = outChannel.map(FileChannel.MapMode.READ_WRITE, 0, inChannel.size());

        byte[] dst = new byte[inMapperBuffer.limit()];
        inMapperBuffer.get(dst);
        outMapperBuffer.put(dst);

        inChannel.close();
        outChannel.close();
    }


    /**
     * 利用通道完成文件的复制
     */
    @Test
    public void test() {
        FileInputStream fis = null;
        FileOutputStream fos = null;

        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            fis = new FileInputStream("1.jpg");
            fos = new FileOutputStream("2.jpg");
            inChannel = fis.getChannel();
            outChannel = fos.getChannel();

            ByteBuffer buf = ByteBuffer.allocate(1024);

            //将数据写到缓冲区
            while (inChannel.read(buf) != -1) {
                buf.flip();
                //读取缓冲器的文件写入通道
                outChannel.write(buf);
                buf.clear();
            }
        } catch (Exception e) {

        } finally {
            try {
                if (outChannel != null) {
                    outChannel.close();
                }
                if (inChannel != null) {
                    inChannel.close();
                }
                if (fos!=null){
                    fos.close();
                }
                if(fis!=null){
                    fis.close();
                }
            } catch (IOException e) {

            }

        }
    }
}
