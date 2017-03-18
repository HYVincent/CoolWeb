package com.vincent.lwx.netty.msg;

/**
 * @Project: schoolmallapi
 * @ClassName: LoginMsg
 * @Description: 登录验证消息类型
 * @author:	chenpy
 * @date:	2016年11月1日
 * @version 1.0.0
 */
public class LoginMsg extends BaseMsg {
    private String userName;
    private String password;
    public LoginMsg() {
        super();
        setType(MsgType.LOGIN);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
