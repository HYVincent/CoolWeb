package com.vincent.lwx.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSON;
import com.vincent.lwx.bean.App;
import com.vincent.lwx.bean.ServiceStatus;
import com.vincent.lwx.bean.Version;
import com.vincent.lwx.db.MyBatisUtils;
import com.vincent.lwx.util.DateUtils;
import com.vincent.lwx.util.ResponseUtils;

import lombok.Data;

/**   
* @Title: AppController.java 
* @Package com.vincent.lwx.controller 
* @Description: TODO(用一句话描述该文件做什么) 
* @author Vincent   
* @date 2017年3月16日 下午12:22:08 
* @version V1.0   
*/
@Controller
public class AppController {
	
	
	/**
	 * 插入版本信息
	 * @param phone
	 * @param version
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "insertVersion",method = RequestMethod.POST)
	public void insertVersion(@RequestParam("phone")String phone,@RequestParam("version")String version,
			@RequestParam("android_version")String android_version,@RequestParam("phoneModel")String phoneModel,HttpServletRequest request,HttpServletResponse response){
		Version currentVersion = null;
		try{
			currentVersion = getCurrentVersion(phone);
			if(currentVersion == null){
				//说明之前没有注册过
				Version ver = new Version();
				ver.setPhone(phone);
				ver.setTime(DateUtils.getCurrentTimeStr());
				ver.setVersion(version);
				ver.setAndroid_version(android_version);
				ver.setPhoneModel(phoneModel);
				SqlSession sqlSession = MyBatisUtils.getSqlSession();
				String insertVersion = "com.vincent.lwx.dao.VersionMapping.insertVersionDetail";
				sqlSession.insert(insertVersion, ver);
				MyBatisUtils.commitTask(sqlSession);
				if(getCurrentVersion(phone).getVersion().equals(version)){
					ResponseUtils.renderJsonDataSuccess(response, "版本信息写入成功");
				}else{
					ResponseUtils.renderJsonDataFail(response, ServiceStatus.RUNTIME_EXCEPTION, "版本信息写入失败");
				}
			}else{
				if(currentVersion.getVersion().equals(version)){
					System.out.println("说明当前版本已是最新版本了，不用更新数据库");
					ResponseUtils.renderJsonDataSuccess(response, "已是最新版本，无需更新");
					return;
				}
				//已经注册过的账户，更新使用版本就好了
				String updateVersion = "com.vincent.lwx.dao.VersionMapping.updateVersion";
				Map<String, String> map = new HashMap<>();
				map.put("phone", phone);
				map.put("version", version);
				map.put("android_version", android_version);
				map.put("phoneModel", phoneModel);
				map.put("time", DateUtils.getCurrentTimeStr());
				SqlSession session = MyBatisUtils.getSqlSession();
				session.update(updateVersion, map);
				MyBatisUtils.commitTask(session);
				if(getCurrentVersion(phone).getVersion().equals(version)){
					ResponseUtils.renderJsonDataSuccess(response, "版本信息更新成功");
				}else{
					ResponseUtils.renderJsonDataFail(response, ServiceStatus.RUNTIME_EXCEPTION, "版本信息更新失败");
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			ResponseUtils.renderJsonDataFail(response, ServiceStatus.SERVICE_EXCEPTION, ServiceStatus.SERVICE_EXCEPTION_TEXT);
		}
	}
	
	/**
	 * 获取当前版本
	 * @param phone
	 * @return
	 */
	public Version getCurrentVersion(String phone){
		String getVersion = "com.vincent.lwx.dao.VersionMapping.getVersionInfo";
		SqlSession sqlSession = MyBatisUtils.getSqlSession();
		Version currentVersion = sqlSession.selectOne(getVersion, phone);
		if(currentVersion != null){
			return currentVersion;
		}else{
			return null;
		}
	}
	

