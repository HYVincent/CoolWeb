package com.vincent.lwx.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.vincent.lwx.bean.Room;
import com.vincent.lwx.bean.ServiceStatus;
import com.vincent.lwx.db.MyBatisUtils;
import com.vincent.lwx.util.ResponseUtils;

/**   
* @Title: RoomController.java 
* @Package com.vincent.lwx.controller 
* @Description: Room
* @author Vincent   
* @date 2017年3月14日 下午11:45:44 
* @version V1.0   
*/
@Controller
public class RoomController {

	private static final Logger logger = LogManager.getLogger(UserController.class);
	

	/**
	 * 
	 * @param phone 手机号码
	 * @param roomType 房间类型
	 * @param roomName 房间名称
	 * @param roomImg 房间图标
	 * @param roomBigImg 房间图片路径
	 * @param request 
	 * @param response
	 */
	@RequestMapping(value = "addRoom",method = RequestMethod.POST)
	public void addRoom(@RequestParam("phone")String phone,@RequestParam("roomType")String roomType,@RequestParam("roomName")String roomName,
			@RequestParam("roomImg")String roomImg,@RequestParam("roomBigImg")String roomBigImg,HttpServletRequest request,HttpServletResponse response){
		try{
			if(getRoomOne(phone, roomName)){
				//已存在，改名字
				ResponseUtils.renderJsonDataFail(response, ServiceStatus.RUNTIME_EXCEPTION, "存在相同名字的房间，修改房间名称再提交");
				return;
			}
			Room room = new Room();
			room.setPhone(phone);
			room.setRoomName(roomName);
			room.setRoomType(roomType);
			room.setRoomImg(roomImg);
			room.setRoomBigImg(roomBigImg);
			String sql = "com.vincent.lwx.mapping.RoomMapping.insertRoom";
			SqlSession sqlSession = MyBatisUtils.getSqlSession();
			sqlSession.insert(sql, room);
			MyBatisUtils.commitTask(sqlSession);
			Room r = getRoomOne2(phone, roomName);
			if(r != null){
				ResponseUtils.renderJsonDataSuccess(response, "Room已保存", r);
			}else{
				ResponseUtils.renderJsonDataFail(response, ServiceStatus.RUNTIME_EXCEPTION, "Room保存失败");
			}
		}catch(Exception e){
			e.printStackTrace();
			ResponseUtils.renderJsonDataFail(response, ServiceStatus.SERVICE_EXCEPTION, ServiceStatus.SERVICE_EXCEPTION_TEXT);
		}
	}
	/**
	 * 获取房间列表
	 * @param phone
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "getAllRoom",method = RequestMethod.GET)
	public void getAllRoom(@RequestParam("phone")String phone,HttpServletRequest request,HttpServletResponse response){
		try {
				String sql = "com.vincent.lwx.mapping.RoomMapping.selectAllRoom";
				SqlSession sqlSession = MyBatisUtils.getSqlSession();
				List<Room> listRoom = sqlSession.selectList(sql, phone);
				if(listRoom.size()>0&&listRoom != null){
					//有数据 返回
					ResponseUtils.renderJsonDataSuccess(response, "已拿到相关数据", listRoom);
				}else{
					//无数据，返回
					ResponseUtils.renderJsonDataFail(response, ServiceStatus.RUNTIME_EXCEPTION,"服务器没有相关数据" );
//					ResponseUtils.renderJsonDataFail(response, ServiceStatus.RUNTIME_EXCEPTION, "服务器没有相关数据");
				}
		} catch (Exception e) {
			e.printStackTrace();
			ResponseUtils.renderJsonDataFail(response, ServiceStatus.SERVICE_EXCEPTION, ServiceStatus.SERVICE_EXCEPTION_TEXT);
		}
	}
	
	/**
	 * 删除房间
	 * @param roomJson
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "deleteRoomItem",method = RequestMethod.POST)
	public void deleteRoomItem(@RequestParam("roomName")String roomName,@RequestParam("phone")String phone,HttpServletRequest request,HttpServletResponse response){
		try{
			Map<String, String> map = new HashMap<>();
			map.put("phone", phone);
			map.put("roomName", roomName);
			String sql = "com.vincent.lwx.mapping.RoomMapping.deleteRoomItem";
			SqlSession sqlSession = MyBatisUtils.getSqlSession();
			sqlSession.delete(sql, map);
			MyBatisUtils.commitTask(sqlSession);
			if(getRoomOne(phone,roomName)){
				ResponseUtils.renderJsonDataFail(response, ServiceStatus.RUNTIME_EXCEPTION, "Room删除失败");
			}else{
				ResponseUtils.renderJsonDataSuccess(response, "Room信息已删除");				
			}
		}catch(Exception e){
			e.printStackTrace();
			ResponseUtils.renderJsonDataFail(response, ServiceStatus.SERVICE_EXCEPTION, ServiceStatus.SERVICE_EXCEPTION_TEXT);
		}
	}
	
	/**
	 * 查询room
	 * @param id
	 * @return
	 */
	public boolean getRoomOne(String phone,String roomName){
		String sql = "com.vincent.lwx.mapping.RoomMapping.selectRoomItem";
		Map<String, String> map = new HashMap<>();
		map.put("roomName", roomName);
		map.put("phone", phone);
		SqlSession sqlSession = MyBatisUtils.getSqlSession();
		Room room = sqlSession.selectOne(sql, map);
		logger.debug(room);
		if(room != null){
//			System.out.println("存在");
			return true ;
		}else{
//			System.out.println("不存在");
			return false;
		}
	}
	
	public Room getRoomOne2(String phone,String roomName){
		String sql = "com.vincent.lwx.mapping.RoomMapping.selectRoomItem";
		Map<String, String> map = new HashMap<>();
		map.put("roomName", roomName);
		map.put("phone", phone);
		SqlSession sqlSession = MyBatisUtils.getSqlSession();
		Room room = sqlSession.selectOne(sql, map);
		logger.debug(room);
		if(room != null){
//			System.out.println("存在");
			return room ;
		}else{
//			System.out.println("不存在");
			return null;
		}
	}
	
}


