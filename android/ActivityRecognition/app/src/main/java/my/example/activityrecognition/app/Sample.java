package my.example.activityrecognition.app;

import android.content.Context;
import android.util.Log;

import com.orm.SugarRecord;

import org.json.JSONException;
import org.json.JSONObject;
import org.la4j.vector.Vector;
import org.la4j.vector.dense.BasicVector;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by ggauravr on 4/2/14.
 */
public class Sample /*extends SugarRecord<Sample> implements Serializable*/ {

    private double[]
            mActivity = new double[Constants.N_ACTIVITIES],
            mRingerMode = new double[Constants.N_RINGER_MODES],
            mApproxTime = new double[Constants.N_AM_PM],
            mDayOfWeek = new double[Constants.N_DAY_OF_WEEK],
            mHour = new double[Constants.N_HOUR];

    private int
            mOriginalDayOfWeek,
            mOriginalHourOfDay,
            mPredictedLabel,
            mOriginalLabel;

    private long mTimestamp;
    private String
            mModel,
            mGradient;

    /*public Sample(Context ctx) {
        super(ctx);
    }*/

    public Sample(
                            /* parameters */
//                            Context ctx,
                            int _activity,
                            int _ringerMode,
                            int _dayOfWeek,
                            int _approxTime,
                            int _hour
    ) {

//        super(ctx);

        mTimestamp = new Date().getTime();
        mActivity[_activity] = 1;
        mRingerMode[_ringerMode] = 1;
        mApproxTime[_approxTime] = 1;

        mOriginalDayOfWeek = _dayOfWeek;
        mOriginalHourOfDay = _hour;

        // sunday or saturday, its a weekend
        if (_dayOfWeek == 1 || _dayOfWeek == 7) {
            mDayOfWeek[1] = 1;
        } else {
            mDayOfWeek[0] = 1;
        }
//        mDayOfWeek = _dayOfWeek;
        if (_hour >= 7 && _hour <= 20) {
            mHour[0] = 1;
        } else {
            mHour[0] = 1;
        }
//        mHour = _hour;
        // check and set it later
        mOriginalLabel = 0;
        mPredictedLabel = 0;
    }

    public double[] getSampleArray(){
        // this will be stored in preferences

        // no bias term here..
        double[] sampleArray = new double[Constants.N_DIMENSIONS+2];
        System.arraycopy(mActivity, 0, sampleArray, 0, mActivity.length);
        System.arraycopy(mRingerMode, 0, sampleArray, mActivity.length, mRingerMode.length);
        System.arraycopy(mDayOfWeek, 0, sampleArray, mActivity.length+mRingerMode.length, mDayOfWeek.length);
        System.arraycopy(mApproxTime, 0, sampleArray, mActivity.length+mRingerMode.length+mDayOfWeek.length, mApproxTime.length);
        System.arraycopy(mHour, 0, sampleArray, mActivity.length+mRingerMode.length+mDayOfWeek.length+mApproxTime.length, mHour.length);
        sampleArray[Constants.N_DIMENSIONS-1] = 0; // bias
        sampleArray[Constants.N_DIMENSIONS] = 0; // predicted label
        sampleArray[Constants.N_DIMENSIONS+1] = mOriginalLabel; // original label

        return sampleArray;
    }

    public Vector getSampleVector() {
        Vector vector = new BasicVector(getSampleArray());
        return vector;
    }

    public Vector getSampleVectorForProcessing() {
        Vector vector = new BasicVector(getSampleArrayForProcessing());
        return vector;
    }

    public double[] getSampleArrayForProcessing() {
        double[] sampleArray = new double[Constants.N_DIMENSIONS];
        System.arraycopy(mActivity, 0, sampleArray, 0, mActivity.length);
        System.arraycopy(mRingerMode, 0, sampleArray, mActivity.length, mRingerMode.length);
        System.arraycopy(mDayOfWeek, 0, sampleArray, mActivity.length+mRingerMode.length, mDayOfWeek.length);
        System.arraycopy(mApproxTime, 0, sampleArray, mActivity.length+mRingerMode.length+mDayOfWeek.length, mApproxTime.length);
        System.arraycopy(mHour, 0, sampleArray, mActivity.length+mRingerMode.length+mDayOfWeek.length+mApproxTime.length, mHour.length);
        sampleArray[Constants.N_DIMENSIONS-1] = 1; // bias

        return sampleArray;
    }

    public String getCommObject() {

        JSONObject commObject = new JSONObject();
        String sample = "";
        // extra params : id, predicted label and original label
        double[] commObjectArray = new double[Constants.N_DIMENSIONS+3];

//        commObjectArray[0] = id;
        System.arraycopy(getSampleArray(), 0, commObjectArray, 1, getSampleArray().length);
        // bias
        commObjectArray[Constants.N_DIMENSIONS] = 1;
        commObjectArray[Constants.N_DIMENSIONS+1] = mPredictedLabel;
        commObjectArray[Constants.N_DIMENSIONS+2] = mOriginalLabel;

        sample = HelperClass.getInstance().getGson().toJson(commObjectArray);

        try {
            commObject.put("sample", sample);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return sample;
    }

    public String getStringFromVector(Vector vector) {
        return HelperClass.getInstance().getGson().toJson((new BasicVector(vector)).toArray());
    }

    public void setGradient(String gradient) {
        mGradient = gradient;
    }

    public void setModel(String model) {
        mModel = model;
    }

    public void setOriginalLabel(int label) {
        mOriginalLabel = label;
    }

    public void setPredictedLabel(int label) {
        mPredictedLabel = label;
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

   /* public int getActivity() {
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
    */
    public int getOriginalDayOfWeek() {
        return mOriginalDayOfWeek;

    }

    public int getOriginalHourOfDay() {
        return mOriginalHourOfDay;

    }
}
