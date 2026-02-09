package com.autoworld.configprovider;

import org.testng.log4testng.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public final class ConfigProvider {
    private static Properties props;
    private static Map<String, Properties> configMap = new HashMap<>();
    private static Logger logger = Logger.getLogger(ConfigProvider.class);

    private ConfigProvider() {
    }

    private static Properties getInstance() {
        if (props == null) {
            props = loadProperties();
        }
        return props;
    }

    private static Properties getInstance(String fileName) {
        Properties prop = null;
        if (configMap.size() == 0) {
            prop = loadProperties(fileName);
            configMap.put(fileName, prop);
            return prop;
        } else {
            Iterator it = configMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Properties> entry = (Map.Entry) it.next();
                if (entry.getKey().equals(fileName)) {
                    return entry.getValue();
                }
            }
            prop = loadProperties(fileName);
            configMap.put(fileName, prop);
            return prop;
        }
    }

    private static Properties loadProperties() {
        Properties props = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream in;
        try {
            in = loader.getResourceAsStream("/properties/AutoWorld.properties");
            props.load(in);

        } catch (Exception e) {
            logger.info("AutoWorld.properties not found .. searching again");
            try {
                InputStream in1 = ConfigProvider.class.getResourceAsStream("/properties/AutoWorld.properties");
                props.load(in1);
                logger.info("AutoWorld.properties file found");
            } catch (IOException e1) {
                logger.error(e.getMessage());
                e1.printStackTrace();
            }
        }


        try {
            in = ConfigProvider.class.getResourceAsStream("/properties");
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String resource;
            while ((resource = br.readLine()) != null) {
                logger.info("Properties file found: " + resource);
                InputStream in1 = ConfigProvider.class.getResourceAsStream("/properties/" + resource);
                props.load(in1);
            }
            br.close();
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return props;
    }

    private static Properties loadProperties(String propertyFile) {
        Properties prop = new Properties();
        InputStream in = ConfigProvider.class.getResourceAsStream("/properties/" + propertyFile + ".properties");

        try {
            prop.load(in);
        } catch (IOException e) {
            logger.warn(propertyFile + ".properties file not found");
        }
        return prop;
    }

    public static String getAsString(String key) {
        return getInstance().getProperty(key);
    }

    public static int getAsInt(String key) {
        return Integer.parseInt(getInstance().getProperty(key));
    }

    public static String getAsString(String fileName, String key) {
        return getInstance(fileName).getProperty(key);
    }

    public static int getAsInt(String fileName, String key) {
        return Integer.parseInt(getInstance(fileName).getProperty(key));
    }

    public static String getAsString(String env, String fileName, String key) {
        Properties prop = new Properties();
        InputStream in = ConfigProvider.class.getResourceAsStream("/properties" + File.separator + env + "/" + fileName + ".properties");
        String value = null;

        try {
            prop.load(in);
            value = prop.getProperty(key);
            prop.clear();
            return value;

        } catch (IOException e) {
            logger.warn(e.getMessage());
        }
        return value;
    }
}
