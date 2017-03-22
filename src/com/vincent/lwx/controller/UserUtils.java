package com.vincent.lwx.controller;

import org.apache.ibatis.session.SqlSession;

import com.vincent.lwx.bean.User;
import com.vincent.lwx.db.MyBatisUtils;

/**    
* @Title: UserUtils.java  
* @Package com.vincent.lwx.controller  
* @Description: TODO(用一句话描述该文件做什么)  
* @author A18ccms A18ccms_gmail_com    
* @date 2017年3月21日 上午10:47:23  
* @version V1.0    
*/

public class UserUtils {
	
	
	public static void main(String[] args) {
		User user = selectUserOne("18696855784");
		System.out.println(user.getHead());
	}
	
	/**
	 * 根据手机号码查询用户
	 * @param phone
	 * @return
	 */
	public static User selectUserOne(String phone){
		String sql = "com.vincent.lwx.mapping.UserMapping.selectUser";
		SqlSession sqlSession = MyBatisUtils.getSqlSession();
		User user = sqlSession.selectOne(sql, phone);
		MyBatisUtils.commitTask(sqlSession);
		if(user!=null){
			return user;
		}else{
			return null;
		}
		
	}
	
	/**
	 * 根据手机号码查询用户是否存在
	 * @param phone
	 * @return
	 */
	public static boolean hasUser(String phone){
		String sql = "com.vincent.lwx.mapping.UserMapping.selectUser";
		SqlSession sqlSession = MyBatisUtils.getSqlSession();
		User user = sqlSession.selectOne(sql, phone);
		MyBatisUtils.commitTask(sqlSession);
		if(user!=null){
			return true;
		}else{
			return false;
		}
	}
	
}


