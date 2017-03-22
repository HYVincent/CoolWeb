package com.vincent.lwx.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.alibaba.fastjson.JSON;
import com.vincent.lwx.bean.Version;
import com.vincent.lwx.util.DateUtils;

/**    
* @Title: Test.java  
* @Package com.vincent.lwx.test  
* @Description: TODO(用一句话描述该文件做什么)  
* @author A18ccms A18ccms_gmail_com    
* @date 2017年3月22日 上午12:01:07  
* @version V1.0    
*/

public class Test {
	public static void main(String[] args) {
		Version v = new Version();
		v.setAndroid_version("android 6.0.0");
		v.setPhone("18696855784");
		v.setPhoneModel("HUAWEL ANTAL20　EMUI5.0");
		v.setTime(DateUtils.getCurrentTimeStr());
		v.setVersion("1.0.5");
		System.out.println(JSON.toJSONString(v));
		
		int random=(int)(Math.random()*10); 
		List<Integer> data = new ArrayList<>();
		if(random>4){
			for(int i=0;i<10;i++){
				data.add(i);
			}
			for(int i:data){
				System.out.print(i+" ");
			}
		}
		int sum = 0;
		if(data.size()>0){
			sum = data.get(3) + data.get(5);
			System.out.println();
			System.out.println(data.get(3)+" "+data.get(5));
			System.out.println("sum = "+((data.get(3))+(data.get(5))));
		}
		
	}
}


