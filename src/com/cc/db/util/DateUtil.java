package com.cc.db.util;

import com.cc.logging.LOG;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    public final static SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yyyy");
    public final static SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss.SSS");
    public final static SimpleDateFormat sdfTimestamp = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");

    public static Date parseDBDate(String dateString) {
        try {
            return sdfDate.parse(dateString);
        } catch (ParseException e) {
            LOG.getLog(DateUtil.class)
                    .error(e);
            return null;
        }
    }

    public static String formatDBDate(Date date) {
        if (date == null)
            return null;
        return sdfDate.format(date);
    }

    public static java.sql.Date toSQLDate(Date date) {
        java.sql.Date sqlDate = null;
        if (date != null)
            sqlDate = new java.sql.Date(date.getTime());
        return sqlDate;
    }

    public static Timestamp toTimestamp(Date date) {
        Timestamp timestamp = null;
        if (date != null)
            timestamp = new Timestamp(date.getTime());
        return timestamp;
    }

    public static Time toTime(Date date) {
        Time time = null;
        if (date != null)
            time = new Time(date.getTime());
        return time;
    }

    public static java.sql.Date trunc(Date date) {
        java.sql.Date sqlDate = null;
        if (date != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.set(Calendar.HOUR,
                    0);
            cal.set(Calendar.MINUTE,
                    0);
            cal.set(Calendar.SECOND,
                    0);
            cal.set(Calendar.MILLISECOND,
                    0);

            sqlDate = new java.sql.Date(cal.getTimeInMillis());
        }
        return sqlDate;
    }

    public static int calcDateDifference(Date a,
                                         Date b) {
        int tempDifference = 0;
        int difference = 0;
        Calendar earlier = Calendar.getInstance();
        Calendar later = Calendar.getInstance();

        if (a.compareTo(b) < 0) {
            earlier.setTime(a);
            later.setTime(b);
        } else {
            earlier.setTime(b);
            later.setTime(a);
        }

        while (earlier.get(Calendar.YEAR) != later.get(Calendar.YEAR)) {
            tempDifference = 365
                    * (later.get(Calendar.YEAR)
                    - earlier.get(Calendar.YEAR));
            difference += tempDifference;

            earlier.add(Calendar.DAY_OF_YEAR,
                    tempDifference);
        }

        if (earlier.get(Calendar.DAY_OF_YEAR) != later.get(Calendar.DAY_OF_YEAR)) {
            tempDifference = later.get(Calendar.DAY_OF_YEAR)
                    - earlier.get(Calendar.DAY_OF_YEAR);
            difference += tempDifference;

            earlier.add(Calendar.DAY_OF_YEAR,
                    tempDifference);
        }

        return difference;
    }

    public static Date getNextMonthFirstDate(Date date) {
        if (date == null)
            return null;

        Calendar tmpCal = Calendar.getInstance();
        tmpCal.setTime(date);
        tmpCal.add(Calendar.MONTH,
                1);
        tmpCal.set(Calendar.DAY_OF_MONTH,
                1);

        return tmpCal.getTime();
    }

    public static Date getMonthFirstDate(Date date) {
        if (date == null)
            return null;
        Calendar tmpCal = Calendar.getInstance();
        tmpCal.setTime(date);
        tmpCal.set(Calendar.DAY_OF_MONTH,
                1);

        return tmpCal.getTime();
    }

    public static Date getMonthLastDate(Date date) {
        Calendar tmpCal = Calendar.getInstance();
        tmpCal.setTime(date);
        tmpCal.add(Calendar.MONTH,
                1);
        tmpCal.set(Calendar.DAY_OF_MONTH,
                1);
        tmpCal.add(Calendar.DAY_OF_MONTH,
                -1);
        // tmpCal.roll(Calendar.DAY_OF_MONTH,
        // tmpCal.getMaximum(Calendar.MONTH));

        return tmpCal.getTime();
    }

    public static Date getYearFirstDate(Date date) {
        if (date == null)
            return null;
        Calendar tmpCal = Calendar.getInstance();
        tmpCal.setTime(date);
        tmpCal.set(Calendar.MONTH,
                Calendar.JANUARY);
        tmpCal.set(Calendar.DAY_OF_MONTH,
                1);

        return tmpCal.getTime();
    }

    public static Date dateNoTime(Date date) {
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY,
                0);
        calendar.set(Calendar.MINUTE,
                0);
        calendar.set(Calendar.SECOND,
                0);
        calendar.set(Calendar.MILLISECOND,
                0);

        return calendar.getTime();
    }

    // get the date fields by date
    public static DateFields getDateFields(Date d) {
        DateFields df = new DateUtil().new DateFields();
        return df.getDateFields(d);
    }

    // get date by date fields
    public static Date getDateByDateFields(DateFields fields) {
        return new DateUtil().new DateFields().getDateByDateFields(fields);
    }

    public class DateFields {
        public int day = 0;
        public int month = 0;
        public int year = 0;

        public DateFields getDateFields(Date date) {
            if (date == null)
                return null;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            DateFields f = new DateFields();

            f.year = calendar.get(Calendar.YEAR);
            f.month = calendar.get(Calendar.MONTH);
            f.day = calendar.get(Calendar.DAY_OF_MONTH);

            return f;
        }

        public Date getDateByDateFields(DateFields dfields) {
            if (dfields == null)
                return null;
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR,
                    dfields.year); // set year
            calendar.set(Calendar.MONTH,
                    dfields.month); // set month
            calendar.set(Calendar.DAY_OF_MONTH,
                    dfields.day); // set day

            return calendar.getTime();
        }
    }

    public static Date dateAddSubstract(Date date,
                                        String method,
                                        String by,
                                        String what) {
        if (method.equals("+"))
            method = "";
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date);
        if (what.equals("d")) {
            c1.add(Calendar.DATE,
                    Integer.valueOf(method
                            + by));
        }
        if (what.equals("m")) {
            c1.add(Calendar.MONTH,
                    Integer.valueOf(method
                            + by));
        }
        if (what.equals("y")) {
            c1.add(Calendar.YEAR,
                    Integer.valueOf(method
                            + by));
        }
        return c1.getTime();
    }
}
