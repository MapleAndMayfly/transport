package com.tsAdmin.control;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.tsAdmin.common.ConfigLoader;
import java.util.HashMap;
import java.util.Map;

/**
 * 配置控制器
 * 主要处理前端对参数配置的更新
 */
public class ConfController extends Controller {
    public void getDefaultConfig() {
        ConfigLoader.use("config.json");
        JSONObject config = ConfigLoader.getFullJson();
        renderJson(config);
    }

    public void getAllPresets() {
        renderJson(DBManager.getAllPresets());
    }

    public void applyPreset() {
        String name = getPara("name");
        ConfigLoader.use(name);
        ConfigLoader.loadConfig();
        renderJson(createResult(true, "应用预设成功"));
    }

    public void startSimulation() { 
        Main.start(); 
        renderJson(createResult(true, "仿真系统启动成功"));
    }

    public void savePreset() {
        try {
            // 方法1：从请求体获取
            String rawData = getRawData();
            System.out.println("=== 保存预设调试信息 ===");
            System.out.println("请求方法: " + getRequest().getMethod());
            System.out.println("Content-Type: " + getRequest().getContentType());
            System.out.println("原始请求体数据: " + rawData);
            
            // 方法2：尝试从参数获取（兼容性）
            if (rawData == null || rawData.isEmpty()) {
                rawData = getPara("fullJson");
                System.out.println("从参数获取的数据: " + rawData);
            }
            
            if (rawData == null || rawData.isEmpty()) {
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
