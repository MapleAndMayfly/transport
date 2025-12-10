package com.tsAdmin.common;

import com.tsAdmin.control.DBManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * 读取通用 JSON 配置，从 resources/config.json 加载对应配置
 */
public final class ConfigLoader
{
    private static String usedConfig;
    private static JSONObject configData;

    /**
     * 从 resources/config.json 加载配置数据
     */
    static
    {
        use("config.json");
    }

    private ConfigLoader(){}

    public static void use(String path)
    {
        usedConfig = path;
        loadConfig();
    }

    public static void loadConfig()
    {
        configData = getFullJson().getJSONObject("configs");

        if (configData == null)
        {
            throw new RuntimeException(usedConfig + " 中缺少 \"configs\" 节点");
        }
    }

    public static boolean containsKey(String key) { return configData.containsKey(key); }

    public static String getString(String key) { return getString(key, "Lost String!"); }
    public static int getInt(String key) { return getInt(key, -1); }
    public static long getLong(String key) { return getLong(key, -1L); }
    public static double getDouble(String key) { return getDouble(key, -1.0); }
    public static boolean getBoolean(String key) { return getBoolean(key, false); }

    public static String getString(String key, String defaultValue)
    {
        try
        {
            JSONObject configItem = configData.getJSONObject(key);
            if (configItem == null) return defaultValue;

            String value = configItem.getString("value");
            return value != null ? value : defaultValue;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return defaultValue;
        }
    }
    public static Integer getInt(String key, Integer defaultValue)
    {
        try
        {
            JSONObject configItem = configData.getJSONObject(key);
            if (configItem == null) return defaultValue;

            Integer value = configItem.getInteger("value");
            return value != null ? value : defaultValue;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return defaultValue;
        }
    }
    public static Long getLong(String key, Long defaultValue)
    {
        try
        {
            JSONObject configItem = configData.getJSONObject(key);
            if (configItem == null) return defaultValue;

            Long value = configItem.getLong("value");
            return value != null ? value : defaultValue;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return defaultValue;
        }
    }
    public static Double getDouble(String key, Double defaultValue)
    {
        try
        {
            JSONObject configItem = configData.getJSONObject(key);
            if (configItem == null) return defaultValue;

            Double value = configItem.getDouble("value");
            return value != null ? value : defaultValue;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return defaultValue;
        }
    }
    public static Boolean getBoolean(String key, Boolean defaultValue)
    {
        try
        {
            JSONObject configItem = configData.getJSONObject(key);
            if (configItem == null) return defaultValue;

            Boolean value = configItem.getBoolean("value");
            return value != null ? value : defaultValue;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static JSONObject getFullJson()
    {
        try
        {
            String jsonString = "";
            if (usedConfig == "config.json")
            {
                InputStream inputStream = ConfigLoader.class.getClassLoader().getResourceAsStream(usedConfig);
                if (inputStream == null) throw new RuntimeException(usedConfig + " 不存在！");

                jsonString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                inputStream.close();
            }
            else
            {
                jsonString = DBManager.getPreset(usedConfig);
            }

            return JSON.parseObject(jsonString);
        }
        catch (Exception e)
        {
            throw new RuntimeException("加载配置文件失败: " + e.getMessage(), e);
        }
    }
}


