package com.fablen.c2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class NioClient {
    static Logger logger = LogManager.getLogger(NioClient.class.getName());

    public static void main(String[] args) throws IOException {
        m1();
    }

    public static void m1() throws IOException {
        SocketChannel ssc = SocketChannel.open();
        ssc.connect(new InetSocketAddress("localhost", 8080));
        logger.debug("m1");
    }
}
