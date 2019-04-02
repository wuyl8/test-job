package com.tydic.boot.ppmService.server.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * @author xkarvy 工具类 常用工具
 */
public class Tools {

	/**
	 * @param objs
	 * @return boolean 是否为空
	 */
	public static boolean isNull(Object[] objs) {
		if (null == objs || objs.length <= 0) {
			return true;
		}
		/* String[] */
		if (objs instanceof String[]) {
			return checkDate((String[]) objs);
		}
		/* Object[] */
		if (objs instanceof Object[]) {
			return checkObjects((Object[]) objs);
		}
		return false;
	}

	/**
	 * @param obj
	 * @return boolean 是否为空
	 */
	public static boolean isNull(Object obj) {
		/* 为空 */
		if (obj == null)
			return true;
		/* String */
		if (obj instanceof String) {
			return checkString((String) obj);
		}
		/* Integer */
		if (obj instanceof Integer) {
			return checkInteger((Integer) obj);
		}
		/* Long */
		if (obj instanceof Long) {
			return checkLong((Long) obj);
		}
		/* Double */
		if (obj instanceof Double) {
			return checkDouble((Double) obj);
		}
		/* Date */
		if (obj instanceof Date) {
			return checkDate((Date) obj);
		}
		/* List */
		if (obj instanceof List) {
			return checkList((List) obj);
		}
		/* Set */
		if (obj instanceof Set) {
			return checkSet((Set) obj);
		}
		/* Map */
		if (obj instanceof Map) {
			return checkMap((Map) obj);
		}
		if (obj instanceof BigDecimal) {
			return BigDecimal((BigDecimal) obj);
		}

		return false;
	}

