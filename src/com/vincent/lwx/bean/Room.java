package com.vincent.lwx.bean;

import java.io.Serializable;

import lombok.Data;

/**   
* @Title: Room.java 
* @Package com.vincent.lwx.bean 
* @Description: TODO(用一句话描述该文件做什么) 
* @author Vincent   
* @date 2017年3月14日 下午11:46:17 
* @version V1.0   
*/
@Data
public class Room implements Serializable{
	
	 /**
	 * 序列化id
	 */
	private static final long serialVersionUID = 1L;
	
	
	private int id;
	private String phone;
	private String roomName;
	private String roomType;
	private String roomImg;//图标
	private String roomBigImg; //大图 实际上是一个List<String>类型，到时候拿出来转换吧，我艹 这里不管，直接返回
	
}


