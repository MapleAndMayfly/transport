package com.tsAdmin.control;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfinal.core.Controller;
import com.tsAdmin.common.ConfigLoader;
import java.util.HashMap;
import java.util.Map;

/**
 * 配置控制器
 * 主要处理前端对参数配置的更新
 */
public class ConfController extends Controller
{
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /** 获取默认配置 */
    public void getDefaultConfig()
    {
        ConfigLoader.use("config.json");
        JsonNode config = ConfigLoader.getFullJson();
        // 将 JsonNode 转换为字符串，确保与 JFinal 的 renderJson 兼容
        try {
            String jsonString = objectMapper.writeValueAsString(config);
            renderJson(jsonString);
        } catch (Exception e) {
            renderJson(createResult(false, "配置序列化失败: " + e.getMessage()));
        }
    }

    /** 获取所有预设 */
    public void getAllPresets()
    {
        renderJson(DBManager.getAllPresets());
    }

    /** 应用预设，需要传入预设名 name */
    public void applyPreset()
    {
        String name = getPara("name");
        ConfigLoader.use(name);
        ConfigLoader.loadConfig();
        renderJson(createResult(true, "应用预设成功"));
    }

    /** 按照已应用的预设开始模拟 */
    public void startSimulation()
    {
        Main.start();
        renderJson(createResult(true, "仿真系统启动成功"));
    }

    public void savePreset()
    {
        try
        {
            // 方法1：从请求体获取
            String rawData = getRawData();
            
            // 方法2：尝试从参数获取（兼容性）
            if (rawData == null || rawData.isEmpty())
            {
                rawData = getPara("fullJson");
                System.out.println("从参数获取的数据: " + rawData);
            }
            
            if (rawData == null || rawData.isEmpty())
            {
                System.err.println("错误：请求数据为空");
                renderJson(createResult(false, "请求数据为空"));
                return;
            }
            
            System.out.println("最终要保存的数据: " + rawData);
            
            // 保存到数据库
            DBManager.savePreset(rawData);
            System.out.println("保存预设完成");
            
            renderJson(createResult(true, "保存成功"));
        } catch (Exception e) {
            System.err.println("保存预设异常: " + e.getMessage());
            e.printStackTrace();
            renderJson(createResult(false, "保存失败: " + e.getMessage()));
        }
    }

    private Map<String, Object> createResult(boolean success, String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("message", message);
        return result;
    }
}
