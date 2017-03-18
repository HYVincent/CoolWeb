package com.vincent.lwx.netty.msg;

/**
 * @Project: schoolmallapi
 * @ClassName: MsgType
 * @Description: 消息类型枚举
 * @author:	chenpy
 * @date:	2016年11月1日
 * @version 1.0.0
 */
public enum  MsgType {
    PING,//客户端和服务器连接通道保持
    LOGIN,//客户端登录服务器消息
    PUSH,//服务器向客户端推送
    CHAT,//家人互通，聊天消息
    ASK//询问是否添加好友的消息
}
