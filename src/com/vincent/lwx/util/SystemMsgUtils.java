package com.vincent.lwx.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.vincent.lwx.db.MyBatisUtils;
import com.vincent.lwx.netty.msg.SystemMsg;

/**
 * @Title: SystemMsgUtils.java
 * @Package com.vincent.lwx.util
 * @Description: TODO(用一句话描述该文件做什么)
 * @author A18ccms A18ccms_gmail_com
 * @date 2017年3月22日 上午2:43:53
 * @version V1.0
 */

public class SystemMsgUtils {
	
	
	public static void main(String[] args) {
		List<SystemMsg> data = getNoSendSystemMsg("18696855784");
		System.out.println(data.size());
		for(int i=0;i<data.size();i++){
			System.out.println("phone:"+data.get(i).getPhoneNum());
			System.out.println(data.get(i).getMsgTitle()+" "+data.get(i).getMsgContent());
			alterSystemMsgStatus(data.get(i));
		}
	}

	/**
	 * 查询尚未推送的消息
	 * 
	 * @param phone
	 * @return
	 */
	public static List<SystemMsg> getNoSendSystemMsg(String phone) {
		String sql = "com.vincent.lwx.dao.SystemMsgMapping.getNoSendSystemMsg";
		Map<String, String> map = new HashMap<>();
		map.put("phone", phone);
		map.put("status", "0");
		SqlSession sqlSession = MyBatisUtils.getSqlSession();
		List<SystemMsg> data = sqlSession.selectList(sql, map);
		return data;
	}
	
	

	/**
	 * 查询用户所有的系统消息
	 * 
	 * @param phone
	 * @return
	 */
	public static List<SystemMsg> getUserSystemMsg(String phone) {
		String sql = "com.vincent.lwx.dao.SystemMsgMapping.getUserAllSystemMsg";
		SqlSession sqlSession = MyBatisUtils.getSqlSession();
		List<SystemMsg> data = sqlSession.selectList(sql, phone);
		if (data != null) {
			return data;
		} else {
			return null;
		}
	}
	
	

	/**
	 * 把消息装填改了
	 * 
	 * @param systemMsg
	 * @return
	 */
	public static boolean alterSystemMsgStatus(SystemMsg systemMsg) {
		String sql = "com.vincent.lwx.dao.SystemMsgMapping.updateMySystemMsgStatus";
		Map<String, String> map = new HashMap<>();
		map.put("phone", systemMsg.getPhoneNum());
		map.put("msgTitle", systemMsg.getMsgTitle());
		map.put("status", "1");
		SqlSession sqlSession = MyBatisUtils.getSqlSession();
		sqlSession.update(sql, map);
		MyBatisUtils.commitTask(sqlSession);
		SystemMsg s = getSystemMsg(systemMsg.getPhoneNum(), systemMsg.getMsgTitle());
		if(s == null){
			System.out.println("s == null");
			return false;
		}
		if (s.getStatus().equals("1")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 保存到数据库
	 * 
	 * @param systemMsg
	 * @return
	 */
	public static boolean saveSystemMsg(SystemMsg systemMsg) {
		String sql = "com.vincent.lwx.dao.SystemMsgMapping.insertMySystemMsg";
		SqlSession sqlSession = MyBatisUtils.getSqlSession();
		sqlSession.insert(sql, systemMsg);
		MyBatisUtils.commitTask(sqlSession);
		if (getSystemMsg(systemMsg.getPhoneNum(), systemMsg.getMsgTitle()) != null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 根据手机号码和消息title查询系统消息
	 * 
	 * @param phone
	 * @param msgTitle
	 * @return
	 */
	public static SystemMsg getSystemMsg(String phone, String msgTitle) {
		String sql = "com.vincent.lwx.dao.SystemMsgMapping.selectSystemOne";
		Map<String, String> map = new HashMap<>();
		map.put("phone", phone);
		map.put("msgTitle", msgTitle);
		SqlSession sqlSession = MyBatisUtils.getSqlSession();
		SystemMsg msg = sqlSession.selectOne(sql, map);
		MyBatisUtils.commitTask(sqlSession);
		if (msg != null) {
			System.out.println("getSystemMsg-->有结果");
			return msg;
		} else {
			System.out.println("getSystemMsg-->没有数据");
			return null;
		}
	}

}
