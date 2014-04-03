package my.example.activityrecognition.app;

import java.util.Calendar;

/**
 * Created by ggauravr on 3/1/14.
 */
public class DateHelper {

    public DateHelper(){

    }

    public static String getFuzzyTime(int fuzzyTime){
        String time = "";

        if(fuzzyTime == Calendar.AM)
            time = "AM";
        else
            time = "PM";

        return time;
    }

    public static String getDayOfWeek(int dayOfWeek){
        String day = "";

        switch (dayOfWeek){
            case 1:
                day = "Sunday";
                break;
            case 2:
                day = "Monday";
            break;
            case 3:
                day = "Tuesday";
            break;
            case 4:
                day = "Wednesday";
            break;
            case 5:
                day = "Thursday";
            break;
            case 6:
                day = "Friday";
            break;
            case 7:
                day = "Saturday";
            break;
        }

        return day;
    }

}
