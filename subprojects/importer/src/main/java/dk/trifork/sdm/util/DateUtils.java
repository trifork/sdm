package dk.trifork.sdm.util;

import org.apache.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateUtils {
	public static final Calendar FUTURE = toCalendar(2999,12,31);
	private static Logger logger = Logger.getLogger(DateUtils.class);
	

	/**
	 * @param long1 representing a date sing the format: yyyyMMdd.
	 * @return a String representing the ISO 8601 date without time zone.
	 */
	public static String toISO8601date(Long long1) {
		if (long1 == null || long1 == 0)
			return null;
		SimpleDateFormat informat = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat outformat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return "" + outformat.format(informat.parse("" + long1));
		} catch (ParseException e) {
			logger.error("Error converting date to iso 8601 date format. Returning unformated string: '" + long1 + "'");
			return "" + long1;
		}
	
	}

	public static String toFilenameDatetime(Calendar cal) {
		SimpleDateFormat outformat = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss");
		return outformat.format(cal.getTime());
	}

	/**
	 * @param year 
	 * @param month (1-12)
	 * @param date (1-31)
	 */
	public static Calendar toCalendar(int year, int month, int date) {
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(year, month-1, date);
		return cal;
	}
	/**
	 * @param year 
	 * @param month (1-12)
	 * @param date (1-31)
	 */

	public static Calendar toCalendar(int year, int month, int date, int hours, int minutes, int secs) {
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(year, month-1, date, hours, minutes, secs);
		return cal;
	}

	public static Calendar toCalendar(java.sql.Date date) {

		if (date == null) return null;
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.setTimeInMillis(date.getTime());
		return cal;
	}

	public static Calendar toCalendar(java.util.Date date) {
		if (date == null) return null;
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.setTimeInMillis(date.getTime());
		return cal;
	}

	public static String toMySQLdate(Calendar date) {
		if (date == null){
			logger.warn("Cannot convert null to mysqldate");
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date.getTime());
	}

}
