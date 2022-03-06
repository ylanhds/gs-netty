package com.fa.netty.c1;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 遍历文件夹
 */
public class TestFilesWalkFileTree {
    static Logger logger = LogManager.getLogger(TestFilesWalkFileTree.class.getName());

    public static void main(String[] args) throws IOException {

        m3();

    }


    //遍历文件夹
    public static void m1() throws IOException {
        AtomicInteger dirCunt = new AtomicInteger();
        AtomicInteger fileCunt = new AtomicInteger();

        Files.walkFileTree(Paths.get("/usr/lib/jvm/jdk-17"), new SimpleFileVisitor<Path>() {
            //进入文件之前执行的方法
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                logger.info("===============>" + dir);
                dirCunt.incrementAndGet();
                return super.preVisitDirectory(dir, attrs);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                logger.info(file);
                fileCunt.incrementAndGet();
                return super.visitFile(file, attrs);
            }
        });
        logger.info("dir count" + dirCunt);
        logger.info("file count" + fileCunt);
    }

    //查看有多少JAR
    public static void m2() throws IOException {
        AtomicInteger jarCunt = new AtomicInteger();

        Files.walkFileTree(Paths.get("/usr/local/java/jdk1.8.0_311"), new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(".jar")) {
                    logger.info(file);
                    jarCunt.incrementAndGet();
                }
                return super.visitFile(file, attrs);
            }
        });
        logger.info("jar count" + jarCunt);
    }

    //进入子目录--删除多级目录
    public static void m3() throws IOException {
        Files.walkFileTree(Paths.get("/home/fablen/logs"), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                logger.info("进入文件=====》" + dir);
                return super.preVisitDirectory(dir, attrs);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                // 可以删除文件 Files.delete(file);
                return super.visitFile(file, attrs);
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                logger.info("《===== 退出文件" + dir);
                // 可以删除文件夹  Files.delete(dir);
                return super.postVisitDirectory(dir, exc);
            }
        });
    }

}
