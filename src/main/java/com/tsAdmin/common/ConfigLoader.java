package com.tsAdmin.common;

import com.tsAdmin.control.DBManager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * 读取通用 JSON 配置，从 resources/config.json 加载对应配置
 */
public final class ConfigLoader
{
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static String usedConfig;
    private static JsonNode configData;

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
        JsonNode fullJson = getFullJson();
        configData = fullJson.get("configs");

        if (configData == null || configData.isNull())
        {
            throw new RuntimeException(usedConfig + " 中缺少 \"configs\" 节点");
        }
    }

    public static boolean containsKey(String key) { return configData != null && configData.has(key); }

    public static String getString(String key) { return getString(key, "Lost String!"); }
    public static int getInt(String key) { return getInt(key, -1); }
    public static long getLong(String key) { return getLong(key, -1L); }
    public static double getDouble(String key) { return getDouble(key, -1.0); }
    public static boolean getBoolean(String key) { return getBoolean(key, false); }

    public static String getString(String key, String defaultValue)
    {
        try
        {
            JsonNode configItem = configData.get(key);
            if (configItem == null || configItem.isNull()) return defaultValue;

            JsonNode valueNode = configItem.get("value");
            if (valueNode == null || valueNode.isNull()) return defaultValue;
            
            String value = valueNode.asText();
            return (value != null && !value.isEmpty()) ? value : defaultValue;
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
            JsonNode configItem = configData.get(key);
            if (configItem == null || configItem.isNull()) return defaultValue;

            JsonNode valueNode = configItem.get("value");
            if (valueNode == null || valueNode.isNull()) return defaultValue;
            
            return valueNode.asInt(defaultValue);
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
            JsonNode configItem = configData.get(key);
            if (configItem == null || configItem.isNull()) return defaultValue;

            JsonNode valueNode = configItem.get("value");
            if (valueNode == null || valueNode.isNull()) return defaultValue;
            
            return valueNode.asLong(defaultValue);
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
            JsonNode configItem = configData.get(key);
            if (configItem == null || configItem.isNull()) return defaultValue;

            JsonNode valueNode = configItem.get("value");
            if (valueNode == null || valueNode.isNull()) return defaultValue;
            
            return valueNode.asDouble(defaultValue);
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
            JsonNode configItem = configData.get(key);
            if (configItem == null || configItem.isNull()) return defaultValue;

            JsonNode valueNode = configItem.get("value");
            if (valueNode == null || valueNode.isNull()) return defaultValue;
            
            return valueNode.asBoolean(defaultValue);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static JsonNode getFullJson()
    {
        try
        {
            String jsonString = "";
            if (usedConfig.equals("config.json"))
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

            return objectMapper.readTree(jsonString);
        }
        catch (Exception e)
        {
            throw new RuntimeException("加载配置文件失败: " + e.getMessage(), e);
        }
    }
}


