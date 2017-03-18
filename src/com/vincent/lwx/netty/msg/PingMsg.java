package com.vincent.lwx.netty.msg;

/**
 * @Project: schoolmallapi
 * @ClassName: PingMsg
 * @Description: 心跳检测消息类型
 * @author:	chenpy
 * @date:	2016年11月1日
 * @version 1.0.0
 */
public class PingMsg extends BaseMsg {
    public PingMsg() {
        super();
        setType(MsgType.PING);
    }
}
