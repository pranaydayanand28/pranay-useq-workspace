package commonutils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class CurrentDate {
	
	/**
	 * This method will find current date in yyyy-MM-dd format .
	 */
	public static String date() {
		Calendar cal = Calendar.getInstance();
		cal.get(Calendar.DATE);
		Date date = cal.getTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(date);
	}

	public static String EPOCH_DATE(){
		Calendar cal = Calendar.getInstance();
		cal.get(Calendar.DATE);
		Date date = cal.getTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(date);
	}
}
