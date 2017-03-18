package com.vincent.lwx.netty.msg;

import java.io.Serializable;

/**
 * @Project: schoolmallapi
 * @ClassName: BaseMsg
 * @Description: 
 * 消息基类
 * 必须实现序列，serialVersionUID 一定要有
 * @author:	chenpy
 * @date:	2016年11月1日
 * @version 1.0.0
 */
public abstract class BaseMsg implements Serializable {
    private static final long serialVersionUID = 1L;
    private MsgType type;
    //必须唯一，否者会出现channel调用混乱
    private String phoneNum;

    //初始化客户端id
    public BaseMsg() {
        this.phoneNum = Constants.getPhoneNum();
    }

    public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public MsgType getType() {
        return type;
    }

    public void setType(MsgType type) {
        this.type = type;
    }
}
