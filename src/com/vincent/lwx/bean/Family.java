package com.vincent.lwx.bean;

import java.io.Serializable;

import lombok.Data;

/**    
* @Title: Family.java  
* @Package com.vincent.lwx.bean  
* @Description: TODO(用一句话描述该文件做什么)  
* @author A18ccms A18ccms_gmail_com    
* @date 2017年3月18日 下午7:48:29  
* @version V1.0    
*/

@Data
public class Family implements Serializable{
	
	/**
	 * 序列化ID 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * id
	 */
	private int id;
	/**
	 * 自己的手机
	 */
	private String phone;
	/**
	 * 家人的手机
	 */
	private String familyPhone;
	/**
	 * 给家人的备注
	 */
	private String remark;
	
	/**
	 * 添加的时间
	 */
	private String time;
	
}


