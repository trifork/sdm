package com.trifork.stamdata.replication;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class TimeHelper {

	public static Date tomorrow() {

		Calendar now = new GregorianCalendar();
		now.add(Calendar.DATE, 1);
		return now.getTime();
	}

	public static Date yesterday() {

		Calendar now = new GregorianCalendar();
		now.add(Calendar.DATE, -1);
		return now.getTime();
	}
}
