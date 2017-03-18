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

import com.vincent.lwx.bean.Feedback;
import com.vincent.lwx.bean.ServiceStatus;
import com.vincent.lwx.db.MyBatisUtils;
import com.vincent.lwx.util.DateUtils;
import com.vincent.lwx.util.ResponseUtils;

/**   
* @Title: FeedbackController.java 
* @Package com.vincent.lwx.controller 
* @Description: TODO(用一句话描述该文件做什么) 
* @author Vincent   
* @date 2017年3月8日 上午12:17:52 
* @version V1.0   
*/
@Controller
public class FeedbackController {

	/**
	 * 添加反馈
	 * @param phone
	 * @param title
	 * @param content
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "addFeedback",method = RequestMethod.POST)
	public void insertUserFeedback(@RequestParam("phone")String phone,@RequestParam("title")String title,@RequestParam("content")String content,
			HttpServletRequest request,HttpServletResponse response){
			SqlSession sqlSession = null;
			try {
				if(hasUserFeedback(phone, title)){
					ResponseUtils.renderJsonDataFail(response, ServiceStatus.RUNTIME_EXCEPTION, "已经存在相同标题的反馈，若要继续，请修改标题");
					return;
				}
				Feedback feedback = new Feedback();
				feedback.setPhone(phone);
				feedback.setContent(content);
				feedback.setTitle(title);
				feedback.setCreateTime(DateUtils.getCurrentTimeStr());
				String sql = "com.vincent.lwx.dao.FeedbackMapping.insert_add";
				sqlSession = MyBatisUtils.getSqlSession();
				sqlSession.insert(sql, feedback);
				MyBatisUtils.commitTask(sqlSession);
				if(hasUserFeedback(phone, title)){
					ResponseUtils.renderJsonDataSuccess(response, "反馈成功");
				}else{
					ResponseUtils.renderJsonDataFail(response, ServiceStatus.RUNTIME_EXCEPTION, "反馈失败");
				}
			} catch (Exception e) {
				e.printStackTrace();
				if(sqlSession != null){
					sqlSession.rollback(true);
					sqlSession.clearCache();
					sqlSession.close();
				}
				ResponseUtils.rendserJsonServiceException(response, ServiceStatus.SERVICE_EXCEPTION, ServiceStatus.SERVICE_EXCEPTION_TEXT);
			}
	}
	
	/**
	 * 获取反馈记录
	 * @param phone
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "getAllFeedback",method = RequestMethod.GET)
	public void getUserAllFeedback(@RequestParam("phone")String phone,HttpServletRequest request,HttpServletResponse response){
		try {
			String sql = "com.vincent.lwx.dao.FeedbackMapping.select_user_feedback";
			SqlSession sqlSession = MyBatisUtils.getSqlSession();
			List<Feedback> data = sqlSession.selectList(sql, phone);
			MyBatisUtils.commitTask(sqlSession);
			if(data != null){
				ResponseUtils.renderJsonDataSuccess(response, "获取反馈记录成功", data);
			}else{
				ResponseUtils.renderJsonDataSuccess(response, "没有历史反馈记录");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			ResponseUtils.rendserJsonServiceException(response, ServiceStatus.SERVICE_EXCEPTION, ServiceStatus.SERVICE_EXCEPTION_TEXT);
		}
	}
	
	/**
	 * 查询用户反馈记录
	 * @param phone
	 * @param title
	 * @return
	 */
	public boolean hasUserFeedback(String phone,String title){
		try{
			String sql = "com.vincent.lwx.dao.FeedbackMapping.select_user_item";
			Map<String, String> map = new HashMap<>();
			map.put("phone", phone);
			map.put("title", title);
			SqlSession sqlSession = MyBatisUtils.getSqlSession();
			Feedback feedback = sqlSession.selectOne(sql, map);
			if(feedback!=null){
				return true;
			}else{
				return false;
			}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
}


