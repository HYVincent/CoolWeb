package com.vincent.lwx.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.vincent.lwx.bean.ServiceStatus;
import com.vincent.lwx.netty.PushServer;
import com.vincent.lwx.netty.msg.MsgType;
import com.vincent.lwx.netty.msg.PushMsg;
import com.vincent.lwx.util.ResponseUtils;

/**   
* @Title: TestPush.java 
* @Package com.vincent.lwx.controller 
* @Description: TODO(用一句话描述该文件做什么) 
* @author Vincent   
* @date 2017年3月13日 下午12:49:15 
* @version V1.0   
*/
@Controller
public class PushMsgController {
	
	/**
	 * 请求推送消息
	 * @param push
	 */
	@RequestMapping(value = "push",method=RequestMethod.POST)
	public void PushToPhone(@RequestParam("phone")String phone,HttpServletRequest request,HttpServletResponse response){
		try{
			System.out.println("推送手机号码："+phone);
			PushMsg pushMsg = new PushMsg();
			pushMsg.setContent("消息推送test");
			pushMsg.setPhoneNum(phone);
			pushMsg.setType(MsgType.PUSH);
			boolean result = PushServer.push(pushMsg);
			if(result){
				ResponseUtils.renderJsonDataSuccess(response, "已推送，注意查看");
			}else{
				ResponseUtils.renderJsonDataSuccess(response, "推送失败，检查服务器代码");
			}
		}catch(Exception e){
			e.printStackTrace();
			ResponseUtils.renderJsonDataFail(response, ServiceStatus.SERVICE_EXCEPTION, ServiceStatus.SERVICE_EXCEPTION_TEXT);
		}
	}
	
	/**
	 * 推送给所有的用户
	 * @param commonMsg
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "pushAllUser",method = RequestMethod.POST)
	public void PushMsgToAllUser(@RequestParam("commonMsg")String commonMsg,HttpServletRequest request,HttpServletResponse response){
		try{
			PushMsg pushMsg = new PushMsg();
			pushMsg.setContent(commonMsg);
			pushMsg.setPhoneNum("");
			pushMsg.setType(MsgType.PUSH);
			boolean result = PushServer.pushAllUser(pushMsg);
			if(result){
				ResponseUtils.renderJsonDataSuccess(response, "已推送，注意查看");
			}else{
				ResponseUtils.renderJsonDataSuccess(response, "推送失败，检查服务器代码");
			}
		}catch(Exception e){
			e.printStackTrace();
			ResponseUtils.renderJsonDataFail(response, ServiceStatus.SERVICE_EXCEPTION, ServiceStatus.SERVICE_EXCEPTION_TEXT);
		}
	}
	
	
	/**
	 * test
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "test",method = RequestMethod.GET)
	public String test(HttpServletRequest request,HttpServletResponse response){
		return "lwx";
	}
}


