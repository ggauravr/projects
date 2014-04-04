package my.example.activityrecognition.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class CollectorActivity extends ActionBarActivity {

    private final String TAG = getClass().getSimpleName();

    private Button 
        mBtnStartService, 
        mBtnStopService;
    
    private boolean mIsServiceRunning = false;
    private Context mContext;
    private HelperClass mHelperInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collector);


        mHelperInstance = HelperClass.getInstance();
        getServiceStatus();

        mBtnStartService = (Button) findViewById(R.id.btn_start);
        mBtnStopService = (Button) findViewById(R.id.btn_stop);
        mContext = this;

        registerEventCallbacks();

    }

    public void registerEventCallbacks() {

        mBtnStartService.setOnClickListener(new View.OnClickListener() {
            /**
             * if service already running
             *     return silently
             * else
             *     set the status in preferences and start the service
             *     
             * @param view
             */
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), BackgroundService.class);

                if (mIsServiceRunning) {
                    showToast("Service already running !");
                    return;
                }
                
                setIsServiceRunning(true);
                startService(intent);
            }
        });

        mBtnStopService.setOnClickListener(new View.OnClickListener() {
            /**
             * if service not running,
             *     return silently
             * else
             *     set the status to false and stop the service
             * 
             * @param view [description]
             */
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), BackgroundService.class);

                if (!mIsServiceRunning) {
                    showToast("Activity not running !");
                    return;
                }

                setIsServiceRunning(false);
                stopService(intent);
            }
        });

    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * gets the running status of background service from shared preferences
     * if not set, sets it to false
     * 
     * @return mIsServiceRunning - running status of the background service
     */
    public boolean getServiceStatus() {

        mIsServiceRunning = Boolean.parseBoolean(mHelperInstance.getFromPreferences( "IS_RUNNING", "false"));

        showToast("Getting Preferences: " + mIsServiceRunning);

        return mIsServiceRunning;
    }

    /**
     * changes the running status of the background service on start and stop
     * 
     * @param mIsServiceRunning - status to which it is to be set
     */
    public void setIsServiceRunning(boolean mIsServiceRunning) {

        this.mIsServiceRunning = mIsServiceRunning;
        mHelperInstance.saveToPreferences( "IS_RUNNING", String.valueOf(mIsServiceRunning));

        showToast("Setting Preferences: " + mIsServiceRunning);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.action_reschedule:
                displayCalendar();
                break;

            default:
                return false;
        }

        return false;

    }

    @Override
    public void onSupportActionModeFinished(android.support.v7.view.ActionMode mode) {
        Toast.makeText(this, "ActionMode Finished !", Toast.LENGTH_SHORT).show();
        super.onSupportActionModeFinished(mode);
    }

    public void displayCalendar(){
        Intent intent = new Intent(this, CalendarActivity.class);
        intent.putExtra("reschedule", true);

        startActivity(intent);
        finish();
    }
}