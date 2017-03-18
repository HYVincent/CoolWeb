package com.vincent.lwx.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.vincent.lwx.db.MyBatisUtils;
import com.vincent.lwx.netty.msg.AskMessage;

/**   
* @Title: AskMessageUtils.java 
* @Package com.vincent.lwx.controller 
* @Description: TODO(用一句话描述该文件做什么) 
* @author Vincent   
* @date 2017年3月17日 上午8:57:34 
* @version V1.0   
*/
public class AskMessageUtils {
	
	/**
	 * 查询是否存在我不在线等的时候别人发给我的验证消息
	 * @param phone
	 * @return
	 */
	public static List<AskMessage> selectNoSendAskMsg(String phone){
		String sql = "com.vincent.lwx.dao.ChatMapping.selectNoSendAskMsg";
		Map<String, String> map = new HashMap<>();
		map.put("phoneNum", phone);
		map.put("status", "0");
		SqlSession sqlSession = MyBatisUtils.getSqlSession();
		List<AskMessage> listMsg = sqlSession.selectList(sql, phone);
		if(listMsg!=null&listMsg.size()>0){
			System.out.println(AskMessageUtils.class.getName()+" 有未发送消息");
			return listMsg;
		}else{
			System.out.println(AskMessageUtils.class.getName()+" 没有未发送消息");
			return null;
		}
	}
	
}


