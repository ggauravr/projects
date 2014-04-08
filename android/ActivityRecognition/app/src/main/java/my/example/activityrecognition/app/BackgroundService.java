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
 * Created by ggauravr on 2/28/14.
 */
public class BackgroundService extends Service
    implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener
{

    private final String TAG = getClass().getSimpleName();
    private final int SAMPLE_FREQUENCY = 30000;

    private BroadcastReceiver mBroadcastReceiver;
    private ActivityRecognitionClient mARClient;
    private PendingIntent mPendingIntent;
    private Handler mServiceHandler;
    private Looper mServiceLooper;
    private HelperClass mHelperInstance;
    private boolean mCmd;

    // if sSelf is null, the background service isn't running
    private static BackgroundService sSelf = null;

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "message : " + msg.toString());
        }
    }

    public static BackgroundService getInstance(){
        return sSelf;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        HandlerThread thread = new HandlerThread("BackgroundService");
        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

        mHelperInstance = HelperClass.getInstance(getApplicationContext());
        sSelf = this;
        mHelperInstance.saveToPreferences("is_service_running", true);

        // connectToDB();
        connectToClient();
     }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mCmd = intent.getBooleanExtra("stop_activity_updates", false);
        Log.d(TAG, "Command : " + String.valueOf(mCmd));

        return START_REDELIVER_INTENT;
    }

    /*public void connectToDB(){
        mDBHelper = new DBHelper(this);

        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        mDBHelper.fillActivityTable(db);
        mDBHelper.fillPlaceTable(db);

        mDBHelper.close();
     }*/

    public void connectToClient(){
        int response = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        Intent intent = new Intent(this, HandlerService.class);

        // Log.d(TAG, "connecting to google play services.. + response : " + response + ":: " + ConnectionResult.SUCCESS);

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

        if(mARClient!=null){
            stopActivityUpdates();
        }

        sSelf = null;
        mHelperInstance.saveToPreferences("is_service_running", false);
        mServiceLooper.quit();
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
        mARClient.requestActivityUpdates(SAMPLE_FREQUENCY, mPendingIntent);

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
