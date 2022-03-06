package com.fablen.c1;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class TestFileChannelTransferTo {
    static Logger logger = LogManager.getLogger(TestFileChannelTransferTo.class.getName());


    public static void main(String[] args) {

        try (FileChannel from = new FileInputStream("data.txt").getChannel();
             FileChannel to = new FileOutputStream("to.txt").getChannel();
        ) {
            // 效果高 底层会利用操作系统的零拷贝进行优化。一次大 2G 数据
           long size = from.size();
            // left 变量代表还剩多少字节
            //
            for (long left = size; left > 0; ) {
                logger.trace("position: " + (size - left) + " left: " + left);
                left -= from.transferTo((size - left), left, to);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