	/**
	 * 检查升级
	 * @param current_version
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "checkAppUpdate",method = RequestMethod.POST)
	public void checkAppUpdate(@RequestParam("current_version")String current_version,HttpServletRequest request,HttpServletResponse response){
		try{
			App app =getNewestVersion();
			if(app.getVersion().equals(current_version)){
				ResponseUtils.renderJsonDataSuccess(response, "当前已是最新版本");
			}else{
				ResponseUtils.renderJsonDataSuccess(response, "有新版本", app);
			}
		}catch(Exception e){
			e.printStackTrace();
			ResponseUtils.renderJsonDataFail(response, ServiceStatus.SERVICE_EXCEPTION, ServiceStatus.SERVICE_EXCEPTION_TEXT);
		}
	}
	
	/**
	 * 注意：\n会导致服务器异常，http协议不能传输该字符
	 * 添加新版本 http://182.254.232.121:8080/CoolWeb/addAppVersion?version=1.0.2&updateDesc=1、修复QQ分享点击无响应\n2、解决一直bug,提升APP体验&downUrl=http://182.254.232.121:8080/apk/1.0.2/app-huawei-release-102.apk
	 * @param version
	 * @param updateDesc
	 * @param downUrl
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "addAppVersion",method = RequestMethod.POST)
	public void insertVersion(@RequestParam("version")String version,@RequestParam("updateDesc")String updateDesc,@RequestParam("downUrl")String downUrl,
			HttpServletRequest request,HttpServletResponse response){
		try{
			App old = getNewestVersion();
			if(old.getVersion().equals(version)){
				ResponseUtils.renderJsonDataFail(response, ServiceStatus.RUNTIME_EXCEPTION, "请增加版本号后，再次尝试");
				return;
			}
			App app = new App();
			app.setVersion(version);
			app.setDownUrl(downUrl);
			app.setUpdateDesc(updateDesc);
			app.setReleaseDate(DateUtils.getCurrentTimeStr());
			String sql = "com.vincent.lwx.dao.AppMapping.insertVersion";
			SqlSession sqlSession = MyBatisUtils.getSqlSession();
			sqlSession.insert(sql, app);
			MyBatisUtils.commitTask(sqlSession);
			App newApp = getNewestVersion();
			if(old.getId()!=newApp.getId()){
				//添加成功
				ResponseUtils.renderJsonDataSuccess(response, "版本已添加");
			}else{
				//添加失败
				ResponseUtils.renderJsonDataFail(response, ServiceStatus.RUNTIME_EXCEPTION, "添加失败");
			}
		}catch(Exception e){
			e.printStackTrace();
			ResponseUtils.renderJsonDataFail(response, ServiceStatus.SERVICE_EXCEPTION, ServiceStatus.SERVICE_EXCEPTION_TEXT);
		}
	}
	
	/**
	 * 删除某一版本
	 * @param id
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "deleteVersion",method = RequestMethod.POST)
	public void deleteVersion(@RequestParam("id")String id,HttpServletRequest request,HttpServletResponse response){
		try{
			String sql = "com.vincent.lwx.dao.AppMapping.deleteVersionId";
			SqlSession sqlSession = MyBatisUtils.getSqlSession();
			sqlSession.delete(sql, Integer.valueOf(id));
			MyBatisUtils.commitTask(sqlSession);
			App app = getNewestVersion();
			if(app.getId()!=Integer.valueOf(id)){
				//已删除
				ResponseUtils.renderJsonDataSuccess(response, "已删除");
			}else{
				//删除失败
				ResponseUtils.renderJsonDataFail(response, ServiceStatus.RUNTIME_EXCEPTION, "删除失败");
			}
		}catch(Exception e){
			e.printStackTrace();
			ResponseUtils.renderJsonDataFail(response, ServiceStatus.SERVICE_EXCEPTION, ServiceStatus.SERVICE_EXCEPTION_TEXT);
		}
	}
	
	/**
	 * 查询数据库最新版本
	 * @return
	 */
	public App getNewestVersion(){
		String sql = "com.vincent.lwx.dao.AppMapping.getNewestVersion";
		SqlSession sqlSession = MyBatisUtils.getSqlSession();
		App app = sqlSession.selectOne(sql);
		if(app!=null){
			return app;
		}else{
			return null;
		}
	}
	
}


