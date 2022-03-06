package com.fa.netty.c1;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Test;

public class Log4j2Test {
 
    public Logger logger = LogManager.getLogger(getClass());

    @Test
    public void test() {
        logger.error("COLOR ERROR");
        logger.warn("COLOR WARN");
        logger.info("COLOR INFO");
        logger.debug("COLOR DEBUG");
        logger.trace("COLOR TRACE");
    }
}