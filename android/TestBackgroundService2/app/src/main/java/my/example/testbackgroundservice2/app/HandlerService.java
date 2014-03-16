package my.example.testbackgroundservice2.app;

import android.app.IntentService;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.Calendar;

/**
 * Created by ggauravr on 3/1/14.
 */
public class HandlerService extends IntentService {

    private final String TAG = getClass().getSimpleName();
    private AudioManager mAudioManager;

    public HandlerService(){
        super("HandlerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        Calendar now = Calendar.getInstance();

        if(ActivityRecognitionResult.hasResult(intent)){
            DBHelper dbHelper = new DBHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            DetectedActivity mostProbableActivity = result.getMostProbableActivity();

            int confidence = mostProbableActivity.getConfidence();
            String action = getType(mostProbableActivity.getType());

            String ringerMode = getRingerModeAsString(mAudioManager.getRingerMode());
            new Feature(mostProbableActivity.getType(), confidence, now.get(Calendar.AM_PM), now.get(Calendar.HOUR), now.get(Calendar.DAY_OF_WEEK)).save(db);
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
