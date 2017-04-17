package com.dmc.database;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;  
public class JdbcDaoImp {  
    private DataSource datasource;  
    private JdbcTemplate jdbcTemplateObject;  
      
    public void setdatasource(DataSource ds) {  
        this.datasource = ds;  
        this.jdbcTemplateObject = new JdbcTemplate(datasource);     
    }  
    
    public List<Map<String, Object>> getAllUsers(){
    	List<Map<String, Object>> results=jdbcTemplateObject.queryForList("select * from USER");
    	System.out.println("返回的数量是:"+results.size());
    	return results;
    }
  
}  