package com.vincent.lwx.bean;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**   
* @Title: User.java 
* @Package com.vincent.lwx.bean 
* @Description: TODO(用一句话描述该文件做什么) 
* @author Vincent   
* @date 2017年3月3日 下午6:36:58 
* @version V1.0   
*/
public class User implements Serializable{

	/**
	 * 序列化id
	 */
	private static final long serialVersionUID = -6375697395831845246L;
	
	/**
	 * 用户id
	 */
	private @Getter String user_id;
	
	/**
	 * 用户手机号码
	 */
	private @Setter@Getter String phone;
	
	/**
	 * 密码
	 */
	private @Setter@Getter String password;
	
	/**
	 * 用户名
	 */
	private @Setter@Getter String nickname;
	
	/**
	 * 用户头像地址
	 */
	private @Setter@Getter String head;
	
	/**
	 * 性别
	 */
	private @Setter@Getter String sex;
	
	/**
	 * 生日
	 */
	private @Setter@Getter String birthday;
	
	/**
	 * 生活状态(发表的说说)
	 */
	private @Setter@Getter String live_status;
	
	/**
	 * 注册时间
	 */
	private @Getter String createTime;


}


