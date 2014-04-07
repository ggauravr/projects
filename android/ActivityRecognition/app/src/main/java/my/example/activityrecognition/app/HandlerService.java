package my.example.activityrecognition.app;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ggauravr on 3/1/14.
 */
public class HandlerService extends IntentService {

    private final String TAG = getClass().getSimpleName();
    private AudioManager mAudioManager;
    private Context mContext;

    private Intent mNetworkHandlerIntent;
    private Sample mSample;

    public HandlerService(){
        super("HandlerService");
    }

    @Override
    public void onCreate() {

        super.onCreate();

        mContext = getApplicationContext();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "HandlerService being destroyed.. ");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        Calendar now = Calendar.getInstance();

        if(ActivityRecognitionResult.hasResult(intent)){

            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            DetectedActivity mostProbableActivity = result.getMostProbableActivity();
            int confidence = mostProbableActivity.getConfidence();
            String 
                action = getType(mostProbableActivity.getType()),
                ringerMode = getRingerModeAsString(mAudioManager.getRingerMode());

            Intent trainingServiceIntent = new Intent(mContext, TrainingService.class);
            trainingServiceIntent.putExtra("activity_type",mostProbableActivity.getType() );
            trainingServiceIntent.putExtra("ringer_mode", mAudioManager.getRingerMode());
            trainingServiceIntent.putExtra("day_of_week", now.get(Calendar.DAY_OF_WEEK));
            trainingServiceIntent.putExtra("am_pm", now.get(Calendar.AM_PM));
            trainingServiceIntent.putExtra("hour_of_day", now.get(Calendar.HOUR_OF_DAY));
            startService(trainingServiceIntent);
/*
            mSample = new Sample(
                    getApplicationContext(),

                    mAudioManager.getRingerMode(),
                    now.get(Calendar.DAY_OF_WEEK),
                    now.get(Calendar.AM_PM),
                    now.get(Calendar.HOUR_OF_DAY)
            );*/

            Log.d(TAG, "Action: " + action + ", Confidence: " + confidence + ", Ringer Mode: " + ringerMode);
            Log.d(TAG, "Time: " + DateHelper.getFuzzyTime(now.get(Calendar.AM_PM)) + ", Hour: " + now.get(Calendar.HOUR)+ ", Day of Week: "+ DateHelper.getDayOfWeek(now.get(Calendar.DAY_OF_WEEK)));

         }
    }

    private String getRingerModeAsString(int ringerMode){
        switch (ringerMode){
            case AudioManager.RINGER_MODE_NORMAL:
                return "Normal";
            case AudioManager.RINGER_MODE_SILENT:
                return "Silent";
            case AudioManager.RINGER_MODE_VIBRATE:
                return "Vibrate";
        }

        return "Unknown";
    }

    private String getType(int type){
        if(type == DetectedActivity.UNKNOWN)
            return "Unknown";
        else if(type == DetectedActivity.IN_VEHICLE)
            return "In Vehicle";
        else if(type == DetectedActivity.ON_BICYCLE)
            return "On Bicycle";
        else if(type == DetectedActivity.ON_FOOT)
            return "On Foot";
        else if(type == DetectedActivity.STILL)
            return "Still";
        else if(type == DetectedActivity.TILTING)
            return "Tilting";
        else
            return "";
    }

}
