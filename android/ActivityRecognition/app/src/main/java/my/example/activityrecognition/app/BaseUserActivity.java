package my.example.activityrecognition.app;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class BaseUserActivity extends ActionBarActivity {

    private static final String TAG = "BaseUserActivity";

    protected boolean mIsServiceRunning = false;
    protected HelperClass mHelperInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHelperInstance = HelperClass.getInstance();
    }

    public void startActivityUpdates(){
        Log.d(TAG, "Starting Activity Updates.. Setting Status to true");
        Intent intent = new Intent(getApplicationContext(), BackgroundService.class);
        // without "stop_activity_updates" command, the service doesn't stop'
        startService(intent);
        // also set serviceRunning status to true
       updateServiceStatus(true);
    }

    public void stopActivityUpdates(boolean createNew){
        Log.d(TAG, "Stopping Activity Updates.. Setting Status to false");
        Intent intent = new Intent(getApplicationContext(), BackgroundService.class);

        if(createNew){
            intent.putExtra("stop_activity_updates", true);
            // this isn't stopService() call,, it's the start service call, with stop command in the intent
            // because background service takes care of stop the activity updates, can't be done here
            startService(intent);    
        }
        else{
            stopService(intent);
        }
        
        // also set serviceRunning status to false
        updateServiceStatus(false);
    }

    public void updateServiceStatus(boolean status){
        mIsServiceRunning = status;
        mHelperInstance.setServiceStatus(status);
    }

    public void showToast(String tag, String msg){
        tag = tag.isEmpty() ? TAG : tag;
        Toast.makeText(this, "TAG: " + tag + ", Msg: "+ msg, Toast.LENGTH_SHORT).show();
    }

}
