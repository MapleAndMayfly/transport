package com.tsAdmin.control;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.tsAdmin.common.ConfigLoader;

/**
 * 配置控制器
 * 主要处理前端对参数配置的更新
 */
public class ConfController extends Controller
{
    /** 获取默认配置 */
    public void getDefaultConfig()
    {
        ConfigLoader.use("config.json");
        JSONObject config = ConfigLoader.getFullJson();
        renderJson(config);
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
    }

    /** 按照已应用的预设开始模拟 */
    public void startSimulation() { Main.start(); }

    /** 保存预设，需要传入全部 JSON 数据 */
    public void savePreset()
    {
        String fullJson = getPara("fullJson");
        DBManager.savePreset(fullJson);
    }
}
