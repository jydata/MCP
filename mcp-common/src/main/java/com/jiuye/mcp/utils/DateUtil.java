package com.jiuye.mcp.utils;

import org.apache.commons.lang3.time.FastDateFormat;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * DateUtil
 *
 * @author kevin
 * @date 2018-09-29
 */
public class DateUtil {

	public static String DF_YYYYMMDD_HHMMSS = "yyyy-MM-dd HH:mm:ss";
	public static String DF_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
	public static String DF_YYYY_MM_DD = "yyyy-MM-dd";
	public static String DF_YYYYMMDD = "yyyyMMdd";
	public static String DF_HHMMSS = "HHmmss";
	public static String DF_YYYY = "yyyy";
	public static String DF_MM = "MM";
	public static String DF_DD = "dd";
	public static String DF_HH = "HH";
	public static String DF_mm = "mm";

	public static String getFormatDate(Date date , String formatStr){
		FastDateFormat fdf = FastDateFormat.getInstance(formatStr);
		String formatDate = fdf.format(date);
		return formatDate;
	}

	public static String getFormatDate(String formatStr){
		Date date = new Date();
		return getFormatDate(date , formatStr);
	}

	public static int getYyyyMMdd(){
		String date = getFormatDate(DF_YYYYMMDD);
		return Integer.parseInt(date);
	}

	public static String getDate(Date date){
		return getFormatDate(date, DF_YYYYMMDD);
	}

	public static int getYear(){
		return Integer.parseInt(getFormatDate(DF_YYYY));
	}

	public static int getYear(Date date){
		return Integer.parseInt(getFormatDate(date, DF_YYYY));
	}

	public static int getMonth(Date date){
		return Integer.parseInt(getFormatDate(date, DF_MM));
	}

	public static int getDay(Date date){
		return Integer.parseInt(getFormatDate(date, DF_DD));
	}

	public static String getHour(Date date){
		return getFormatDate(date, DF_HH);
	}

	public static String getMinute(Date date){
		return getFormatDate(date, DF_mm);
	}

	/**
	 * 获取昨天日期，返回格式yyyyMMdd
	 */
	public static int getYesterday(){
		Date yesterday = addDay(new Date() , -1);
		return Integer.parseInt(getFormatDate(yesterday, DF_YYYYMMDD));
	}

	public static String getYesterday(String format){
		Date yesterday = addDay(new Date() , -1);
		return getFormatDate(yesterday, format);
	}

	/**
	 * 对日期进行加减
	 */
	public static Date addDay(Date date,int amount){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_MONTH, amount);
		return c.getTime();
	}

	/**
	 * 对日期进行加减
	 */
	public static Date addMonth(Date date,int amount){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MONTH, amount);
		return c.getTime();
	}

	public static Date getDate(int flag){
		Date date=new Date();//取时间
		 Calendar calendar = new GregorianCalendar();
		 calendar.setTime(date);
		 calendar.add(calendar.DATE,flag);//把日期往后增加一天.整数往后推,负数往前移动
		 date=calendar.getTime(); //这个时间就是日期往后推一天的结果 
         return date;		
	}

	/**
	 * 获取上个月第一天
	 */
	public static Date getLastMonthFirstDay(){
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return calendar.getTime();
	}

	/**
	 * 获取上个月最后一天
	 */
	public static Date getLastMonthEndDay(){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1); 
		calendar.add(Calendar.DATE, -1);
		return calendar.getTime();
	}

	/**
	 * 获取指定月份最后一天
	 */
	public static String getEndDay(Date assignDate){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(assignDate);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.add(Calendar.MONTH, 1);
		calendar.add(Calendar.DATE, -1);
		return getFormatDate(calendar.getTime(), DF_DD);
	}

	public static Date parse(String dateStr , String format){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date date = null;
		try {
			date = sdf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 判断该日期是否属于昨天
	 */
	public static boolean isYesterDay(Date queryDate){
		String queryDateStr = getFormatDate(queryDate, DF_YYYYMMDD);
		String yesterDay = getFormatDate(addDay(new Date(), -1), DF_YYYYMMDD);
		if(queryDateStr.equals(yesterDay)){
			return true;
		}else{
			return false;
		}
	}

	public static String getFormatTime(Date date){
		return getFormatDate(date, DF_YYYYMMDD_HHMMSS);
	}

	public static String getFormatTime(long ms){
		return getFormatDate(new Date(ms), DF_YYYYMMDD_HHMMSS);
	}

	public static Timestamp getCurrentTime(){
		Date date = new Date();
		return new Timestamp(System.currentTimeMillis());
	}

	public static void main(String[] args) {
//		System.out.println(getFormatDate(DateUtil.DF_YYYYMMDD_HHMMSS));
		System.out.println(getCurrentTime());
	}
}
