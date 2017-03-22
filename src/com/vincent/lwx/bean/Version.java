package com.vincent.lwx.bean;

import java.io.Serializable;

import lombok.Data;


/**    
* @Title: Version.java  
* @Package com.vincent.lwx.bean  
* @Description: TODO(用一句话描述该文件做什么)  
* @author A18ccms A18ccms_gmail_com    
* @date 2017年3月21日 下午9:59:24  
* @version V1.0    
*/

@Data
public class Version implements Serializable{
	
	/**
	 * 序列化ID
	 */
	private static final long serialVersionUID = 1L;

	private String phone;//手机号码
	
	private String version;//当前APP版本
	
	private String time;//最后登录时间
	
	private String android_version;//Android版本
	
	private String phoneModel;//手机型号
	
}


