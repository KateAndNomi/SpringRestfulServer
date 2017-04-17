package com.dmc.controller;

import java.util.List;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.dmc.database.JdbcDaoImp;

@SpringBootApplication
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		ApplicationContext context = new ClassPathXmlApplicationContext("com/dmc/controller/JdbcConfig.xml");
		JdbcDaoImp daoImp = (JdbcDaoImp) context.getBean("jdbcDaoImp");
		System.out.println("MAXX数据库配置完成,开始测试打印------->");
		List<Map<String, Object>> results = daoImp.getAllUsers();
		for (Map<String, Object> map : results) {
			String name = (String) map.get("name");
			System.out.println(name);
		}
		System.out.println("<--------测试完成");
		DatabaseMg.getInstance().setDefaultDaoImp(daoImp);
	}
}