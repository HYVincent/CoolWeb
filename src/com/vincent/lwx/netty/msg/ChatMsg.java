package com.vincent.lwx.netty.msg;

import lombok.Data;

/**   
* @Title: ChatMsg.java 
* @Package com.vincent.lwx.netty.msg 
* @Description: TODO(用一句话描述该文件做什么) 
* @author Vincent   
* @date 2017年3月16日 下午11:43:42 
* @version V1.0   
*/
@Data
public class ChatMsg extends BaseMsg{

	/**
	 * 聊天消息 序列化Id
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 表示对方的手机号码
	 */
	private String ask_phone;
	
	/**
	 * 聊天的内容
	 */
	private String chatContent;
	
	/**
	 * 类型，1 表示我发出去的， 0表示别人发给我的
	 */
	private String msgType;



}


