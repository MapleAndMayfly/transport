package com.tsAdmin.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * 读取通用 JSON 配置，从 resources/config.json 加载对应配置
 */
public final class ConfigLoader
{
    private static String configPath;
    private static JSONObject configData;

    static
    {
        // 从 resources/config.json 加载配置数据
        loadConfig("config.json");
    }

    private ConfigLoader(){}

    public static void loadConfig(String path)
    {
        configPath = path;
        loadConfig();
    }

    public static void loadConfig()
    {
        try
        {
            InputStream inputStream = ConfigLoader.class.getClassLoader().getResourceAsStream(configPath);
            if (inputStream == null)
            {
                throw new RuntimeException(configPath + " 文件丢失");
            }

            String jsonString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            JSONObject jsonObject = JSON.parseObject(jsonString);
            configData = jsonObject.getJSONObject("configs");

            if (configData == null)
            {
                throw new RuntimeException(configPath + " 中缺少 \"configs\" 节点");
            }

            inputStream.close();
        }
        catch (Exception e)
        {
            throw new RuntimeException("加载配置文件失败: " + e.getMessage(), e);
        }
    }

    public static String getString(String key) { return getString(key, "Lost String!"); }
    public static int getInt(String key) { return getInt(key, -1); }
    public static long getLong(String key) { return getLong(key, -1L); }
    public static double getDouble(String key) { return getDouble(key, -1.0); }
    public static boolean getBoolean(String key) { return getBoolean(key, false); }

    public static String getString(String key, String defaultValue)
    {
        String value = configData.getString(key);
        return value != null ? value : defaultValue;
    }
    public static Integer getInt(String key, Integer defaultValue)
    {
        Integer value = configData.getInteger(key);
        return value != null ? value : defaultValue;
    }
    public static Long getLong(String key, Long defaultValue)
    {
        Long value = configData.getLong(key);
        return value != null ? value : defaultValue;
    }
    public static Double getDouble(String key, Double defaultValue)
    {
        Double value = configData.getDouble(key);
        return value != null ? value : defaultValue;
    }
    public static Boolean getBoolean(String key, Boolean defaultValue)
    {
        Boolean value = configData.getBoolean(key);
        return value != null ? value : defaultValue;
    }

    public static boolean containsKey(String key)
    {
        return configData.containsKey(key);
    }

    public static JSONObject getAllConfigs()
    {
        return configData;
    }
}


