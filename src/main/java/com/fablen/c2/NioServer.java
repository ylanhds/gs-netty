package com.fa.netty.c2;

import com.fa.netty.Utils.ByteBufferUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 使用 nio 来理解阻塞模式
 * 总结：阻塞模式所有的阻塞方法都会影响下一个方法的执行
 */
public class NioServer {
    static Logger logger = LogManager.getLogger(NioServer.class.getName());

    public static void main(String[] args) throws IOException {
        m1();
//        m2();
//        m3();
    }

    /**
     * 阻塞模式
     */
    private static void m1() throws IOException {
        // 使用 nio 来理解阻塞模式
        ByteBuffer buffer = ByteBuffer.allocate(16);
        // 1 创建服务器
        ServerSocketChannel ssc = ServerSocketChannel.open();
        // 2 绑定监听端口
        ssc.bind(new InetSocketAddress(8080));

        // 3 连接集合
        List<SocketChannel> channels = new ArrayList<>();
        while (true) {
            // 4 accept 建立与客户端连接，SocketChannel 用来与客户端之间通信
            logger.info("connection.....");
            // accept方法 默认是阻塞的 意味着线程停止运行  当连接建立以后就恢复
            SocketChannel sc = ssc.accept();
            logger.info("connected.....");
            channels.add(sc);
            for (SocketChannel channel : channels) {
                //5 接受客户端发送的数据
                logger.info("before read...{}", channel);
                // read方法 默认是阻塞的 线程不会运行
                channel.read(buffer);
                buffer.flip();
                ByteBufferUtil.debugAll(buffer);
                buffer.clear();
                logger.info("after read...{}", channel);
            }
        }
    }

    /**
     * 非阻塞模式
     */
    private static void m2() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(16);

        // 1 创建服务器
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);//accept方法 切换非阻塞(ssc.configureBlocking(false)) 线程还是继续运行 但是返回的sc 是 null

        // 2 绑定监听端口
        ssc.bind(new InetSocketAddress(8080));

        // 3 连接集合
        List<SocketChannel> channels = new ArrayList<>();
        while (true) {
            // 4 accept 建立与客户端连接，SocketChannel 用来与客户端之间通信
            logger.info("connection.....");
            SocketChannel sc = ssc.accept();
            if (sc != null) {
                logger.info("connected.....");
                sc.configureBlocking(false);// 当 SocketChannel 设置非阻塞 channel.read 线程还是继续运行
                channels.add(sc);

            }
            for (SocketChannel channel : channels) {
                //5 接受客户端发送的数据
                logger.info("before read...{}", channel);
                int read = channel.read(buffer); //read方法 切换非阻塞  线程会执行，如果没有返回数据。 read 返回 0
                if (read > 0) {
                    buffer.flip();
                    buffer.clear();
                    logger.info("after read...{}", channel);
                }
            }
        }
    }

    /**
     * 非阻塞模式-selector
     */
    private static void m3() throws IOException {
        // 1 创建selector 管理多个 channel
        Selector selector = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        /*
         *  1、 创建 selector 和 channel 的联系（注册）
         *  2、 SelectionKey 的含义：当事件发生时，可以知道 事件和哪个channel发生的事件
         *  备注：事件类型： accept 、connect、read、write
         *  accept: 有连接请求时触发
         *  connect: 客户端那边建立连接后触发
         *  read:    数据可读时
         *  write:   可写事件（）
         */
        SelectionKey sscKey = ssc.register(selector, 0, null);

        //感兴趣的事件 只关注的事件 例如：accept 事件
        sscKey.interestOps(SelectionKey.OP_ACCEPT);
        logger.debug("register key:{}", sscKey);

        ssc.bind(new InetSocketAddress(8080));
        while (true) {
            // 3 、select 方法 没有事件发生，就会让线程阻塞。
            // select 在事件未处理时，它不会阻塞。事件发生后，要么处理 要么取消 key.cancel();
            selector.select();
            // 4 、处理事件 Set<SelectionKey> 包含了所有发生的事件(sscKey.interestOps(SelectionKey....))
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                // 处理key 要把Set<SelectionKey>集合中删除
                iterator.remove();
                logger.debug("SelectionKey key: {}", key);
                //1 事件发生后， 要么取消 key.cancel();  要么处理
                //  key.cancel();
                // 5 事件区分类型
                if (key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel sc = channel.accept();
                    sc.configureBlocking(false);


                    SelectionKey scKey = sc.register(selector, 0, null);
                    scKey.interestOps(SelectionKey.OP_READ);
                    logger.debug("SocketChannel sc: {}", sc);
                    logger.debug("SSelectionKey scKey: {}", scKey);
                } else if (key.isReadable()) {
                    try {

                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(16);
                        int read = channel.read(buffer);
                        if (read == -1) {
                            key.cancel();
                        } else {
                            buffer.flip();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        key.cancel();
                    }
                }
            }

        }


    }
}

















