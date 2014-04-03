package my.example.activityrecognition.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.gson.Gson;
import com.orm.SugarRecord;
import org.la4j.vector.Vector;
import org.la4j.vector.dense.BasicVector;

/**
 * Created by ggauravr on 4/2/14.
 */
public class Sample extends SugarRecord<Sample> {
    /**
     *  class level constants
     */
    private static final String TAG = "Sample";
    private static final int TYPE_SAMPLE = 1;
    private static final int TYPE_MODEL   = 2;
    private static final double LAMBDA =  0.0001;

//    private long int mTimestamp;
    private Context mContext;
    private int mActivity;
    private int mRingerMode;
    private int mApproxTime;
    private int mDayOfWeek;
    private int mHour;
    private int mPredictedLabel;
    private int mOriginalLabel;

    private Gson mGson = null;
    private HelperClass mHelperInstance;

    private Vector mModelVector = new BasicVector(new double[] {0, 0, 0, 0, 0});

    public Sample(Context ctx){
        super(ctx);
    }

    public Sample(Context ctx, int _activity, int _ringerMode, int _dayOfWeek,  int _approxTime, int _hour){
        super(ctx);

        mContext = ctx;
//        TO DO: get time and store the timestamp
        mActivity = _activity;
        mRingerMode = _ringerMode;
        mApproxTime = _approxTime;
        mDayOfWeek = _dayOfWeek;
        mHour = _hour;

//        TO DO: fetch ground truth from the schedule and store it
        mHelperInstance = HelperClass.getInstance();
        handle();
    }

    public void handle(){

        if(isConnectedToNetwork()){
            /**
             * fetch model parameters
             * save latest param in the preferences file ??
             * if there are previous entries stored in DB, send them to th server
             * 
             */
            fetchModel();
            saveModel();

            syncEntries();
        }

        /**
         * compute the gradient, and train the model
         *
         */        
        updateModel(getGradient());

        if(isConnectedToNetwork()){
            /**
             * sync any remaining DB entries, before updating the current one
             * sync data with the server
             *
             */
        }
        else{
            /**
             * store the computations in DB
             */
        }
    }

    public Vector getVector(int type){

        Vector vector= null;

        if(type == TYPE_SAMPLE){
            vector = new BasicVector(new double[]{mActivity, mRingerMode, mDayOfWeek, mApproxTime, mHour});
        }

        return vector;
    }

    public boolean isConnectedToNetwork(){

        ConnectivityManager connMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }

        return false;
    }

    public Vector getGradient(){
        Vector x = getVector(TYPE_SAMPLE);
        double wx = x.innerProduct(mModelVector);
        double probability = 1/(1+Math.exp(-wx));
        double factor = -((double)mOriginalLabel - probability)*probability*(1-probability);

        // gradient
        return x.multiply(factor);
    }

    public void updateModel(Vector gradient){
        mModelVector = mModelVector.subtract(gradient.multiply(LAMBDA));

        saveModel();
    }

    public void saveModel(){
        mHelperInstance.saveToPreferences(mContext, "model", ( new BasicVector(mModelVector) ).toArray());
    }

    public void fetchModel(){
        String stringModel = HelperClass.getInstance().getFromPreferences(mContext, "model", "");
        /**
         * TO DO: fetch params from network
         * 
         *  fetch model params from network
         * 
         */
        
        // if some preferences are stored, restore it
        if(stringModel != ""){
            mModelVector = new BasicVector(mHelperInstance.getGson().fromJson(stringModel, double[].class));
        }
    }

    public void syncEntries(){

    }

}
