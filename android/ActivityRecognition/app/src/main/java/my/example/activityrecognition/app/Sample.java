package my.example.activityrecognition.app;

import android.content.Context;

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
public class Sample extends SugarRecord<Sample> implements Serializable {

    private int
            mActivity,
            mRingerMode,
            mApproxTime,
            mDayOfWeek,
            mHour,
            mPredictedLabel,
            mOriginalLabel;

    private long mTimestamp;
    private String 
        mModel,
        mGradient;

    public Sample(Context ctx) {
        super(ctx);
    }

    public Sample(
                            /* parameters */
                            Context ctx,
                            int _activity,
                            int _ringerMode,
                            int _dayOfWeek,
                            int _approxTime,
                            int _hour
    ) {

        super(ctx);

        mTimestamp = new Date().getTime();
        mActivity = _activity;
        mRingerMode = _ringerMode;
        mApproxTime = _approxTime;
        mDayOfWeek = _dayOfWeek;
        mHour = _hour;
        // check and set it later
        mOriginalLabel = 0;
        mPredictedLabel = 0;
    }

    public Vector getSampleVector() {

       Vector vector = new BasicVector(
                new double[]{
                        mActivity,
                        mRingerMode,
                        mDayOfWeek,
                        mApproxTime,
                        mHour
                }
        );

        return vector;
    }

    public String getCommObject(){

        JSONObject commObject = new JSONObject();
        Vector sampleVector = new BasicVector(
                new double[]{
                        // id is an extra parameter sent to the server
                        id,
                        mActivity,
                        mRingerMode,
                        mDayOfWeek,
                        mApproxTime,
                        mHour,
                        mPredictedLabel,
                        mOriginalLabel
                }
        );
        String sample = HelperClass.getInstance().getGson().toJson(((BasicVector)sampleVector).toArray());

        try {
            commObject.put("sample", sample);
            /*commObject.put("model", mModel);
            commObject.put("gradient", mGradient);*/
            /*commObject.put("model", getStringFromVector(model));
            commObject.put("gradient", getStringFromVector(gradient));*/
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return sample;

//        return commObject.toString();
    }

    public String getStringFromVector(Vector vector){
        return HelperClass.getInstance().getGson().toJson((new BasicVector(vector)).toArray());
    }

    public void setGradient(String gradient){
        mGradient = gradient;
    }

    public void setModel(String model){
        mModel = model;
    }

    public void setOriginalLabel(int label) {
        mOriginalLabel = label;
    }

    public void setPredictedLabel(int label) {
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

    public int getDayOfWeek() {
        return mDayOfWeek;

    }

}
