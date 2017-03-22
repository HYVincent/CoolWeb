package com.vincent.lwx.netty;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.session.SqlSession;

import com.vincent.lwx.db.MyBatisUtils;
import com.vincent.lwx.netty.msg.AskMessage;
import com.vincent.lwx.netty.msg.ChatMsg;
import com.vincent.lwx.netty.msg.LoginMsg;
import com.vincent.lwx.netty.msg.PushMsg;
import com.vincent.lwx.netty.msg.SystemMsg;
import com.vincent.lwx.util.SystemMsgUtils;

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
	 * 
	 * @param pushMsg
	 */
	public static boolean push(PushMsg pushMsg) {

		SocketChannel channel = NettyChannelMap.get(pushMsg.getPhoneNum());
		System.out.println("phone->" + pushMsg.getPhoneNum());
		if (channel != null) {
			System.out.println("channel not null，开始推送");
			channel.writeAndFlush(pushMsg);
			return true;
		} else {
			System.out.println("PushServer-->channel is null,please check code;");
			return false;
		}
	}

	/**
	 * 系统消息
	 * 
	 * @param systemMsg
	 * @return
	 */
	public static boolean push(SystemMsg systemMsg) {
		if(systemMsg == null){
			System.out.println("systemsMsg is null");
			return false;
		}
		Map<String,SocketChannel> map = NettyChannelMap.getMap();
		if(map!=null){
			System.out.println("systemMsg.getPhoneNum-->"+systemMsg.getPhoneNum());
			SocketChannel channel = map.get(systemMsg.getPhoneNum());
			System.out.println("phone->" + systemMsg.getPhoneNum());
			if (channel != null) {
				System.out.println("channel not null，开始推送");
				channel.writeAndFlush(systemMsg);
				return true;
			} else {
				System.out.println("PushServer-->channel is null,please check code;");
				return false;
			}
		}else{
			System.out.println("map is null");
			return false;
		}
		
	}

	/**
	 * 相当于是给自己推送消息 聊天消息推送
	 * 
	 * @param chatMsg
	 */
	public static boolean push(ChatMsg chatMsg) {
		SocketChannel channel = NettyChannelMap.get(chatMsg.getAsk_phone());
		System.out.println("phone->" + chatMsg.getPhoneNum());
		if (channel != null) {
			System.out.println("channel not null，开始推送");
			channel.writeAndFlush(chatMsg);
			return true;
		} else {
			System.out.println("PushServer-->channel is null,please check code;");
			System.out.println("这个人不在线，暂时发送不了");
			return false;
		}
	}

	/**
	 * 向某个用户添加请求消息
	 * 
	 * @param pushMsg
	 */
	public static boolean push(AskMessage askMessage) {
		SocketChannel channel = NettyChannelMap.get(askMessage.getPhoneNum());
		System.out.println("正在向" + askMessage.getPhoneNum() + "发送添加请求，这是" + askMessage.getFromPhone() + "发送的");
		if (channel != null) {
			System.out.println("channel not null，开始推送");
			channel.writeAndFlush(askMessage);
			return true;
		} else {
			System.out.println("PushServer-->channel is null,please check code;");
//			throw new NullPointerException("请检查服务器代码，channel object is null");
			return false;
		}
	}

	/**
	 * 向所有的用户推送消息
	 * 
	 * @param pushMsg
	 */
	public static boolean pushAllUser(PushMsg pushMsg) {
		try {
			Map<String, SocketChannel> map = NettyChannelMap.getMap();
			Set<String> key = map.keySet();// 获取所有的key值
			for (String phoneNumber : key) {
				// System.out.println(phoneNumber);
				SocketChannel s = map.get(phoneNumber);
				s.writeAndFlush(pushMsg);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 个所有的用户推送系统消息
	 * 
	 * @param systemMsg 消息和title需要在传入之前设置，手机和状态无需设置
	 * @return
	 */
	public static boolean pushAllUser(SystemMsg systemMsg) {
		try {
			List<String> phones = selectAllPhoneNum();
			// 保存起来
			for (String phone : phones) {
				systemMsg.setPhoneNum(phone);
				if (hasPhone(phone)) {
					// 在线
					push(systemMsg);
					systemMsg.setStatus("1");
					push(systemMsg);
				} else {
					systemMsg.setStatus("0");
				}
				SystemMsgUtils.saveSystemMsg(systemMsg);
			}
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 查询数据库注册的所有号码
	 * 
	 * @return
	 */
	public static List<String> selectAllPhoneNum() {
		String sql = "com.vincent.lwx.mapping.UserMapping.selectAllUserPhone";
		SqlSession sqlSession = MyBatisUtils.getSqlSession();
		List<String> phones = sqlSession.selectList(sql);
		if (phones != null) {
			return phones;
		} else {
			return null;
		}
	}

	/**
	 * 查询某个账号是否在线
	 * 
	 * @param phone
	 * @return
	 */
	public static boolean hasPhone(String phone) {
		boolean isExist = false;
		Map<String, SocketChannel> map = NettyChannelMap.getMap();
		System.out.println("map.size=" + map.size());
		Set<String> key = map.keySet();// 获取所有的key值
		for (String phoneMap : key) {
			if (phone.equals(phoneMap)) {
				isExist = true;
				// break;
				System.out.println("PushService-->在线");
			}
		}
		return isExist;
	}
}
