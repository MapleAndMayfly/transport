package com.tsAdmin.control;

import com.jfinal.core.Controller;

public class IndexController extends Controller
{
    public void index()
    {
        Main.init();
        render("index.html");
    }
}
