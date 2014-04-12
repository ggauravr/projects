package my.example.activityrecognition.app;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.ActivityRecognitionClient;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 *  @author : Gaurav Ramesh
 *  @email : gggauravr@gmail.com         
 * 
 *  @class : BackgroundService
 *  @description: main service which runs on a separated thread from the main UI thread
 *                      manages connecting to Google Play Services and to the ActivityRecognition API
 *                      registers/unregisters for activity updates
 * 
 */

public class BackgroundService extends Service
    implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener
{

    private final String TAG = getClass().getSimpleName();

    private ActivityRecognitionClient mARClient;
    private PendingIntent mPendingIntent;
    private Handler mServiceHandler;
    private Looper mServiceLooper;
    private HelperClass mHelperInstance;
    private boolean mCmd;

    private boolean mIsConnected = false;

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        HandlerThread thread = new HandlerThread("BackgroundService");
        thread.start();

        mIsConnected = false;
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

        mHelperInstance = HelperClass.getInstance(getApplicationContext());
        
        mHelperInstance.saveToPreferences(R.string.key_service_status, true);
        
        connectToClient();
     }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mCmd = intent.getBooleanExtra("stop_activity_updates", false);

        // VERY IMPORTANT : DO NOT MESS
        if(mIsConnected){
            stopSelf();
        }

        return START_REDELIVER_INTENT;
    }

    public void connectToClient(){
        int response = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        Intent intent = new Intent(this, HandlerService.class);

        if (response == ConnectionResult.SUCCESS) {
            mARClient = new ActivityRecognitionClient(this, this, this);
            mARClient.connect();
            mPendingIntent = PendingIntent.getService(this, 1000, intent, PendingIntent.FLAG_UPDATE_CURRENT );

        } else {
            Log.d(TAG, "can't connect to Google Play Services");
            
            // TODO : Implement the Error Dialog to install Google Play Service
        }
    }

    @Override
    public void onDestroy() {

        if(mARClient != null){
            stopActivityUpdates();
        }

        Intent intent = new Intent(getApplicationContext(), TrainingService.class);
        stopService(intent);

        mHelperInstance.saveToPreferences(R.string.key_service_status, false);
        mServiceLooper.quit();

        // necessary for sync
        mHelperInstance.saveToPreferences(R.string.key_activity_count, 0);

        super.onDestroy();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public BackgroundService(){
        super();
    }

    @Override
    public void onConnected(Bundle bundle) {
        int sampleFrequency = Integer.parseInt(mHelperInstance.getFromPreferences(R.string.key_sample_frequency, Integer.toString(Constants.SAMPLE_FREQUENCY)));
        mIsConnected = true;
        mARClient.requestActivityUpdates(sampleFrequency, mPendingIntent);
        if(mCmd){
            stopSelf();
        }
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void stopActivityUpdates(){
        Log.d(TAG, "Destroying IntentService.. removing activity updates and disconnecting client");
        mARClient.removeActivityUpdates(mPendingIntent);
        mARClient.disconnect();
    }
}