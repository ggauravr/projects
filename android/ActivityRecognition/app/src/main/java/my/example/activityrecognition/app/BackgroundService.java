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
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.ActivityRecognitionClient;

/**
 * Created by ggauravr on 2/28/14.
 */
public class BackgroundService extends Service
    implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener
{

    private final String TAG = getClass().getSimpleName();
    private BroadcastReceiver mBroadcastReceiver;
    private ActivityRecognitionClient mARClient;
    private PendingIntent mPendingIntent;
    private Handler mServiceHandler;
    private Looper mServiceLooper;
    private DBHelper mDBHelper;

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "message : " + msg.toString());
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        HandlerThread thread = new HandlerThread("BackgroundService");
        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

        connectToDB();
        connectToClient();
     }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "call to start service.. ");

        return START_NOT_STICKY;
    }

    public void connectToDB(){
        mDBHelper = new DBHelper(this);

       SQLiteDatabase db = mDBHelper.getWritableDatabase();

        mDBHelper.fillActivityTable(db);
        mDBHelper.fillPlaceTable(db);

        mDBHelper.close();
     }

    public void connectToClient(){
        int response = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        Intent intent = new Intent(this, HandlerService.class);

        Log.d(TAG, "connecting to google play services.. + response : " + response + ":: " + ConnectionResult.SUCCESS);

        if (response == ConnectionResult.SUCCESS) {
            Log.d(TAG, "successfully connnected to google play sevices");
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
            Log.d(TAG, "Destroying IntentService.. removing activity updates and disconnecting client");
            mARClient.removeActivityUpdates(mPendingIntent);
            mARClient.disconnect();
        }
        
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
        mARClient.requestActivityUpdates(5000, mPendingIntent);

        Log.d(TAG, "onConnected called");

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
