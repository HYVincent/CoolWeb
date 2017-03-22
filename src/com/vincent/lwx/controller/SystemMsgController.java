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
import com.vincent.lwx.db.MyBatisUtils;
import com.vincent.lwx.netty.PushServer;
import com.vincent.lwx.netty.msg.MsgType;
import com.vincent.lwx.netty.msg.SystemMsg;
import com.vincent.lwx.util.DateUtils;
import com.vincent.lwx.util.ResponseUtils;
import com.vincent.lwx.util.SystemMsgUtils;

/**    
* @Title: SystemMsgController.java  
* @Package com.vincent.lwx.controller  
* @Description: TODO(用一句话描述该文件做什么)  
* @author A18ccms A18ccms_gmail_com    
* @date 2017年3月22日 上午1:51:23  
* @version V1.0    
*/
@Controller
public class SystemMsgController {

	/**
	 * 向某个用户发送系统消息
	 */
	@RequestMapping(value = "sendSystemMsgToPhone",method =RequestMethod.POST)
	public void sendSystemMsg(@RequestParam("phone")String phone,@RequestParam("msgTitle")String msgTitle,
			@RequestParam("msgContent")String msgContent,HttpServletRequest request,HttpServletResponse response){
		try{
			if(SystemMsgUtils.getSystemMsg(phone, msgTitle)!=null){
				ResponseUtils.renderJsonDataFail(response, ServiceStatus.RUNTIME_EXCEPTION, "请修改标题之后再次发送");
				return;
			}
			SystemMsg msg = new SystemMsg();
			msg.setMsgContent(msgContent);
			msg.setMsgTitle(msgTitle);
			msg.setTime(DateUtils.getCurrentTimeStr());
			msg.setPhoneNum(phone);
			msg.setType(MsgType.SYSTEM_MSG);
			//查询当前用户是否在线
			boolean onLine = PushServer.hasPhone(phone);
			if(onLine){
				System.out.println("在线，开始发送");
				msg.setStatus("1");
			}else{
				System.out.println("不在线，保存起来");
				msg.setStatus("0");
			}
			//保存起来先
			String sql = "com.vincent.lwx.dao.SystemMsgMapping.insertMySystemMsg";
			SqlSession sqlSession = MyBatisUtils.getSqlSession();
			sqlSession.insert(sql, msg);
			MyBatisUtils.commitTask(sqlSession);
			SystemMsg s = SystemMsgUtils.getSystemMsg(phone, msgTitle);
			if(s!=null){
				//保存成功
				if(msg.getStatus().equals("1")){
					//开始推送
					PushServer.push(msg);
					ResponseUtils.renderJsonDataSuccess(response, "已推送");
				}else{
					ResponseUtils.renderJsonDataSuccess(response, "已保存");
				}
			}else{
				//保存失败
				ResponseUtils.renderJsonDataFail(response, ServiceStatus.RUNTIME_EXCEPTION, "保存失败");
			}
		}catch(Exception e){
			e.printStackTrace();
			ResponseUtils.renderJsonDataFail(response, ServiceStatus.SERVICE_EXCEPTION, ServiceStatus.SERVICE_EXCEPTION_TEXT);
		}
	}
	
	/**
	 * 获取用户所有的系统消息
	 * @param phone
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "getUserAllSystemMsg",method = RequestMethod.GET)
	public void getUserAllSystemMsg(@RequestParam("phone")String phone,HttpServletRequest request,HttpServletResponse response){
		try{
			List<SystemMsg> data = SystemMsgUtils.getUserSystemMsg(phone);
			if(data!=null){
				ResponseUtils.renderJsonDataSuccess(response, "已获取",data);
			}else{
				ResponseUtils.renderJsonDataFail(response, ServiceStatus.RUNTIME_EXCEPTION, "没有数据");
			}
		}catch(Exception e){
			e.printStackTrace();
			ResponseUtils.renderJsonDataFail(response, ServiceStatus.SERVICE_EXCEPTION, ServiceStatus.SERVICE_EXCEPTION_TEXT);
		}
	}
	
	/**
	 * 给所有的用户发送系统消息
	 * @param msgTitle
	 * @param msgContent
	 */
	@RequestMapping(value = "sendSystemMsgToAllUser",method = RequestMethod.POST)
	public void sendSystemMsgToAllUser(@RequestParam("msgTitle")String msgTitle,@RequestParam("msgContent")String msgContent,HttpServletRequest request,HttpServletResponse response){
		try {
			SystemMsg systemMsg = new SystemMsg();
			systemMsg.setType(MsgType.SYSTEM_MSG);
			systemMsg.setTime(DateUtils.getCurrentTimeStr());
			systemMsg.setMsgContent(msgContent);
			systemMsg.setMsgTitle(msgTitle);
			boolean status = PushServer.pushAllUser(systemMsg);
			if(status){
				ResponseUtils.renderJsonDataSuccess(response, "已推送并已保存");
			}else{
				ResponseUtils.renderJsonDataFail(response, ServiceStatus.RUNTIME_EXCEPTION, "出现未知错误，查看服务器日志吧");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			ResponseUtils.renderJsonDataFail(response, ServiceStatus.SERVICE_EXCEPTION, ServiceStatus.SERVICE_EXCEPTION_TEXT);
		}
	}
	
	
	
}


