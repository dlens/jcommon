package com.dlens.common2.numeric;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


public class Log4JTest {
    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void test() {
        Logger logger = Logger.getLogger(getClass());
        logger.debug("Hello world.");
//        fail("Check the output to see if you got a hello world debug message.");
    }

}
