package my.example.activityrecognition.app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.orm.SugarRecord;

import org.la4j.vector.Vector;
import org.la4j.vector.dense.BasicVector;
import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ggauravr on 4/2/14.
 */
public class Sample extends SugarRecord<Sample> implements Serializable{

    //    private long int mTimestamp;
    /*private Context
            mContext;*/

    private int mActivity;
    private int mRingerMode;
    private int mApproxTime;
    private int mDayOfWeek;
    private int mHour;
    private long mTimestamp;
    private int mPredictedLabel;
    private int mOriginalLabel;

    public Sample(Context ctx){
        super(ctx);
    }

    public Sample(Context ctx, int _activity, int _ringerMode, int _dayOfWeek, int _approxTime, int _hour) {
        super(ctx);

//        int hour, minute, row, col, position;
//        Calendar rightNow = Calendar.getInstance();
//        boolean[] schedule = new boolean[Constants.N_GRIDS];
//        String stringSchedule;

//        mContext = ctx;
//        mHelperInstance = HelperClass.getInstance(ctx);

//        TO DO: get time and store the timestamp
        mTimestamp = new Date().getTime();
        mActivity = _activity;
        mRingerMode = _ringerMode;
        mApproxTime = _approxTime;
        mDayOfWeek = _dayOfWeek;
        mHour = _hour;
//        check and set it later
        mOriginalLabel = 0;

        /*Log.d(TAG, "Hour : " + hour + ", Minute: " + minute + ", Day Of Week: " + mDayOfWeek);
        Log.d(TAG, "Row : " + row + ", Column: " + col);
        Log.d(TAG, "Position : " + position + ", Original Label: " + mOriginalLabel);*/
    }

    public void setOriginalLabel(int label){
        mOriginalLabel = label;
    }

    public void setPredictedLabel(int label){
        mPredictedLabel = label;
    }

    public int getActivity() {
        return mActivity;
    }

    public int getRingerMode() {
        return mRingerMode;
    }

    public int getApproxTime() {
        return mApproxTime;
    }

    public int getHour() {
        return mHour;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    public int getPredictedLabel() {
        return mPredictedLabel;
    }

    public int getOriginalLabel() {
        return mOriginalLabel;
    }

    public int getDayOfWeek(){
        return mDayOfWeek;

    }

}
