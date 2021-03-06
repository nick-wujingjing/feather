package com.wujiabo.opensource.feather.web.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wujiabo.opensource.feather.mybatis.model.TUser;
import com.wujiabo.opensource.feather.web.bind.CurrentUser;

@Controller
public class IndexController {


    @RequestMapping("/")
    public String index(@CurrentUser TUser loginUser, Model model) {
        return "index";
    }

    /**
     * 基于角色 标识的权限控制案例
     */
    @RequestMapping(value = "/testr")
    @ResponseBody
    @RequiresRoles(value = "TEST_TESTR")
    public String admin() {
        return "Has Role";
    }

    /**
     * 基于权限标识的权限控制案例
     */
    @RequestMapping(value = "/testp")
    @ResponseBody
    @RequiresPermissions(value = "TEST_TESTP")
    public String create() {
        return "Has Permission";
    }


}
