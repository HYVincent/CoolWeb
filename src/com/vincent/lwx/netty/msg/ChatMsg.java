package com.vincent.lwx.netty.msg;

/**   
* @Title: ChatMsg.java 
* @Package com.vincent.lwx.netty.msg 
* @Description: TODO(用一句话描述该文件做什么) 
* @author Vincent   
* @date 2017年3月16日 下午11:43:42 
* @version V1.0   
*/
public class ChatMsg extends BaseMsg{

	/**
	 * 聊天消息 序列化Id
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 表示对方的手机号码
	 */
	private String oppositePhone;
	
	/**
	 * 聊天的内容
	 */
	private String charContent;

	
	
	public String getCharContent() {
		return charContent;
	}

	public void setCharContent(String charContent) {
		this.charContent = charContent;
	}

	public String getOppositePhone() {
		return oppositePhone;
	}

	public void setOppositePhone(String oppositePhone) {
		this.oppositePhone = oppositePhone;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	

}


