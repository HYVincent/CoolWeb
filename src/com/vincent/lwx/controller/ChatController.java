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

import com.vincent.lwx.bean.Family;
import com.vincent.lwx.bean.ServiceStatus;
import com.vincent.lwx.bean.User;
import com.vincent.lwx.db.MyBatisUtils;
import com.vincent.lwx.netty.PushServer;
import com.vincent.lwx.netty.msg.AskMessage;
import com.vincent.lwx.netty.msg.ChatMsg;
import com.vincent.lwx.netty.msg.MsgType;
import com.vincent.lwx.util.DateUtils;
import com.vincent.lwx.util.ResponseUtils;

import lombok.Data;

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
	 * 采用接口的方式发送消息
	 * @param phone
	 * @param ask_phone
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "sendChatMsg",method = RequestMethod.POST)
	public void sendChatMsg(@RequestParam("phone")String phone,@RequestParam("ask_phone")String ask_phone,
			@RequestParam("chatContent")String chatContent,HttpServletRequest request,HttpServletResponse response){
		try {
			ChatMsg chatMsg = new ChatMsg();
			chatMsg.setType(MsgType.CHAT);
			chatMsg.setAsk_phone(phone);
			chatMsg.setPhoneNum(ask_phone);
			chatMsg.setChatContent(chatContent);
			chatMsg.setMsgType("0");
			boolean status = PushServer.push(chatMsg);
			if(status){
				ResponseUtils.renderJsonDataSuccess(response, "已发送");
			}else{
				ResponseUtils.renderJsonDataFail(response, ServiceStatus.RUNTIME_EXCEPTION, "不在线");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			ResponseUtils.renderJsonDataFail(response, ServiceStatus.RUNTIME_EXCEPTION, ServiceStatus.SERVICE_EXCEPTION_TEXT);
		}
	}
	
	
	
	/**
	 * 查找用户
	 * 
	 * @param myself_phone
	 * @param ask_phone
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "searchFamily", method = RequestMethod.POST)
	public void findFamily(@RequestParam("ask_phone") String ask_phone, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			String sql = "com.vincent.lwx.mapping.UserMapping.selectUser";
			SqlSession sqlSession = MyBatisUtils.getSqlSession();
			// List<User> listUser = sqlSession.selectList(sql, ask_phone);
			User user = sqlSession.selectOne(sql, ask_phone);
			if (user != null) {
				user.setPassword("*******");
				ResponseUtils.renderJsonDataSuccess(response, "已找到", user);
			} else {
				ResponseUtils.renderJsonDataFail(response, ServiceStatus.RUNTIME_EXCEPTION, "该用户尚未注册");
			}
		} catch (Exception e) {
			e.printStackTrace();
			ResponseUtils.renderJsonDataFail(response, ServiceStatus.SERVICE_EXCEPTION,
					ServiceStatus.SERVICE_EXCEPTION_TEXT);
		}
	}

	/**
	 * 向对方发起添加请求
	 * 
	 * @param phone
	 * @param ask_phone
	 * @param msgContent
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "sendAddFamilyQuest", method = RequestMethod.POST)
	public void sendAddFamilyQuest(@RequestParam("phone") String phone, @RequestParam("ask_phone") String ask_phone,
			@RequestParam("msgContent") String msgContent, HttpServletRequest request, HttpServletResponse response) {
		try {
			AskMessage askMessage = new AskMessage();
			askMessage.setFromPhone(phone);
			askMessage.setPhoneNum(ask_phone);
			askMessage.setRemark("0");
			askMessage.setMsgContent(msgContent);
			askMessage.setType(MsgType.ASK);
			askMessage.setTime(DateUtils.getCurrentTimeStr());
			if (PushServer.hasPhone(ask_phone)) {
				System.out.println("在线，发过去");
				askMessage.setStatus("1");
			} else {
				System.out.println("不在线");
				askMessage.setStatus("0");
			}
			// 保存起来
			if (saveAskMsg(askMessage)) {
				System.out.println("已保存");
			} else {
				System.out.println("保存失败");
			}
			if (askMessage.getStatus().equals("1")) {
				// 开始推送消息
				PushServer.push(askMessage);
			}
			ResponseUtils.renderJsonDataSuccess(response, "请求已发送");
		} catch (Exception e) {
			e.printStackTrace();
			ResponseUtils.renderJsonDataFail(response, ServiceStatus.SERVICE_EXCEPTION,
					ServiceStatus.SERVICE_EXCEPTION_TEXT);
		}
	}

	/**
	 * 这是被添加 同意添加到家人列表 注意的是两边都要加进去
	 * 
	 * @param phone
	 *            我自己的手机，
	 * @param familyPhone
	 *            家人的手机
	 * @param msgContent
	 *            验证消息的内容
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "addFamilyToList", method = RequestMethod.POST)
	public void agreeAddFamily(@RequestParam("phone") String phone, @RequestParam("familyPhone") String familyPhone,
			@RequestParam("msgContent") String msgContent, HttpServletRequest request, HttpServletResponse response) {
		try {
			System.out.println("familyPhone-->" + familyPhone);
			String alterRemark = "com.vincent.lwx.dao.ChatMapping.alterAskMsgAgreeAddStatus";
			Map<String, String> map = new HashMap<>();
			map.put("phoneNum", phone);
			map.put("fromPhone", familyPhone);
			map.put("msgContent", msgContent);
			// AskMessage a =selectItemAskMsg(phone, familyPhone, msgContent);
			// System.out.println("phoneNum-->"+a.getPhoneNum() +"
			// FromPhone-->"+ a.getFromPhone());
			SqlSession sqlSession = MyBatisUtils.getSqlSession();
			System.out.println("map.fromPhone=" + map.get("fromPhone"));
			sqlSession.update(alterRemark, map);
			MyBatisUtils.commitTask(sqlSession);
			// 添加到家人列表，注意两边都要添加进去，不然一边看不到
			String add = "com.vincent.lwx.dao.FamilyMapping.addToFamilyList";
			Family f = new Family();
			f.setPhone(phone);
			f.setFamilyPhone(familyPhone);
			f.setRemark(familyPhone);
			f.setTime(DateUtils.getCurrentTimeStr());
			SqlSession sqlSession2 = MyBatisUtils.getSqlSession();
			sqlSession2.insert(add, f);
			MyBatisUtils.commitTask(sqlSession2);
			Family f2 = new Family();
			f2.setPhone(familyPhone);
			f2.setFamilyPhone(phone);
			f2.setRemark(phone);
			f2.setTime(DateUtils.getCurrentTimeStr());
			SqlSession sqlSession3 = MyBatisUtils.getSqlSession();
			sqlSession3.insert(add, f2);
			MyBatisUtils.commitTask(sqlSession3);
			// 添加完毕之后查询一下  查询异常了，会导致重复添加，另外添加之前应该查询一下是否存在，如果存在则不要添加了
			if (getFamilyOne(phone, familyPhone) != null) {
				// 添加成功
				ResponseUtils.renderJsonDataSuccess(response, "已添加");
			} else {
				// 添加失败
				ResponseUtils.renderJsonDataFail(response, ServiceStatus.RUNTIME_EXCEPTION, "添加失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
			ResponseUtils.renderJsonDataFail(response, ServiceStatus.SERVICE_EXCEPTION,
					ServiceStatus.SERVICE_EXCEPTION_TEXT);
		}
	}

	/**
	 * 删除一个 删除一边的
	 * 
	 * @param phone
	 * @param familyPhone
	 * @param requeste
	 * @param response
	 */
	@RequestMapping(value = "deleteFamilyOne", method = RequestMethod.POST)
	public void deleteFamilyOne(@RequestParam("phone") String phone, @RequestParam("familyPhone") String familyPhone,
			HttpServletRequest requeste, HttpServletResponse response) {
		try {
			String sql = "com.vincent.lwx.dao.FamilyMapping.deleteFamilyOne";
			Map<String, String> map = new HashMap<>();
			map.put("phone", phone);
			map.put("familyPhone", familyPhone);
			SqlSession sqlSession = MyBatisUtils.getSqlSession();
			sqlSession.delete(sql, map);
			MyBatisUtils.commitTask(sqlSession);
			if (getFamilyOne(phone, familyPhone) != null) {
				//删除失败
				ResponseUtils.renderJsonDataFail(response, ServiceStatus.RUNTIME_EXCEPTION, "删除失败");
			}else{
				ResponseUtils.renderJsonDataSuccess(response, "已删除");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ResponseUtils.renderJsonDataFail(response, ServiceStatus.RUNTIME_EXCEPTION, ServiceStatus.SERVICE_EXCEPTION_TEXT);
		}
	}

	/**
	 * 获取家人列表
	 * 
	 * @param phone
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "getAllFamilyList", method = RequestMethod.POST)
	public void getAllFamily(@RequestParam("phone") String phone, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			List<Family> data = getFamilyAll(phone);
			if (data == null) {
				ResponseUtils.renderJsonDataFail(response, ServiceStatus.RUNTIME_EXCEPTION, "没有数据");
			} else {
				ResponseUtils.renderJsonDataSuccess(response, "已获取", data);
			}
		} catch (Exception e) {
			e.printStackTrace();
			ResponseUtils.renderJsonDataFail(response, ServiceStatus.SERVICE_EXCEPTION,
					ServiceStatus.SERVICE_EXCEPTION_TEXT);
		}
	}

	/**
	 * 查询家人
	 * 
	 * @param phone
	 * @param familyPhone
	 * @return
	 * @throws Exception
	 */
	public Family getFamilyOne(String phone, String familyPhone) throws Exception {
		String sql = "com.vincent.lwx.dao.FamilyMapping.selectFamilyOne";
		Map<String, String> map = new HashMap<>();
		map.put("phone", phone);
		map.put("familyPhone", familyPhone);
		SqlSession sqlSession = MyBatisUtils.getSqlSession();
		Family f = sqlSession.selectOne(sql, map);
		
		if (f != null) {
			return f;
		} else {
			return null;
		}
	}

	/**
	 * 获取家人列表
	 * 
	 * @param phone
	 * @return
	 */
	public List<Family> getFamilyAll(String phone) {
		try {
			String sql = "com.vincent.lwx.dao.FamilyMapping.selectFamilyAll";
			SqlSession sqlSession = MyBatisUtils.getSqlSession();
			List<Family> data = sqlSession.selectList(sql, phone);
			MyBatisUtils.commitTask(sqlSession);
			if (data != null & data.size() > 0) {
				System.out.println("getFamilyAll--有数据");
				return data;
			} else {
				System.out.println("getFamilyAll--没有数据");
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getFamilyAll--获取家人列表异常了");
			return null;
		}
	}

	/**
	 * 发送验证消息给对方请求添加好友 保存的是被动方接受方的消息
	 * 
	 * @param myself_phone
	 *            我
	 * @param ask_phone
	 *            请求添加的对方
	 * @param request
	 * @param response
	 * @param msgContent
	 *            内容
	 */
	@RequestMapping(value = "sendVerifyMsgToFamily", method = RequestMethod.POST)
	public void sendVerifyMsgToFamily(@RequestParam("myself_phone") String myself_phone,
			@RequestParam("ask_phone") String ask_phone, @RequestParam("msgContent") String msgContent,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			if (selectItemAskMsg(ask_phone, myself_phone, msgContent) != null) {
				// 说明之前发过
				ResponseUtils.renderJsonDataFail(response, ServiceStatus.RUNTIME_EXCEPTION, "修改内容重试，换一个吧");
				return;
			}
			// 注意，askmsg是相对于接收方来说的 和接口请求的号码是相反的
			// TODO 消息应该两边都保存 现在保存的作为被动接受方的，主动发送方的没有保存
			AskMessage askMessage = new AskMessage();
			askMessage.setFromPhone(myself_phone);
			askMessage.setMsgContent(msgContent);
			askMessage.setPhoneNum(ask_phone);
			askMessage.setType(MsgType.ASK);
			askMessage.setTime(DateUtils.getCurrentTimeStr());
			if (PushServer.hasPhone(ask_phone)) {
				// 在线
				askMessage.setStatus("1");
			} else {
				// 不在线
				// System.out.println(ask_phone+" 不在线");
				askMessage.setStatus("0");
			}

			if (saveAskMsg(askMessage)) {
				// 检查"我"是否在线，在线才推送
				System.out.println("status:" + askMessage.getStatus());
				if (askMessage.getStatus().equals("1")) {
					PushServer.push(askMessage);
					ResponseUtils.renderJsonDataSuccess(response, "请求已发送");
				} else {
					System.out.println("不在线，等在在线再发送");
					ResponseUtils.renderJsonDataSuccess(response, "请求已发送");
					ResponseUtils.renderJsonDataFail(response, ServiceStatus.RUNTIME_EXCEPTION, "对方不在线，消息将在对方登录的时候发送");
				}
			} else {
				// 保存失败
				ResponseUtils.renderJsonDataFail(response, ServiceStatus.RUNTIME_EXCEPTION, "请求发送失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
			ResponseUtils.renderJsonDataFail(response, ServiceStatus.RUNTIME_EXCEPTION,
					ServiceStatus.SERVICE_EXCEPTION_TEXT);
		}
	}

	/**
	 * 保存一个askMesage对象到服务器
	 * 
	 * @param askMessage
	 * @return
	 */
	public boolean saveAskMsg(AskMessage askMessage) throws Exception {
		String sql = "com.vincent.lwx.dao.ChatMapping.saveAskMsg";
		SqlSession sqlSession = MyBatisUtils.getSqlSession();
		sqlSession.insert(sql, askMessage);
		MyBatisUtils.commitTask(sqlSession);
		AskMessage ask = selectItemAskMsg(askMessage.getPhoneNum(), askMessage.getFromPhone(),
				askMessage.getMsgContent());
		if (ask == null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 查询是否保存成功
	 * 
	 * @param phoneNum
	 * @param fromPhone
	 * @param msgContent
	 * @return
	 */
	public AskMessage selectItemAskMsg(String phoneNum, String fromPhone, String msgContent) {
		// System.out.println("------"+phoneNum+"----"+fromPhone+"--------"+msgContent+"-----------");
		String sql = "com.vincent.lwx.dao.ChatMapping.selectItemAskMsg";
		Map<String, String> map = new HashMap<>();
		map.put("phoneNum", phoneNum);
		map.put("fromPhone", fromPhone);
		map.put("msgContent", msgContent);
		SqlSession sqlSession = MyBatisUtils.getSqlSession();
		AskMessage a = sqlSession.selectOne(sql, map);
		MyBatisUtils.commitTask(sqlSession);
		if (a != null) {
			System.out.println("保存成功");
			return a;
		} else {
			System.out.println("保存失败");
			return null;
		}
	}
}
