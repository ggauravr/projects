package my.example.activityrecognition.app;

import android.database.sqlite.SQLiteDatabase;

import java.util.Date;

/**
 * Created by ggauravr on 3/3/14.
 */
public class Feature {

    private int mActivity;
    private int mConfidence;
    private int mApproxTime;
    private int mHour;
    private int mDayOfWeek;
    long mTimestamp;

    public Feature(int activity, int confidence, int approxTime, int hour, int day){
        mActivity = activity;
        mConfidence = confidence;
        mHour = hour;
        mDayOfWeek = day;
        mTimestamp = new Date().getTime();
    }

    public int getActivity() {
        return mActivity;
    }

    public int getConfidence() {
        return mConfidence;
    }

    public int getApproxTime() {
        return mApproxTime;
    }

    public int getHour() {
        return mHour;
    }

    public int getDayOfWeek() {
        return mDayOfWeek;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    public void save(SQLiteDatabase db){
        DBHelper.saveFeature(db, this);
    }

}
