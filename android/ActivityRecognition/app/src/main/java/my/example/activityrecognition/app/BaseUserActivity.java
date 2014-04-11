package my.example.activityrecognition.app;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class BaseUserActivity extends ActionBarActivity {

    private final String TAG = getClass().getSimpleName();

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
    }

    public void stopActivityUpdates(boolean createNew){
        Log.d(TAG, "Stopping Activity Updates.. Setting Status to false");
        Intent intent = new Intent(getApplicationContext(), BackgroundService.class);


        if(createNew){
            Log.d(TAG, "Creating new BG process to stop it.. ");
            intent.putExtra("stop_activity_updates", true);
            // this isn't stopService() call,, it's the start service call, with stop command in the intent
            // because background service takes care of stop the activity updates, can't be done here
            startService(intent);    
        }
        else{
            Log.d(TAG, "Calling stop service.. bg service ");
            stopService(intent);
        }
    }

    public void showToast(String tag, String msg){
        tag = tag.isEmpty() ? TAG : tag;
        Toast.makeText(this, "TAG: " + tag + ", Msg: "+ msg, Toast.LENGTH_SHORT).show();
    }

}
