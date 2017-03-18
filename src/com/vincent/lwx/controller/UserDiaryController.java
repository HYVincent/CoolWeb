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
import com.vincent.lwx.bean.UserDiary;
import com.vincent.lwx.db.MyBatisUtils;
import com.vincent.lwx.util.DateUtils;
import com.vincent.lwx.util.ResponseUtils;

/**   
* @Title: UserDiaryController.java 
* @Package com.vincent.lwx.controller 
* @Description: TODO(用一句话描述该文件做什么) 
* @author Vincent   
* @date 2017年3月4日 下午1:39:37 
* @version V1.0   
*/
@Controller
public class UserDiaryController {

	/**
	 * 插入日记
	 * @param user_id
	 * @param diaryTitle
	 * @param diaryContent
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "insertDiary",method = RequestMethod.POST)
	public void insertDiary(@RequestParam("user_id")String user_id,@RequestParam("diaryTitle")String diaryTitle,@RequestParam("diaryContent")String diaryContent,
			HttpServletRequest request,HttpServletResponse response){
		try{
			if(hasUserDiary(user_id, diaryTitle)){
				//记录已存在
				ResponseUtils.renderJsonDataFail(response, ServiceStatus.RUNTIME_EXCEPTION, "该日记标题已存在,请修改日记标题重试");
				return;
			}
			UserDiary userDiary = new UserDiary();
			userDiary.setDiaryContent(diaryContent);
			userDiary.setDiaryTime(DateUtils.getCurrentTimeStr());
			userDiary.setDiaryTitle(diaryTitle);
			userDiary.setUser_id(user_id);
			String sql = "com.vincent.lwx.dao.UserDiaryMapping.insertDiary";
			SqlSession sqlSession = MyBatisUtils.getSqlSession();
			sqlSession.insert(sql, userDiary);
			MyBatisUtils.commitTask(sqlSession);
			UserDiary u = hasUserDiaryForTitle(user_id, diaryTitle);
			if(u.getUser_id().equals(user_id)&&u.getDiaryTitle().equals(diaryTitle)){
				ResponseUtils.renderJsonDataSuccess(response, "日记已保存");
			}else{
				ResponseUtils.renderJsonDataFail(response, ServiceStatus.RUNTIME_EXCEPTION, "日记保存失败");
			}
		}catch(Exception e){
			e.printStackTrace();
			ResponseUtils.rendserJsonServiceException(response, ServiceStatus.SERVICE_EXCEPTION,
					ServiceStatus.SERVICE_EXCEPTION_TEXT);
		}
		
	}
	
	/**
	 * 删除用户所有的日记
	 * @param user_id
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "deleteUserAllDiary",method = RequestMethod.POST)
	public void deleteUserAllDiary(@RequestParam("user_id")String user_id,HttpServletRequest request,HttpServletResponse response){
		try {
			String sql = "com.vincent.lwx.dao.UserDiaryMapping.deleteAllDiaryForId";
			SqlSession sqlSession = MyBatisUtils.getSqlSession();
			sqlSession.delete(sql,user_id);
			MyBatisUtils.commitTask(sqlSession);
			if(hasUserDiary(user_id)){
				//删除失败
				ResponseUtils.renderJsonDataFail(response, ServiceStatus.RUNTIME_EXCEPTION, "删除失败");
			}else{
				//删除成功
				ResponseUtils.renderJsonDataSuccess(response, "已删除所有日记");
			}
		} catch (Exception e) {
			e.printStackTrace();
			ResponseUtils.rendserJsonServiceException(response, ServiceStatus.SERVICE_EXCEPTION,
					ServiceStatus.SERVICE_EXCEPTION_TEXT);
		}
	}
	
	
	/**
	 * 根据用户id和日记title删除
	 * @param user_id
	 * @param diaryTitle
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "deleteOneDiary",method = RequestMethod.POST)
	public void deleteUserDiaryForIdAndTitle(@RequestParam("user_id")String user_id,@RequestParam("diaryTitle")String diaryTitle,
			HttpServletRequest request,HttpServletResponse response){
		try {
			String sql = "com.vincent.lwx.dao.UserDiaryMapping.deleteDiaryForIdAndTitle";
			SqlSession sqlSession = MyBatisUtils.getSqlSession();
			Map<String, String> map = new HashMap<>();
			map.put("user_id", user_id);
			map.put("diaryTitle", diaryTitle);
			sqlSession.delete(sql, map);
			MyBatisUtils.commitTask(sqlSession);
			if(hasUserDiary(user_id, diaryTitle)){
				//删除失败
				ResponseUtils.renderJsonDataFail(response, ServiceStatus.RUNTIME_EXCEPTION, "删除失败");
			}else{
				//删除成功
				ResponseUtils.renderJsonDataSuccess(response, "已删除");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			ResponseUtils.rendserJsonServiceException(response, ServiceStatus.SERVICE_EXCEPTION,
					ServiceStatus.SERVICE_EXCEPTION_TEXT);
		}
	}
	
	/**
	 * 获取用户的所有日记
	 * @param user_id
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "getUserAllDiary")
	public void getUserAllDiaryForUserId(@RequestParam("user_id")String user_id,HttpServletRequest request,HttpServletResponse response){
		try{
			List<UserDiary> data = getUserAllDiaryForId(user_id);
			if(data != null){
				ResponseUtils.renderJsonDataSuccess(response, "获取所有日记成功", data);
			}else{
				ResponseUtils.renderJsonDataSuccess(response, "没有日记");
			}
		}catch(Exception e){
			e.printStackTrace();
			ResponseUtils.rendserJsonServiceException(response, ServiceStatus.SERVICE_EXCEPTION,
					ServiceStatus.SERVICE_EXCEPTION_TEXT);
		}
	}
	
	/**
	 * 该用户是否存在日记 true you  false 没有
	 * @param user_id
	 * @param request
	 * @param response
	 */
	public boolean hasUserDiary(String user_id){
		SqlSession sqlSession = MyBatisUtils.getSqlSession();
		String sql = "com.vincent.lwx.dao.UserDiaryMapping.selectAllDiary";
		List<UserDiary> data = sqlSession.selectList(sql, user_id);
		if(data.size()>0){
			return true;
		}else{
			return false;
		}
	}

	
	/**
	 * 获取用户所有的日记
	 * @param user_id
	 * @return
	 */
	public List<UserDiary> getUserAllDiaryForId(String user_id){
		SqlSession sqlSession = MyBatisUtils.getSqlSession();
		String sql = "com.vincent.lwx.dao.UserDiaryMapping.selectAllDiary";
		List<UserDiary> data = sqlSession.selectList(sql, user_id);
		try {
			if(data.size()>0){
				return data;
			}else{
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 查询某个title记录是否存在
	 * @param user_id
	 * @param title
	 * @return
	 */
	public UserDiary hasUserDiaryForTitle(String user_id,String diaryTitle){
		Map<String, String> map = new HashMap<>();
		map.put("user_id", user_id);
		map.put("diaryTitle", diaryTitle);
		String sql = "com.vincent.lwx.dao.UserDiaryMapping.selectDiary";
		SqlSession sqlSession = MyBatisUtils.getSqlSession();
		UserDiary diary = sqlSession.selectOne(sql, map);
		MyBatisUtils.commitTask(sqlSession);
		if(diary != null){
			return diary;
		}else{
			return null;
		}
		
	}
	
	
	/**
	 * 查询某个title记录是否存在
	 * @param user_id
	 * @param title
	 * @return
	 */
	public boolean hasUserDiary(String user_id,String diaryTitle){
		Map<String, String> map = new HashMap<>();
		map.put("user_id", user_id);
		map.put("diaryTitle", diaryTitle);
		String sql = "com.vincent.lwx.dao.UserDiaryMapping.selectDiary";
		SqlSession sqlSession = MyBatisUtils.getSqlSession();
		UserDiary diary = sqlSession.selectOne(sql, map);
		MyBatisUtils.commitTask(sqlSession);
		if(diary != null){
			return true;
		}else{
			return false;
		}
		
	}
	
}


