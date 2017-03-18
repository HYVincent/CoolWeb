package com.vincent.lwx.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**   
* @Title: MySelfController.java 
* @Package com.vincent.lwx.controller 
* @Description: TODO(用一句话描述该文件做什么) 
* @author Vincent   
* @date 2017年3月16日 上午11:10:49 
* @version V1.0   
*/
@Controller
public class MySelfController {
	
	/**
	 * http://127.0.0.1:8080/CoolWeb/myself 地址访问
	 * http://182.254.232.121:8080/CoolWeb/myself
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "myself",method = RequestMethod.GET)
	public String myself(HttpServletRequest request,HttpServletResponse response){
		
		return "lwx";
	}
	
}


