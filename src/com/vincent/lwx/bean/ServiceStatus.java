package com.vincent.lwx.bean;

/**   
* @Title: ServiceStatus.java 
* @Package com.vincent.lwx.bean 
* @Description: TODO(用一句话描述该文件做什么) 
* @author Vincent   
* @date 2017年3月3日 下午10:22:29 
* @version V1.0   
*/
public class ServiceStatus {
	
	/*服务器错误*/
	public static final int SYSTEM_ERROR = 10001;
	/*服务器异常*/
	public static final int SERVICE_EXCEPTION = 10002;
	/*运行环境异常*/
	public static final int RUNTIME_EXCEPTION = 10003;
	/*服务器异常提示文字*/
	public static final String SERVICE_EXCEPTION_TEXT = "服务器异常，请稍后重试";

}


