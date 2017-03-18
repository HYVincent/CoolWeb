package com.vincent.lwx.util;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.logging.log4j.web.Log4jServletContextListener;

/**   
* @Title: MyLog4jServletContextListener.java 
* @Package com.vincent.lwx.util 
* @Description: TODO(用一句话描述该文件做什么) 
* @author Vincent   
* @date 2017年3月4日 上午10:32:23 
* @version V1.0   
*/
public class MyLog4jServletContextListener extends Log4jServletContextListener{
	
	private ServletContext context;
	
	/**
	 * 当Servlet容器启动Web应用时调用该方法。在调用完该方法之后，容器再对Filter初始化，并且对那些在Web应用启动时就需要被初始化的Servlet进行初始化。
	 */
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		this.context = arg0.getServletContext();
		System.out.println(context.getContextPath());
	}
	
	/**
	 * 当Servlet容器终止Web应用时调用该方法。在调用该方法之前，容器会先销毁所有的Servlet和Filter过滤器。
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		// TODO Auto-generated method stub
		this.context = null;
	}
	
	
}


