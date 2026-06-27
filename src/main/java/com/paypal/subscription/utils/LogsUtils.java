package com.paypal.subscription.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogsUtils {

    private LogsUtils(){
        super();
    }
    public static Logger logger(){
        return LogManager.getLogger(Thread.currentThread().getStackTrace()[3].getClassName());
    }

    public static void info(String... message){
        logger().info(String.join("", message));
    }

    public static void error(String... message){
        logger().error(String.join("", message));
    }

}
