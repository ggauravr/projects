package my.example.activityrecognition.app;

/**
 * Created by ggauravr on 4/3/14.
 */
public class Constants {

    public static final String PREFERENCES_FILE = "UserPreferences";

    // initial hour : 7 AM
    public static final int 
        INIT_HR = 0,
        N_ROWS = 64,
        N_COLS = 8,
        N_GRIDS = N_ROWS * N_COLS,

        SAMPLE_FREQUENCY = 30000,

        N_ACTIVITIES = 6,
        N_RINGER_MODES = 3,
        // 0 - WORK HOUR, 1 - OTHER
        N_HOUR = 2,
        // 0 - AM , 1 PM
        N_AM_PM = 2,
        // 0 - WEEKDAY, 1 - WEEKEND
        N_DAY_OF_WEEK = 2,
        N_BIAS = 1,

        N_DIMENSIONS =  N_ACTIVITIES + N_RINGER_MODES + N_HOUR + N_AM_PM + N_DAY_OF_WEEK + N_BIAS;


    // TO DO : add names of services and activities with namespace here.. 
    // for reference in other places
}
