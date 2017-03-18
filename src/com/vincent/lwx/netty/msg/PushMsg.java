package com.vincent.lwx.netty.msg;

/**
 * @Project: schoolmallapi
 * @ClassName: PushMsg
 * @Description: 推送消息类型
 * @author:	chenpy
 * @date:	2016年11月1日
 * @version 1.0.0
 */
public class PushMsg extends BaseMsg {
    
    /**
	 * 序列化ID
	 */
	private static final long serialVersionUID = 1L;
	
	private String content;

    public PushMsg() {
        super();
        setType(MsgType.PUSH);
    }
    
	
	public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
