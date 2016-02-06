package de.struckmeierfliesen.ds.micromanagement;

import android.support.annotation.NonNull;
import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
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

    public static String getDayAbbrev(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        String[] days;
        if (Locale.getDefault().getCountry().equals(Locale.GERMAN.getCountry())) {
            days = new String[]{"So", "Mo", "Di", "Mi", "Do", "Fr", "Sa"};
        } else {
            days = new String[]{"Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"};
        }
        return days[day - 1];
    }

    public static int getDayDifference(Date date1, Date date2) {
        /*if (isSameDay(date1, date2)) return 0;
        else if (isSameDay(addDays(date1, 1), date2)) return -1;
        else if (isSameDay(addDays(date1, -1), date2)) return 1;
        else {*/
            date1 = getStartOfDay(date1);
            date2 = getStartOfDay(date2);
            long diffInMillies = date1.getTime() - date2.getTime();
            return (int) TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        //}
    }

    public static Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    public static Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
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

    public static String formatDate(Date date) {
        if (date == null) return "null";
        return DateFormat.format("dd.MM.yy", date).toString();
    }
}
