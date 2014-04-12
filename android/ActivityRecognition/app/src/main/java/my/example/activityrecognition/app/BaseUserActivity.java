package my.example.activityrecognition.app;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 *  @author : Gaurav Ramesh
 *  @email : gggauravr@gmail.com         
 * 
 *  @class : BaseUserActivity
 *  @description: implements some functions, common to multiple activities,
 *                      like starting and stopping the background service
 * 
 */

public class BaseUserActivity extends ActionBarActivity {

    private final String TAG = getClass().getSimpleName();

    protected HelperClass mHelperInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHelperInstance = HelperClass.getInstance();
    }

    public void startActivityUpdates(){
        Log.d(TAG, "Starting activity updates");
        Intent intent = new Intent(getApplicationContext(), BackgroundService.class);
        // without "stop_activity_updates" command, the service doesn't stop'
        startService(intent);
    }

    public void stopActivityUpdates(boolean createNew){
        Log.d(TAG, "Stopping activity updates");
        Intent intent = new Intent(getApplicationContext(), BackgroundService.class);

        if(createNew){
            intent.putExtra("stop_activity_updates", true);
            // this isn't stopService() call,, it's the start service call, with stop command in the intent
            // because background service takes care of stop the activity updates, can't be done here
            startService(intent);    
        }
        else{
            Log.d(TAG, "Stopping BackgroundService");
            stopService(intent);
        }
    }

}
