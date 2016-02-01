package de.struckmeierfliesen.ds.micromanagement;

import android.support.annotation.NonNull;
import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Util {
    private Util() {}
    public static final int TEXT_BLACK = 0xFF212121;
    public static final int WHITE = 0xFFFFFFFF;
    public static final int BLACK = 0xFF000000;
    public static final int GREEN = 0xFF4CAF50;
    public static final int LIGHT_GREEN = 0xFF8BC34A;
    public static final int LIME = 0xFFCDDC39;
    public static final int YELLOW = 0xFFFFEB3B;
    public static final int AMBER = 0xFFFFC107;
    public static final int ORANGE = 0xFFFF9800;
    public static final int DEEP_ORANGE = 0xFFFF5722;
    public static final int RED = 0xFFFF5722;

    // TODO maybe profile
    // returns true if either date is null
    public static boolean isSameDay(Date date1, Date date2) {
        return date1 == null || date2 == null
                || DateFormat.format("dd.MM.yy", date1).equals(DateFormat.format("dd.MM.yy", date2));
    }

    public static Date addDays(Date date, int value) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, value);
        return c.getTime();
    }

    public static int getDayDifference(Date date1, Date date2) {
        /*if (isSameDay(date1, date2)) return 0;
        else if (isSameDay(addDays(date1, 1), date2)) return -1;
        else if (isSameDay(addDays(date1, -1), date2)) return 1;
        else {*/
            date1 = dateAtMidnight(date1);
            date2 = dateAtMidnight(date2);
            long diffInMillies = date1.getTime() - date2.getTime();
            return (int) TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        //}
    }

    public static Date dateAtMidnight(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    @NonNull
    public static String getWeekday(Date date) {
        switch (getDayDifference(new Date(), date)) {
            case 0:
                return "Today";
            case 1:
                return "Yesterday";
            default:
                return DateFormat.format("EEEE", date).toString();
        }
    }
}
