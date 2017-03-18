package com.vincent.lwx.bean;

import java.io.Serializable;

import lombok.Data;

/**   
* @Title: FeedBack.java 
* @Package com.vincent.lwx.bean 
* @Description: TODO用户反馈
* @author Vincent   
* @date 2017年3月8日 上午12:06:19 
* @version V1.0   
*/
@Data
public class Feedback implements Serializable{

	/**
	 * 序列化id
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 反馈的用户手机号码
	 */
	private String phone;
	
	/**
	 * 反馈的标题
	 */
	private String title;
	
	/**
	 * 反馈的内容
	 */
	private String content;
	
	/**
	 * 创建时间
	 */
	private String createTime;

}


