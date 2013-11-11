package de.arnefeil.bewegungsmelder.models;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by arne on 10/3/13.
 */
final public class Date implements Comparable<Date> {

    final private int day;
    final private int month;
    final private int year;

    public Date(String date) throws IllegalArgumentException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setLenient(false);
        try {
            df.parse(date);
            String[] splitDate = date.split("[-]");
            this.day = Integer.parseInt(splitDate[2]);
            this.month = Integer.parseInt(splitDate[1]);
            this.year = Integer.parseInt(splitDate[0]);
        } catch (Exception e) {
            throw new IllegalArgumentException("Kein gültiges Datum");
        }
    }

    public int getDay() {
        return this.day;
    }

    public int getMonth() {
        return this.month;
    }

    public int getYear() {
        return this.year;
    }

    public Date dateInDays(int countDays) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(df.parse("" + this.year
                    + "-" + this.month
                    + "-" + this.day));
            cal.add(Calendar.DATE, countDays);
        } catch (Exception e) {
            e.printStackTrace();
        }
        df.setCalendar(cal);
        String date = df.format(cal.getTime());

        return new Date(date);
    }

    public static Date today() {
        Calendar cal = new GregorianCalendar();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setCalendar(cal);
        String date = df.format(cal.getTime());

        return new Date(date);
    }

    public String getWeekday() {
        String[] weekdays = {"So.", "Mo.", "Di.", "Mi.", "Do.", "Fr.", "Sa."};
        Calendar cal = new GregorianCalendar();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            cal.setTime(df.parse("" + this.year
                    + "-" + this.month
                    + "-" + this.day));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return weekdays[cal.get(Calendar.DAY_OF_WEEK) - 1];
    }

    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof Date) {
            Date d = (Date) o;
            result = d.getDay() == this.getDay()
                    && d.getMonth() == this.getMonth()
                    && d.getYear() == this.getYear();
        }
        return result;
    }

    @Override
    public int hashCode() {
        return this.getYear() * 365 + this.getMonth() * 30 + this.getDay();
    }

    @Override
    public String toString() {
        return this.getWeekday() + " " + this.getDay() + "." +
                this.getMonth() + "." + this.getYear();
    }

    @Override
    public int compareTo(Date another) {
        return this.hashCode() - another.hashCode();
    }
}
