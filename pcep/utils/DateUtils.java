package pcep.utils;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DateUtils {
	public static Calendar getWindowStart(Calendar cal, int windowType) {
		Calendar result = null;
		if (windowType == Calendar.MONTH) {
			result = new GregorianCalendar(
					cal.get(Calendar.YEAR), 
					cal.get(Calendar.MONTH), 
					1,
					0,
					0,
					0);
		}
		else if (windowType == Calendar.DATE) {
			result = new GregorianCalendar(
					cal.get(Calendar.YEAR), 
					cal.get(Calendar.MONTH), 
					cal.get(Calendar.DATE));
		}
		else if (windowType == Calendar.HOUR) {
			result = new GregorianCalendar(
					cal.get(Calendar.YEAR), 
					cal.get(Calendar.MONTH), 
					cal.get(Calendar.DATE),
					cal.get(Calendar.HOUR),
					0,
					0);
		}
		else if (windowType == Calendar.MINUTE) {
			result = new GregorianCalendar(
					cal.get(Calendar.YEAR), 
					cal.get(Calendar.MONTH), 
					cal.get(Calendar.DATE),
					cal.get(Calendar.HOUR),
					cal.get(Calendar.MINUTE),
					0);
		}
		else {
			// TODO: Throw error
		}
		return result;
	}
	
	public static String formattedDate(Calendar cal) {
		return DateUtils.formattedDate(cal.getTimeInMillis());
	}
	
	public static String formattedDate(long time) {
		return (new SimpleDateFormat("dd.MM.YYYY hh:mm:ss")).format(time);
	}
	
	public static String formattedDate(Date date) {
		return DateUtils.formattedDate(date.getTime());
	}
}
