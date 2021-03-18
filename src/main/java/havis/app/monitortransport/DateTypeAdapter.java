package havis.app.monitortransport;

import java.util.Calendar;
import java.util.Date;

import javax.xml.bind.DatatypeConverter;

public class DateTypeAdapter {

	public static Date parseDate(String string) {
        if (string == null)
            return null;
		return DatatypeConverter.parseDateTime(string).getTime();
	}

	public static String printDate(Date date) {
        if (date == null)
            return null;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return DatatypeConverter.printDateTime(calendar);
	}
}