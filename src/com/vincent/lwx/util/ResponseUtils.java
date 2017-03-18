package com.vincent.lwx.util;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

public class ResponseUtils {
	
	/**
	 * 返回数据
	 * @param response
	 * @param statusCode
	 * @param msg
	 * @param dataJson
	 */
	public static void renderJsonDataSuccess(HttpServletResponse response,String msg){
		try {  
		response.setCharacterEncoding("utf-8");  
        response.setHeader("Pragma", "No-cache");  
        response.setHeader("Cache-Control", "no-cache");  
        response.setDateHeader("Expires", 0);  
        response.getWriter().write("{\"status\":\""
        		+"1"+"\",\"errorCode\":\""
        		+""+"\",\"msg\":\""
        		+msg+"\",\"data\":"
        		+"\"\""+"}");  
        } catch (IOException e) { 
        	e.printStackTrace();
        }
	}
	
	/**
	 * 返回数据
	 * @param response
	 * @param statusCode
	 * @param msg
	 * @param dataJson
	 */
	public static void renderJsonDataSuccess(HttpServletResponse response,String msg,Object o){
		try {  
		response.setCharacterEncoding("utf-8");  
        response.setHeader("Pragma", "No-cache");  
        response.setHeader("Cache-Control", "no-cache");  
        response.setDateHeader("Expires", 0);  
        response.getWriter().write("{\"status\":\""
        		+"1"+"\",\"errorCode\":\""
        		+""+"\",\"msg\":\""
        		+msg+"\",\"data\":"
        		+JSON.toJSONString(o)+"}");  
        } catch (IOException e) { 
        	e.printStackTrace();
        }
	}
	 
	/**
	 * 返回数据
	 * @param response
	 * @param statusCode
	 * @param msg
	 * @param dataJson
	 */
	public static void renderJsonDataFail(HttpServletResponse response,
			int errorCode,String msg){
		try {  
		response.setCharacterEncoding("utf-8");  
        response.setHeader("Pragma", "No-cache");  
        response.setHeader("Cache-Control", "no-cache");  
        response.setDateHeader("Expires", 0);  
        response.getWriter().write("{\"status\":\""
        		+"-1"+"\",\"errorCode\":\""
        		+errorCode+"\",\"msg\":\""
        		+msg+"\",\"data\":"
        		+"\"\""+"}");  
        } catch (IOException e) { 
        	e.printStackTrace();
        }
	}
	
	public static void rendserJsonServiceException(HttpServletResponse response,
			int errorCode,String msg){
		try {  
		response.setCharacterEncoding("utf-8");  
        response.setHeader("Pragma", "No-cache");  
        response.setHeader("Cache-Control", "no-cache");  
        response.setDateHeader("Expires", 0);  
        response.getWriter().write("{\"status\":\""
        		+"0"+"\",\"errorCode\":\""
        		+errorCode+"\",\"msg\":\""
        		+msg+"\",\"data\":"
        		+"\"\""+"}");  
        } catch (IOException e) { 
        	e.printStackTrace();
        }
	}
}

