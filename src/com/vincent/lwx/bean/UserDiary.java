package com.vincent.lwx.bean;

import java.io.Serializable;

import lombok.Data;

/**   
* @Title: UserDiary.java 
* @Package com.vincent.lwx.bean 
* @Description: TODO(用一句话描述该文件做什么) 
* @author Vincent   
* @date 2017年3月4日 下午1:25:17 
* @version V1.0   
*/
@Data
public class UserDiary implements Serializable{

	/**
	 * 序列化id
	 */
	private static final long serialVersionUID = 1978187872109096960L;
	
	/**
	 * 用户id
	 */
	private String user_id;
	
	/**
	 * 日记title
	 */
	private String diaryTitle;
	
	/**
	 * 日记内容
	 */
	private String diaryContent;
	
	/**
	 * 记录日记的时间
	 */
	private String diaryTime;
	

}


