package com.vincent.lwx.db;

import java.io.IOException;
import java.io.Reader;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.vincent.lwx.util.EatTimerTask;


/**
 * @Title: MyBatisUtils.java
 * @Package com.vincent.julie.config
 * @Description: TODO(��һ�仰�������ļ���ʲô)
 * @author Vincent
 * @date 2017��2��18�� ����12:05:35
 * @version V1.0
 */

public class MyBatisUtils {
	private static SqlSessionFactory sqlSessionFactory;
	private static SqlSession sqlSession;
	private static long timeInterval;//上一次运行的时间
	private static  TimerTask task =null;
	
	static {
		String resource = "CoolWeb-mybatis.xml";
		Reader reader = null;
		try {
			reader = Resources.getResourceAsReader(resource);
		} catch (IOException e) {
			System.out.println(e.getMessage());

		}
		sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
	}
	/**
	 * 
	 * @return
	 */
	public static SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}
	
	/**
	 * ��ȡsqlSession����
	 * @return
	 */
	public static SqlSession getSqlSession(){
		if (task != null){
	    	 task.cancel();  //将原任务从队列中移除
	      }
		task = new EatTimerTask();
		timeInterval = System.currentTimeMillis();
		 //间隔�?1小时
	     long period = 1000 * 60 * 60;    
	     //测试时间每分钟一�?
	     //period = 1000 * 60;        
	     Timer timer = new Timer(); 
	     timer.schedule(task, timeInterval, period);
		
		if(sqlSessionFactory == null){
			sqlSessionFactory = MyBatisUtils.getSqlSessionFactory();
		}
		sqlSession = sqlSessionFactory.openSession();
		return sqlSession;
	}
	
	/**
	 * 提交事务
	 * @param sqlSession
	 */
	public static void commitTask(SqlSession sqlSession){
		if(sqlSession != null){
			sqlSession.commit();
			sqlSession.clearCache();
			sqlSession.close();
		}
	}
}
