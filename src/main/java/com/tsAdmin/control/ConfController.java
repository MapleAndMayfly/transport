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
    public void getDefaultConfig()
    {
        ConfigLoader.use("config.json");
        JSONObject config = ConfigLoader.getFullJson();
        renderJson(config);
    }

    public void getAllPresets()
    {
        renderJson(DBManager.getAllPresets());
    }

    public void applyPreset()
    {
        String name = getPara("name");
        ConfigLoader.use(name);
        ConfigLoader.loadConfig();
    }

    public void startSimulation() { Main.start(); }

    public void savePreset()
    {
        String fullJson = getPara("fullJson");
        DBManager.savePreset(fullJson);
    }
}
