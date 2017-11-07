package com.sample.configuration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import com.wordnik.swagger.jaxrs.config.BeanConfig;


@WebServlet(name = "SwaggerJaxrsConfig", loadOnStartup = 1)
public class SwaggerConfig extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	@Override
	    public void init(ServletConfig servletConfig) {
	        try {
	            super.init(servletConfig);
	            
	            BeanConfig beanConfig = new BeanConfig();
	            beanConfig.setBasePath("http://localhost:8080/springproject/");
	            beanConfig.setVersion("1.0.0");
	            beanConfig.setScan(true);
	            beanConfig.setResourcePackage("com.sample.controller");
	        } catch (ServletException e) {
	            System.out.println(e.getMessage());
	        }
	    }
}
