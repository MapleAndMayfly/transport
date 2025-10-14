package com.tsAdmin.control;

import com.jfinal.core.Controller;

public class IndexController extends Controller
{
    public void index()
    {
        Main.start();
        render("index.html");
    }
}
