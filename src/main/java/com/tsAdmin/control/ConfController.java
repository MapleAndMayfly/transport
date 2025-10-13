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
    public void getConfig()
    {
        JSONObject config = ConfigLoader.getFullJson();
        renderJson(config);
    }
}
