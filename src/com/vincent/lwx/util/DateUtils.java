package com.vincent.lwx.util;

import java.text.ParseException;  
import java.text.SimpleDateFormat;  
import java.util.Calendar;  
import java.util.Date;  
  
  
/**   
* @Title: DateUtils.java 
* @Package com.vincent.julie.utils 
* @Description: TODO(��һ�仰�������ļ���ʲô) 
* @author Vincent  
* @date 2017��2��18�� ����11:44:00 
* @version V1.0   
*/
public class DateUtils  
{  
    
	private static String defaultDatePattern = "yyyy-MM-dd";  
	private static SimpleDateFormat simpleDateFormat = null;
	
	/**
	 * 获取当前系统时间
	 * @return
	 */
	public static String getCurrentTimeStr(){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		return df.format(new Date());// new Date()为获取当前系统时间
	}
	
	public static SimpleDateFormat getSimpleDateFormat() {
		try {
			if(simpleDateFormat == null)
				simpleDateFormat = new SimpleDateFormat(defaultDatePattern);
			return simpleDateFormat;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
		
	}
  
    
    public static String getDatePattern()  
    {  
        return defaultDatePattern;  
    }  
  
    
    public static String getToday()  
    {  
        Date today = new Date();  
        return format(today);  
    }  
  
     
    public static String format(Date date)  
    {  
        return date == null ? " " : format(date, getDatePattern());  
    }  
  
   
    public static String format(Date date, String pattern)  
    {  
        return date == null ? " " : new SimpleDateFormat(pattern).format(date);  
    }  
  
   
    public static Date addMonth(Date date, int n)  
    {  
        Calendar cal = Calendar.getInstance();  
        cal.setTime(date);  
        cal.add(Calendar.MONTH, n);  
        return cal.getTime();  
    }  
  
    public static String getLastDayOfMonth(String year, String month)  
    {  
        Calendar cal = Calendar.getInstance();  
        cal.set(Calendar.YEAR, Integer.parseInt(year));  
        cal.set(Calendar.DATE, 1);  
        cal.add(Calendar.MONTH, 1);  
        cal.add(Calendar.DATE, -1);  
        return String.valueOf(cal.get(Calendar.DAY_OF_MONTH));// �����ĩ�Ǽ���  
    }  
  
}  

