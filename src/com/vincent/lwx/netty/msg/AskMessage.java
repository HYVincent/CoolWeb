package com.vincent.lwx.netty.msg;

/**   
* @Title: AskMessage.java 
* @Package com.vincent.lwx.netty.msg 
* @Description: TODO(用一句话描述该文件做什么) 
* @author Vincent   
* @date 2017年3月17日 上午7:50:40 
* @version V1.0   
*/
public class AskMessage extends BaseMsg{
	
	/**
	 * 
	  	create table ask_msg(
		phone varchar(11) not null,
		from_phone varchar(11) not null,
		msgContent varchar(20) not null,
		remark varchar(100),
		type varchar(10) not null,
		status boolean not null,
		time varchar(20) not null
		)engine = InnoDB default charset = utf8;
	 * 
	 */
	
	
	/**
	 * 验证消息 序列化ID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 对方手机号码
	 */
	private String fromPhone;
	
	/**
	 * 是否通过，默认0，未通过，1通过
	 */
	private String remark;//
	
	/**
	 * 请求时间
	 */
	private String time;
	
	/**
	 * 状态 该消息是否已被推送 0 未推送，1已推送
	 */
	private String status;
	
	/**
	 * 消息内容
	 */
	private String msgContent;

	public String getFromPhone() {
		return fromPhone;
	}

	public void setFromPhone(String fromPhone) {
		this.fromPhone = fromPhone;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMsgContent() {
		return msgContent;
	}

	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}

	
	
	
}


