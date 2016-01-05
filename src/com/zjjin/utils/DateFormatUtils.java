package com.zjjin.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.annotation.SuppressLint;

@SuppressLint("SimpleDateFormat")
public class DateFormatUtils {
	/**
	 * 将字符串转为时间戳
	 * 
	 * @param user_time
	 * @return
	 */
	public static String getTime(String user_time) {
		String re_time = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
		Date d;
		try {
			d = sdf.parse(user_time);
			long l = d.getTime();
			String str = String.valueOf(l);
			re_time = str.substring(0, 10);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return re_time;
	}
	
	/**
	 * 将时间戳转为字符串
	 * 
	 * @param cc_time
	 * @return
	 */
	public static String getStrTime(String cc_time) {
		String re_StrTime = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
		// 例如：cc_time=1291778220
		long lcc_time = Long.valueOf(cc_time);
		re_StrTime = sdf.format(new Date(lcc_time));
		return re_StrTime;
	}
	
	/**
	 * 获得时间的long表示形式
	 * @param time
	 * @return
	 */
	public static long getLongTime(String time){
		String[]	timeStart	= time.split(":");
		String		hourStart	= timeStart[0];
		String		minuteStart = timeStart[1];
		if(hourStart.charAt(0) == '0'){
			hourStart = hourStart.charAt(1) + "";
		}
		if(minuteStart.charAt(0) == '0'){
			minuteStart = minuteStart.charAt(1) + "";
		}
		String 	userTime = "";
		long currentTime = System.currentTimeMillis();
		String 	currentTimeStr	= DateFormatUtils.getStrTime(String.valueOf(currentTime));
		userTime = currentTimeStr.substring(0, currentTimeStr.indexOf("日") + 1) + hourStart + "时" + minuteStart + "分00秒";
		return Long.parseLong(DateFormatUtils.getTime(userTime));
	}
	
	
	/**
	 * 获取当前星期几：一，二，三，四，五，六，日
	 * @return
	 */
	public static String StringData(){
		String mYear;  
	    String mMonth;  
	    String mDay;  
	    String mWay;  
        final Calendar c = Calendar.getInstance();  
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));  
        mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份  
        mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份  
        mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码  
        mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));  
        if("1".equals(mWay)){  
            mWay ="日";  
        }else if("2".equals(mWay)){  
            mWay ="一";  
        }else if("3".equals(mWay)){  
            mWay ="二";  
        }else if("4".equals(mWay)){  
            mWay ="三";  
        }else if("5".equals(mWay)){  
            mWay ="四";  
        }else if("6".equals(mWay)){  
            mWay ="五";  
        }else if("7".equals(mWay)){  
            mWay ="六";  
        }  
        return mYear + "年" + mMonth + "月" + mDay+"日"+"/星期"+mWay;  
    }
	
}
