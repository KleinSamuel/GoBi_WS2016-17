package dateNtimeStuff;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Containing methods for date and time handling.
 * 
 * @author Samuel Klein
 */
public class DateFactory {

	/**
	 * Returns current date and time as String (yyyy/mm/dd hh:mm:ss)
	 * 
	 * @return String 
	 */
	public static String getDateAsString(){
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		
		return dateFormat.format(cal.getTime());
	}
	
	
	
	
	
	public static void main(String[] args) {
		
		
		
	}
}
