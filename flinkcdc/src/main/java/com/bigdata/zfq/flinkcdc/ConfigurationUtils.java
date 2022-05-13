package com.bigdata.zfq.flinkcdc;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author: zhaofuqiang
 * @date: 2022/05/13 下午 10:48
 * @version: 1.0 配置文件工具类
 */

public class ConfigurationUtils {
    private static Properties properties = new Properties();

    //静态代码块
    static {
        InputStream resourceAsStream = ConfigurationUtils.class.getClassLoader().getResourceAsStream("custom.properties");
        try {
            properties.load(resourceAsStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String getProperties(String key) {
        return properties.getProperty(key);
    }

    //获取boolen类型的配置项
    public static boolean getBoolean(String key) {
        String myKey = properties.getProperty(key);

        try {
            return Boolean.valueOf(myKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
