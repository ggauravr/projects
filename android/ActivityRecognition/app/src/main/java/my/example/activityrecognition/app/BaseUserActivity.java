package my.example.activityrecognition.app;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class BaseUserActivity extends ActionBarActivity {

    protected boolean mIsServiceRunning = true;
    protected HelperClass mHelperInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHelperInstance = HelperClass.getInstance(this);

//        setContentView(R.layout.activity_base_user);
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.base_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    public void startActivityUpdates(){
        Intent intent = new Intent(getApplicationContext(), BackgroundService.class);
        // without "stop_activity_updates" command, the service doesn't stop'
        startService(intent);
        // also set serviceRunning status to false
       updateServiceStatus(true);
    }

    public void stopActivityUpdates(){
        Intent intent = new Intent(getApplicationContext(), BackgroundService.class);
        intent.putExtra("stop_activity_updates", true);
        // this isn't stopService() call,, it's the start service call, with stop command in the intent
        // because background service takes care of stop the activity updates, can't be done here
        startService(intent);
        // also set serviceRunning status to false
        updateServiceStatus(false);
    }

    public void updateServiceStatus(boolean status){
        mIsServiceRunning = status;
        mHelperInstance.setServiceStatus(status);
    }

}
