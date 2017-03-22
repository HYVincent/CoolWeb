package com.vincent.lwx.netty.msg;


import lombok.Data;

/**    
* @Title: SystemMsg.java  
* @Package com.vincent.lwx.bean  
* @Description: TODO(用一句话描述该文件做什么)  
* @author A18ccms A18ccms_gmail_com    
* @date 2017年3月22日 上午1:37:14  
* @version V1.0    
*/
@Data
public class SystemMsg extends BaseMsg {
	
	/**
	 * 序列化ID
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
		create table systemMsg(
		phone varchar(11) not null,
		msgTitle varchar(50) not null,
		msgContent varchar(255) not null,
		status varchar(1) default 0,
		time varchar(30) not null)engine = InnoDB default charset = utf8;
	 */
	//title
	private String msgTitle;
	//消息内容
	private String msgContent;
	//状态 0 未发送 1已发送
	private String status;
	//消息产生时间
	private String time;
	
}


