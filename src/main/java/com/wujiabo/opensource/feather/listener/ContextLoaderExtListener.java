package com.wujiabo.opensource.feather.listener;

import javax.servlet.ServletContextEvent;

import org.springframework.web.context.ContextLoaderListener;

import com.wujiabo.opensource.feather.customized.dao.CustomizedSqlLoader;

public class ContextLoaderExtListener extends ContextLoaderListener {

	@Override
	public void contextInitialized(ServletContextEvent event) {
		super.contextInitialized(event);
		CustomizedSqlLoader.getInstance();
	}
}
