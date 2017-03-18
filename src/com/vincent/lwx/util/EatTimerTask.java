package com.vincent.lwx.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimerTask;

import org.apache.ibatis.session.SqlSession;

import com.vincent.lwx.db.MyBatisUtils;


/**   
* @Title: EatTimerTask.java 
* @Package com.vincent.julie.utils 
* @Description: TODO(用一句话描述该文件做�?�?) 
* @author Vincent  
* @date 2017�?2�?28�? 下午9:27:43 
* @version V1.0   
*/

public class EatTimerTask extends TimerTask{

    //吃饭时间
    private static List<Integer> eatTimes;
    /*
     * 静�?�初始化
     * */
    static {
        initEatTimes();
    }
    
    /*
     * 初始化吃饭时�?
     * */
    private static void initEatTimes(){
        eatTimes = new ArrayList<Integer>();
        eatTimes.add(8);
        eatTimes.add(12);
        eatTimes.add(18);
    }

    /*
     * 执行
     * */
    @Override
    public void run() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if(eatTimes.contains(hour))
        {
            SqlSession sqlSession = MyBatisUtils.getSqlSession();
        }
    }


}


