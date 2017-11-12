package com.sample.configuration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import io.swagger.jaxrs.config.BeanConfig;

public class SwaggerConfig extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	@Override
	    public void init(ServletConfig servletConfig) {
	        try {
	            super.init(servletConfig);
	            
	            BeanConfig beanConfig = new BeanConfig();
	            beanConfig.setBasePath("/scheduler/rest/");
	            beanConfig.setHost("localhost:8080");
	            beanConfig.setSchemes(new String[]{"http"});
	            beanConfig.setVersion("1.0.0");
	            beanConfig.setScan(true);
	            beanConfig.setResourcePackage("com.sample.controller");
	        } catch (ServletException e) {
	            System.out.println(e.getMessage());
	        }
	    }
}
