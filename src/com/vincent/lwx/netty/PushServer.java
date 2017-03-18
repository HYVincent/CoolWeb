package com.vincent.lwx.netty;


import java.util.Map;
import java.util.Set;

import com.vincent.lwx.netty.msg.AskMessage;
import com.vincent.lwx.netty.msg.LoginMsg;
import com.vincent.lwx.netty.msg.PushMsg;

import io.netty.channel.socket.SocketChannel;

/**
 * netty推送服务端
 *
 * @author 徐飞
 * @version 2016/02/25 16:38
 */
public class PushServer {


	/**
	 * 向某个用户推送消息
	 * @param pushMsg
	 */
    public static boolean push(PushMsg pushMsg){
    	
        SocketChannel channel = NettyChannelMap.get(pushMsg.getPhoneNum());
        System.out.println("phone->"+pushMsg.getPhoneNum());
        if (channel != null) {
        	System.out.println("channel not null，开始推送");
            channel.writeAndFlush(pushMsg);
            return true;
        }else{
        	System.out.println("PushServer-->channel is null,please check code;");
        	throw new NullPointerException("请检查服务器代码，channel object is null");
        	
        }
    }
    
    /**
	 * 向某个用户添加请求消息
	 * @param pushMsg
	 */
    public static boolean push(AskMessage askMessage){
        SocketChannel channel = NettyChannelMap.get(askMessage.getPhoneNum());
        System.out.println("正在向"+askMessage.getPhoneNum()+"发送添加请求，这是"+askMessage.getFromPhone()+"发送的");
        if (channel != null) {
        	System.out.println("channel not null，开始推送");
            channel.writeAndFlush(askMessage);
            return true;
        }else{
        	System.out.println("PushServer-->channel is null,please check code;");
        	throw new NullPointerException("请检查服务器代码，channel object is null");
        }
    }
    
    
    /**
     * 向所有的用户推送消息
     * @param pushMsg
     */
    public static boolean pushAllUser(PushMsg pushMsg){
    	try{
    		Map<String,SocketChannel> map = NettyChannelMap.getMap();
        	Set<String> key = map.keySet();//获取所有的key值
        	for(String phoneNumber:key){
//        		System.out.println(phoneNumber);
        		SocketChannel s = map.get(phoneNumber);
        		s.writeAndFlush(pushMsg);
        	}
        	return true;
    	}catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
    }
    
    /**
     * 查询某个账号是否在线
     * @param phone
     * @return
     */
    public static boolean hasPhone(String phone){
    	boolean isExist = false;
    	Map<String,SocketChannel> map = NettyChannelMap.getMap();
    	System.out.println("map.size="+map.size());
    	Set<String> key = map.keySet();//获取所有的key值
    	for(String phoneMap:key){
    		if(phone.equals(phoneMap)){
    			isExist = true;
//    			break;
    			System.out.println("PushService-->在线");
    		}
    	}
    	return isExist;
    }
}
