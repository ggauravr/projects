package my.example.testbackgroundservice2.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

    // constants and static members
    private final String TAG = getClass().getSimpleName();

    // UI elements, cached
    private Button mBtnStartService, mBtnStopService;
    
    // instance attributes
    private boolean mIsServiceRunning = false;
    private Context mContext;
    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

                Intent intent = new Intent(getApplicationContext(), my.example.testbackgroundservice2.app.BackgroundService.class);

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

                Intent intent = new Intent(getApplicationContext(), my.example.testbackgroundservice2.app.BackgroundService.class);

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

        mPreferences = getSharedPreferences(TAG, MODE_PRIVATE);
        mIsServiceRunning = mPreferences.getBoolean("IS_RUNNING", false);

        showToast("Getting Preferences: " + mIsServiceRunning);

        return mIsServiceRunning;
    }

    /**
     * changes the running status of the background service on start and stop
     * 
     * @param mIsServiceRunning - status to which it is to be set
     */
    public void setIsServiceRunning(boolean mIsServiceRunning) {

        SharedPreferences.Editor editor = mPreferences.edit();

        this.mIsServiceRunning = mIsServiceRunning;
        editor.putBoolean("IS_RUNNING", mIsServiceRunning);
        
        // commit saves synchronously, apply does it asynchronously
        editor.apply();

        showToast("Setting Preferences: " + mIsServiceRunning);
    }
}
