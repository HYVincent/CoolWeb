package com.vincent.lwx.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.vincent.lwx.bean.ServiceStatus;
import com.vincent.lwx.bean.User;
import com.vincent.lwx.db.MyBatisUtils;
import com.vincent.lwx.netty.msg.MsgType;
import com.vincent.lwx.netty.msg.PushMsg;
import com.vincent.lwx.util.DateUtils;
import com.vincent.lwx.util.ResponseUtils;

/**
 * @Title: UserControl.java
 * @Package com.vincent.lwx.mapping
 * @Description: TODO(��һ�仰�������ļ���ʲô)
 * @author Vincent
 * @date 2017��3��3�� ����6:28:37
 * @version V1.0
 */
@Controller
public class UserController {

//	private static final Logger logger = LogManager.getLogger(UserController.class);

	/**
	 * 注册
	 * 
	 * @param phone
	 * @param password
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "register", method = RequestMethod.POST)
	public void registerUser(@RequestParam("phone") String phone, @RequestParam("password") String password,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			if (hasUserPhone(phone)) {
				// 用户已存在，无须再次注册
				ResponseUtils.renderJsonDataFail(response, ServiceStatus.RUNTIME_EXCEPTION, "该号码已被注册");
				return;
			} else {
				// 当前号码尚未注册
				String sql = "com.vincent.lwx.mapping.UserMapping.insertRegister";
				Map<String, String> parameter = new HashMap<>();
				parameter.put("phone", phone);
				parameter.put("password", password);
				parameter.put("createTime", DateUtils.getCurrentTimeStr());
				SqlSession sqlSession = MyBatisUtils.getSqlSession();
				sqlSession.insert(sql, parameter);
				// 注意，如果不提交，数据是不会写入到数据库的
				sqlSession.commit();
				sqlSession.clearCache();
				sqlSession.close();
				if (hasUserPhone(phone)) {
					// 注册成功
					ResponseUtils.renderJsonDataSuccess(response, "注册成功");
				} else {
					// 注册失败
					ResponseUtils.renderJsonDataFail(response, ServiceStatus.RUNTIME_EXCEPTION, "注册失败");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			ResponseUtils.rendserJsonServiceException(response, ServiceStatus.SERVICE_EXCEPTION,
					ServiceStatus.SERVICE_EXCEPTION_TEXT);
		}
	}

	/**
	 * 用户登录
	 * 
	 * @param phone
	 * @param password
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "login", method = RequestMethod.POST)
	public void login(@RequestParam("phone") String phone, @RequestParam("password") String password,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			User user = getUserPhone(phone);
			if (user != null) {
				// 登录
				if (user.getPassword().equals(password)) {
					// 登录成功，返回个人信息
					user.setPassword("************");
					ResponseUtils.renderJsonDataSuccess(response, "登录成功", user);
				} else {
					// 登录失败，提示用户
					ResponseUtils.renderJsonDataFail(response, ServiceStatus.RUNTIME_EXCEPTION, "账户或密码错误");
				}
			} else {
				// 尚未注册
				ResponseUtils.renderJsonDataFail(response, ServiceStatus.RUNTIME_EXCEPTION, "该手机号码尚未注册");
			}
		} catch (Exception e) {
			e.printStackTrace();
			ResponseUtils.rendserJsonServiceException(response, ServiceStatus.SERVICE_EXCEPTION,
					ServiceStatus.SERVICE_EXCEPTION_TEXT);
		}
	}

	/**
	 * 修改用户信息
	 * @param phone
	 * @param head
	 * @param sex
	 * @param birthday
	 * @param nickname
	 * @param live_status
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "updateUserInfo",method = RequestMethod.POST)
	public void alterUserInfo(@RequestParam("phone")String phone
			,@RequestParam("sex")String sex,@RequestParam("birthday")String birthday,@RequestParam("nickname")String nickname
			,@RequestParam("live_status")String live_status,HttpServletRequest request,HttpServletResponse response){
		try {
			User user = new User();
			user.setBirthday(birthday);
			user.setPhone(phone);
			user.setNickname(nickname);
			user.setSex(sex);
			user.setLive_status(live_status);
			String sql  = "com.vincent.lwx.mapping.UserMapping.updateUserInfo";
			SqlSession sqlSession = MyBatisUtils.getSqlSession();
			sqlSession.update(sql, user);
			sqlSession.commit();
			sqlSession.clearCache();
			sqlSession.close();
			if(getUserPhone(phone).getNickname().equals(nickname)){
				//修改成功
				ResponseUtils.renderJsonDataSuccess(response, "个人资料修改成功",getUserPhone(phone));
			}else{
				//修改失败
				ResponseUtils.renderJsonDataFail(response, ServiceStatus.RUNTIME_EXCEPTION, "个人资料修改失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
			ResponseUtils.rendserJsonServiceException(response, ServiceStatus.SERVICE_EXCEPTION,
					ServiceStatus.SERVICE_EXCEPTION_TEXT);
		}
	}
	
	/**
	 * 重置密码
	 * @param password
	 * @param user_id
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "resetPassword",method = RequestMethod.POST)
	public void updateUserPassword(@RequestParam("phone")String phone,@RequestParam("password")String password,
			HttpServletRequest request,HttpServletResponse response){
		try {
			String sql = "com.vincent.lwx.mapping.UserMapping.updateUserPassword";
			Map<String, String> map = new HashMap<>();
			map.put("phone", phone);
			map.put("password", password);
//			logger.debug(map);
			SqlSession sqlSession = MyBatisUtils.getSqlSession();
			sqlSession.update(sql, map);
			MyBatisUtils.commitTask(sqlSession);
			User user = getUserPhone(phone);
//			logger.debug(user);
			if(user.getPassword().equals(password)){
				//重置密码成功
				ResponseUtils.renderJsonDataSuccess(response, "重置密码成功");
			}else{
				//重置密码失败
				ResponseUtils.renderJsonDataFail(response, ServiceStatus.RUNTIME_EXCEPTION, "重置密码失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
			ResponseUtils.rendserJsonServiceException(response, ServiceStatus.SERVICE_EXCEPTION,
					ServiceStatus.SERVICE_EXCEPTION_TEXT);
		}
	}
	
	/**
	 * 修改用户密码
	 * @param phone
	 * @param old_password
	 * @param new_password
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "alterUserPassword",method = RequestMethod.POST)
	public void alterUserPassword(@RequestParam("phone")String phone,@RequestParam("old_password")String old_password,
		@RequestParam("new_password")String new_password,HttpServletRequest request,HttpServletResponse response){
		try {
			User user = getUserPhone(phone);
			if(user.getPassword().equals(old_password)){
				String sql = "com.vincent.lwx.mapping.UserMapping.updateUserPassword";
				Map<String, String> map = new HashMap<>();
				map.put("phone", phone);
				map.put("password", new_password);
//				logger.debug(map);
				SqlSession sqlSession = MyBatisUtils.getSqlSession();
				sqlSession.update(sql, map);
				MyBatisUtils.commitTask(sqlSession);
				User u = getUserPhone(phone);
				if(u.getPassword().equals(new_password)){
					//密码修改成功
					ResponseUtils.renderJsonDataSuccess(response, "密码已修改");
				}else{
					ResponseUtils.renderJsonDataFail(response, ServiceStatus.RUNTIME_EXCEPTION, "密码修改失败，请稍后重试");
				}
			}else{
				ResponseUtils.renderJsonDataFail(response, ServiceStatus.RUNTIME_EXCEPTION, "旧密码错误，请重试");
			}
		} catch (Exception e) {
			e.printStackTrace();
			ResponseUtils.rendserJsonServiceException(response, ServiceStatus.SERVICE_EXCEPTION,
					ServiceStatus.SERVICE_EXCEPTION_TEXT);
		}
		
	}
	
	/**
	 * 获取用户信息
	 * @param phone
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "getUserInfo",method = RequestMethod.GET)
	public void getUserInfo(@RequestParam("phone")String phone,HttpServletRequest request,HttpServletResponse response){
		User user = getUserPhone(phone);
		if(user!=null){
			user.setPassword("******");
			ResponseUtils.renderJsonDataSuccess(response, "获取成功", user);
		}else{
			ResponseUtils.renderJsonDataFail(response, ServiceStatus.RUNTIME_EXCEPTION, "用户信息获取失败");
		}
	}

	/**
	 * 模糊查询用户信息
	 * @param phone
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "searchUserLike",method = RequestMethod.POST)
	public void searchUserLike(@RequestParam("phone")String phone,HttpServletRequest request,HttpServletResponse response){
		try {
			String sql = "com.vincent.lwx.mapping.UserMapping.searchLikeFamily";
			SqlSession sqlSession = MyBatisUtils.getSqlSession();
			List<User> data = sqlSession.selectList(sql, phone+"%");
			MyBatisUtils.commitTask(sqlSession);
			if(data == null){
				ResponseUtils.renderJsonDataFail(response, ServiceStatus.RUNTIME_EXCEPTION, "没有数据");
			}else{
				ResponseUtils.renderJsonDataSuccess(response, "已找到如下数据",data);
			}
		} catch (Exception e) {
			e.printStackTrace();
			ResponseUtils.renderJsonDataFail(response, ServiceStatus.SERVICE_EXCEPTION, ServiceStatus.SERVICE_EXCEPTION_TEXT);
		}
	}
	
	/**
	 * 根据手机号码用户，若存在返回User对象，不存在则返回null
	 * 
	 * @param phone
	 * @return
	 */
	public User getUserPhone(String phone) {
		String sql = "com.vincent.lwx.mapping.UserMapping.selectUser";
		SqlSession session = MyBatisUtils.getSqlSession();
		User user = session.selectOne(sql, phone);
		session.commit();
		session.clearCache();
		session.close();
//		logger.debug(user);
		if (user != null) {
			// 用户已存在
			return user;
		} else {
			// 用户不存在
			return null;
		}
	}

	/**
	 * 根据手机号码查询用户是否存在
	 * 
	 * @param phone
	 * @return
	 */
	public boolean hasUserPhone(String phone) {
		String sql = "com.vincent.lwx.mapping.UserMapping.selectUser";
		SqlSession session = MyBatisUtils.getSqlSession();
		User user = session.selectOne(sql, phone);
		MyBatisUtils.getSqlSession().close();
		if (user != null) {
			// 用户已存在
			return true;
		} else {
			// 用户不存在
			return false;
		}
	}

}
