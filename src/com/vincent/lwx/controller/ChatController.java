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
import com.vincent.lwx.netty.PushServer;
import com.vincent.lwx.netty.msg.AskMessage;
import com.vincent.lwx.netty.msg.MsgType;
import com.vincent.lwx.util.DateUtils;
import com.vincent.lwx.util.ResponseUtils;

/**   
* @Title: ChatController.java 
* @Package com.vincent.lwx.controller 
* @Description: 聊天实现以及加好友验证等 
* @author Vincent   
* @date 2017年3月16日 下午11:55:38 
* @version V1.0   
*/
@Controller
public class ChatController {


	/**
	 * 查找家人
	 * @param myself_phone
	 * @param ask_phone
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "searchFamily",method = RequestMethod.POST)
	public void findFamily(@RequestParam("ask_phone")String ask_phone,
			HttpServletRequest request,HttpServletResponse response){
		try{
			String sql = "com.vincent.lwx.dao.ChatMapping.searchFamily";
			SqlSession sqlSession = MyBatisUtils.getSqlSession();
//			List<User> listUser = sqlSession.selectList(sql, ask_phone);
			User user =  sqlSession.selectOne(sql, ask_phone);
			if(user!=null){
				user.setPassword("*******");
				ResponseUtils.renderJsonDataSuccess(response, "已找到", user);
			}else{
				ResponseUtils.renderJsonDataFail(response, ServiceStatus.RUNTIME_EXCEPTION, "该用户尚未注册");
			}
		}catch(Exception e){
			e.printStackTrace();
			ResponseUtils.renderJsonDataFail(response, ServiceStatus.SERVICE_EXCEPTION, ServiceStatus.SERVICE_EXCEPTION_TEXT);
		}
	}
	
	
	/**
	 * 发送验证消息给对方请求添加好友
	 * @param myself_phone 我
	 * @param ask_phone 请求添加的对方
	 * @param request
	 * @param response
	 * @param msgContent 内容
	 */
	@RequestMapping(value = "sendVerifyMsgToFamily",method = RequestMethod.POST)
	public void sendVerifyMsgToFamily(@RequestParam("myself_phone")String myself_phone,@RequestParam("ask_phone")String ask_phone,@RequestParam("msgContent")String msgContent,HttpServletRequest request,HttpServletResponse response){
		try{
			if(selectItemAskMsg(ask_phone, myself_phone, msgContent)!=null){
				//说明之前发过
				ResponseUtils.renderJsonDataFail(response, ServiceStatus.RUNTIME_EXCEPTION, "修改内容重试，换一个吧");
				return;
			}
			//注意，askmsg是相对于接收方来说的 和接口请求的号码是相反的
			AskMessage askMessage = new AskMessage();
			askMessage.setFromPhone(myself_phone);
			askMessage.setMsgContent(msgContent);
			askMessage.setPhoneNum(ask_phone);
			askMessage.setType(MsgType.ASK);
			askMessage.setTime(DateUtils.getCurrentTimeStr());
			if(PushServer.hasPhone(ask_phone)){
				//在线
				askMessage.setStatus("1");
			}else{
				//不在线
//				System.out.println(ask_phone+" 不在线");
				askMessage.setStatus("0");
			}
			
			if(saveAskMsg(askMessage)){
				//检查"我"是否在线，在线才推送
				System.out.println("status:"+askMessage.getStatus());
				if(askMessage.getStatus().equals("1")){
					PushServer.push(askMessage);
				}else{
					ResponseUtils.renderJsonDataSuccess(response, "请求已发送");
				}
			}else{
				//保存失败
				ResponseUtils.renderJsonDataFail(response, ServiceStatus.RUNTIME_EXCEPTION, "请求发送失败");
			}	
		}catch(Exception e){
			e.printStackTrace();
			ResponseUtils.renderJsonDataFail(response, ServiceStatus.RUNTIME_EXCEPTION, ServiceStatus.SERVICE_EXCEPTION_TEXT);
		}
	}
	
	/**
	 * 保存一个askMesage对象到服务器
	 * @param askMessage
	 * @return
	 */
	public boolean saveAskMsg(AskMessage askMessage) throws Exception{
		String sql = "com.vincent.lwx.dao.ChatMapping.saveAskMsg";
		SqlSession sqlSession = MyBatisUtils.getSqlSession();
		sqlSession.insert(sql, askMessage);
		MyBatisUtils.commitTask(sqlSession);
		AskMessage ask = selectItemAskMsg(askMessage.getPhoneNum(), askMessage.getFromPhone(), askMessage.getMsgContent());
		if(ask == null){
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * 查询是否保存成功
	 * @param phoneNum
	 * @param fromPhone
	 * @param msgContent
	 * @return
	 */
 	public AskMessage selectItemAskMsg(String phoneNum,String fromPhone,String msgContent){
// 		System.out.println("------"+phoneNum+"----"+fromPhone+"--------"+msgContent+"-----------");
		String sql = "com.vincent.lwx.dao.ChatMapping.selectItemAskMsg";
		Map<String, String> map = new HashMap<>();
		map.put("phoneNum", phoneNum);
		map.put("fromPhone", fromPhone);
		map.put("msgContent", msgContent);
		SqlSession sqlSession = MyBatisUtils.getSqlSession();
		AskMessage a = sqlSession.selectOne(sql, map);
		MyBatisUtils.commitTask(sqlSession);
		if(a!=null){
			System.out.println("保存成功");
			return a;
		}else{
			System.out.println("保存失败");
			return null;
		}
	}
}


