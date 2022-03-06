package com.fablen.c1;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 多级目录拷贝
 */
public class TestFilesCopy {
    static Logger logger = LogManager.getLogger(TestFilesCopy.class.getName());

    public static void main(String[] args) throws IOException {

        String source = "/home/fablen/IdeaProjects/gs-netty-ws/to.txt";
        String target = "/home/fablen/IdeaProjects/gs-netty-ws/to.txtaaa";

        Files.walk(Paths.get(source)).forEach(path -> {
            try {
                String replace = path.toString().replace(source, target);
                //是目录
                if (Files.isDirectory(path)) {
                    Files.createDirectories(Paths.get(replace));
                }
                //是文件
                if (Files.isRegularFile(path)) {
                    Files.copy(path, Paths.get(replace));
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }

        });
    }
}