	/**
	 * 时间跨度
	 * @param startTime
	 * @param endTime
	 * @param format
	 * @param type
	 * @return
	 */
	public static long countDay(String startTime, String endTime, String format,int type) {
		 
		//按照传入的格式生成一个simpledateformate对象
		SimpleDateFormat sd = new SimpleDateFormat(format);
		long nd = 1000*24*60*60;//一天的毫秒数
		long nh = 1000*60*60;//一小时的毫秒数
		long nm = 1000*60;//一分钟的毫秒数
		long ns = 1000;//一秒钟的毫秒数long diff;try {
		
		//获得两个时间的毫秒时间差异
		long spanDate  = 0;
		try {
			long diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();
			switch(type){
				case Calendar.DATE : 
					spanDate = diff/nd;
					break;
				case Calendar.HOUR :
					spanDate = diff/nh;
					break;
				case Calendar.MINUTE :
					spanDate = diff/nm;
					break;
				case Calendar.SECOND :
					spanDate = diff/ns;
					break;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return spanDate;
	}

	private static boolean BigDecimal(BigDecimal bigDecimal) {
		if (null == bigDecimal)
			return true;
		return false;
	}

	private static boolean checkList(List list) {
		if (list.size() <= 0)
			return true;
		return false;
	}

	private static boolean checkObjects(Object[] objs) {
		if (objs.length <= 0)
			return true;
		return false;
	}

	private static boolean checkSet(Set set) {
		if (set.isEmpty() || set.size() <= 0)
			return true;
		return false;
	}

	private static boolean checkMap(Map set) {
		if (set.isEmpty() || set.size() <= 0)
			return true;
		return false;
	}

	private static boolean checkDate(String[] strings) {
		if (strings.length <= 0)
			return true;
		return false;
	}

	private static boolean checkDate(Date date) {
		if (date == null) {
			return true;
		}
		return false;
	}

	private static boolean checkDouble(Double double1) {
		if (double1.doubleValue() == 0) {
			return true;
		}
		return false;
	}

	private static boolean checkLong(Long long1) {
		if (long1.longValue() == 0 || long1.longValue() == -1L) {
			return true;
		}
		return false;
	}

	private static boolean checkInteger(Integer integer) {
		if (integer.intValue() == 0 || integer.intValue() == -1) {
			return true;
		}
		return false;
	}

	private static boolean checkString(String string) {
		if (string.trim().length() <= 0 || "null".equalsIgnoreCase(string)) {
			return true;
		}
		return false;
	}

	public static Class gatAttrClassType(Object obj) {

		/* String */
		if (obj instanceof String) {
			return String.class;
		}
		/* Integer */
		if (obj instanceof Integer) {
			return Integer.class;
		}
		/* Long */
		if (obj instanceof Long) {
			return Long.class;
		}
		/* Double */
		if (obj instanceof Double) {
			return Double.class;
		}
		/* Date */
		if (obj instanceof Date) {
			return Date.class;
		}
		/* Date */
		if (obj instanceof java.sql.Date) {
			return java.sql.Date.class;
		}
		/* Timestamp */
		if (obj instanceof java.sql.Timestamp) {
			return java.sql.Timestamp.class;
		}
		/* List */
		if (obj instanceof List) {
			return List.class;
		}
		/* Set */
		if (obj instanceof Set) {
			return Set.class;
		}
		/* Map */
		if (obj instanceof Map) {
			return Map.class;
		}
		if (obj instanceof BigDecimal) {
			return BigDecimal.class;
		}

		if (obj instanceof Boolean) {
			return Boolean.class;
		}

		return Object.class;
	}

	public final static java.sql.Timestamp string2Time(String dateString,
			String forMat) {
		DateFormat dateFormat;

		dateFormat = new SimpleDateFormat(forMat, Locale.ENGLISH);// 设定格式
		dateFormat.setLenient(false);
		java.util.Date timeDate = null;
		try {
			timeDate = dateFormat.parse(dateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		java.sql.Timestamp dateTime = new java.sql.Timestamp(timeDate.getTime());// Timestamp类型,timeDate.getTime()返回一个long型

		return dateTime;
	}

	/**
	 * 计算当天加减 获取 根据指定格式 计算指定类型
	 * 
	 * @param format
	 *            格式：如"yyyy-MM-dd"
	 * @param type
	 *            类型 如:Calendar.YEAR,Calendar.MONTH,Calendar.DATE
	 * @param add
	 *            具体家的值
	 * @return String
	 */
	public static String addDate(String format, int type, int add) {
		Calendar cal = Calendar.getInstance();
		int newYear = cal.get(Calendar.YEAR);
		int newMonth = cal.get(Calendar.MONTH);
		int newDay = cal.get(Calendar.DATE);
		int newHour = cal.get(Calendar.HOUR_OF_DAY);
		int newMinute = cal.get(Calendar.MINUTE);
		int newSecond = cal.get(Calendar.SECOND);
		switch (type) {
		case Calendar.YEAR:
			newYear += add;
			break;
		case Calendar.MONTH:
			newMonth += add;
			break;
		case Calendar.DATE:
			newDay += add;
			break;
		case Calendar.HOUR_OF_DAY:
			newHour += add;
			break;
		case Calendar.MINUTE:
			newMinute += add;
			break;
		case Calendar.SECOND:
			newSecond += add;
			break;
		}
		cal.set(newYear, newMonth, newDay, newHour, newMinute, newSecond);
		SimpleDateFormat simpDate = new SimpleDateFormat(format);
		return simpDate.format(cal.getTime());
	}

	/**
	 * 指定日期 指定格式 指定类型 计算时间
	 * 
	 * @param inDate
	 *            按照format格式指定的时间，如：2010-02-02
	 * @param format
	 *            格式：如"yyyy-MM-dd"
	 * @param type
	 *            类型 如:Calendar.YEAR,Calendar.MONTH,Calendar.DATE
	 * @param add
	 *            具体增加的值
	 * @return string
	 */
	public static String addDateByTime(String inDate, String format, int type,
			int add) {
		SimpleDateFormat simpDate = new SimpleDateFormat(format);
		Calendar cal = Calendar.getInstance();
		try {
			cal.setTime(simpDate.parse(inDate));
			int newYear = cal.get(Calendar.YEAR);
			int newMonth = cal.get(Calendar.MONTH);
			int newDay = cal.get(Calendar.DATE);
			int newHour = cal.get(Calendar.HOUR_OF_DAY);
			int newMinute = cal.get(Calendar.MINUTE);
			int newSecond = cal.get(Calendar.SECOND);
			switch (type) {
			case Calendar.YEAR:
				newYear += add;
				break;
			case Calendar.MONTH:
				newMonth += add;
				break;
			case Calendar.DATE:
				newDay += add;
				break;
			case Calendar.HOUR_OF_DAY:
				newHour += add;
				break;
			case Calendar.MINUTE:
				newMinute += add;
				break;
			case Calendar.SECOND:
				newSecond += add;
				break;
			}
			cal.set(newYear, newMonth, newDay, newHour, newMinute, newSecond);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return simpDate.format(cal.getTime());
	}

	/**
	 * 将InputStream转换成某种字符编码的String
	 * 
	 * @param in
	 * @param encoding
	 * @return
	 * @throws Exception
	 */
	public static String InputStream2String(InputStream in, String encoding)
			throws Exception {

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] data = new byte[1024];
		int count = -1;
		while ((count = in.read(data, 0, 1024)) != -1)
			outStream.write(data, 0, count);

		data = null;
		return new String(outStream.toByteArray(), "ISO-8859-1");
	}

	/**
	 * 根据制定类型转换业务类型
	 * 
	 * @param value
	 * @param type
	 * @return
	 * @throws ParseException
	 */
	public static Object setPropertyType(Object value, Class type)
			throws ParseException {

		/* Char */
		if (type == char.class) {
			value = Double.valueOf(value.toString());
		}
		/* String */
		if (type == String.class) {
			value = value.toString();
		}
		/* Short */
		if (type == Short.class || type == short.class) {
			value = Double.valueOf(value.toString());
		}
		/* Integer */
		if (type == Integer.class || type == int.class) {
			value = Integer.valueOf(value.toString());
		}
		/* Long */
		if (type == Long.class || type == long.class) {
			value = Long.valueOf(value.toString());
		}
		/* Float */
		if (type == Float.class || type == float.class) {
			value = Double.valueOf(value.toString());
		}
		/* Double */
		if (type == Double.class || type == double.class) {
			value = Double.valueOf(value.toString());
		}
		/* Boolean */
		if (type == Boolean.class || type == boolean.class) {
			value = Boolean.valueOf(value.toString());
		}
		/* Date */
		if (type == Date.class) {
			SimpleDateFormat simpDate = new SimpleDateFormat("yyyy-MM-dd",
					Locale.ENGLISH);
			simpDate.setLenient(false);
			java.util.Date timeDate = simpDate.parse(value.toString());// util类型
			value = timeDate;
		}
		if (type == BigDecimal.class) {
			value = BigDecimal.valueOf(Long.valueOf(value.toString()));
		}

		return value;
	}


	// 将手字母大写
	public static String toFirstLetterUpperCase(String strName) {
		if (strName == null || strName.length() < 2) {
			return strName;
		}
		String firstLetter = strName.substring(0, 1).toUpperCase();
		return firstLetter + strName.substring(1, strName.length());
	}

	public static Object ifNull(Object object) {
		if(isNull(object)){
			return "";
		}else{
			return object;
		}
	}
	// 判断一个字符串是否都为数字  
	public static boolean isDigit(String strNum) {  
	    return strNum.matches("[0-9]{1,}");  
	}

	//将double转成int
	public static int isDouble(Object value) {
		Double d = (Double) value;
		int s1 = d.intValue();
		return s1;
	}
	
	public static String formatDate(String format) {
		if (format == null || "".equals(format.trim())) {
			return "";
		}
		SimpleDateFormat df = new SimpleDateFormat(format);
		
		return df.format(new Date());
	}
	
	/**
     * 生成随机数
     * @param max= 99999999 生成八位随机数
     * @return
     */
     public static String creatRandomNumber(int max) {
          Random r = new Random();
          String rt= String.valueOf(r.nextLong());
          String rtStr=rt.substring(rt.length()-max,rt.length()) ;
          return  rtStr;
     }
	
	public static String getBatchNo(){
		return formatDate("yyyyMMddHHmmss");
	}
	
	public static void main(String[] args) {
		System.out.println(getBatchNo());
	}
}
