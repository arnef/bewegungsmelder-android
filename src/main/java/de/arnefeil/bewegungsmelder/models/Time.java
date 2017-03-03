package de.arnefeil.bewegungsmelder.models;

/**
 * Created by arne on 10/3/13.
 */
public class Time {

    private int timestamp;
    private int hours;
    private int minutes;
    private String time;
    private boolean allday;

    public Time(int timestamp) {
        this.timestamp = timestamp;
        this.hours = timestamp / 3600;
        this.minutes = timestamp / 60 % 60;
        this.allday = false;
    }
    public Time(String time) {
        this.time = time;
    }

    public static Time allDayTime() {
        Time time = new Time(0);
        time.allday = true;
        return time;
    }


    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof Time) {
            Time t = (Time) o;
            result = t.hashCode() == this.hashCode();
        }

        return result;
    }

    @Override
    public int hashCode() {
        if (this.time != null) {
            return time.hashCode();
        } else {
            return this.timestamp;
        }
    }

    @Override
    public String toString() {
        if (this.allday) return "";
        if (time != null) {
            return time;
        } else {
            String hours = "" + this.hours;
            String minutes = "" + this.minutes;
            if (this.hours < 10) hours = "0" + this.hours;
            if (this.minutes < 10) minutes = "0" + this.minutes;
            return hours + ":" + minutes;
        }
    }

    public int getHours() {
        return this.hours;
    }

    public int getMinutes() {
        return this.minutes;
    }

    public boolean isAllday() {
        return this.allday;
    }
}
