package com.tsAdmin.common;

import com.jfinal.config.*;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.render.ViewType;
import com.jfinal.template.Engine;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.kit.PropKit;
import com.tsAdmin.controller.MainController;

public class MainConfig extends JFinalConfig
{
    /**
     * 配置常量
     */
    @Override
    public void configConstant(Constants me)
    {
        PropKit.use(".config");
        me.setDevMode(PropKit.getBoolean("devMode", false));
        me.setError404View("/common/404.html");
        me.setError500View("/common/500.html");
        me.setViewType(ViewType.FREE_MARKER);
    }

    /**
     * 配置路由
     */
    @Override
    public void configRoute(Routes me)
    {
        me.add("/", MainController.class, "/");
    }

    /**
     * 配置插件
     */
    @Override
    public void configPlugin(Plugins me)
    {
        // 配置 Druid 数据库连接池插件
        DruidPlugin druidPlugin = createDruidPlugin(PropKit.getBoolean("devMode", false));
        me.add(druidPlugin);
        
        // 配置 ActiveRecord 插件
        ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
        me.add(arp);
    }

    /**
     * 配置全局拦截器
     */
    @Override
    public void configInterceptor(Interceptors me) {}

    /**
     * 配置处理器
     */
    @Override
    public void configHandler(Handlers me)
    {
        
    }

    @Override
    public void configEngine(Engine arg0) {}

    private DruidPlugin createDruidPlugin(boolean isDev)
    {
        String head = isDev ? "dev_" : "";
        return new DruidPlugin(
            PropKit.get(head + "jdbcUrl"),
            PropKit.get(head + "user"),
            PropKit.get(head + "password"));
    }
}
