package my.example.activityrecognition.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CollectorActivity extends BaseUserActivity {

    private final String TAG = getClass().getSimpleName();

    private Button mBtnService;
    private TextView mTxtServiceStatus;
    private TextView mTxtSampleDetails;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collector);

        mHelperInstance = HelperClass.getInstance();

        mBtnService = (Button) findViewById(R.id.btn_service);
        mTxtServiceStatus = (TextView) findViewById(R.id.txt_service_status);
        mTxtSampleDetails = (TextView) findViewById(R.id.txt_sample);

        mContext = this;

        registerEventCallbacks();

    }

    @Override
    protected void onStart() {
        super.onStart();

       setMessages();
    }

    public void registerEventCallbacks() {

        LocalBroadcastManager.getInstance(mContext).registerReceiver(activityBroadcastReceiver, new IntentFilter(getString(R.string.msg_activity_broadcast)));

        mBtnService.setOnClickListener(new View.OnClickListener() {
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

                if (mHelperInstance.getServiceStatus()) {
                    stopActivityUpdates(true);
                    setMessages(false);
                    Toast.makeText(mContext, R.string.msg_service_kill, Toast.LENGTH_SHORT).show();
                }
                else{
                    startActivityUpdates();
                    setMessages(true);
                    Toast.makeText(mContext, R.string.msg_service_start, Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private BroadcastReceiver activityBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String
                    action = intent.getStringExtra("activity"),
                    ringerMode = intent.getStringExtra("ringer_mode"),
                    dayOfWeek = intent.getStringExtra("day_of_week"),
                    fuzzyTime = intent.getStringExtra("am_pm");
            int hourOfDay = intent.getIntExtra("hour_of_day", 0);
            int confidence = intent.getIntExtra("confidence", 0);

            /**
             * update the UI with the sample details..
             *
             * */
            String msg = "Action : " + action + "\n" +
                    "Confidence : " + confidence + "\n" +
                    "Ringer Mode : " + ringerMode + "\n" +
                    "Day/Hour : " + dayOfWeek + ", " + hourOfDay + " " + fuzzyTime;

            mTxtSampleDetails.setText(msg);
         }
    };

    public void setMessages(){
        setMessages(mHelperInstance.getServiceStatus());
    }

    public void setMessages(boolean status){
        if (status) {
            mTxtServiceStatus.setText(R.string.txt_service_running);
            mBtnService.setText(R.string.txt_service_btn_stop);
        }
        else{
            mTxtServiceStatus.setText(R.string.txt_service_not_running);
            mBtnService.setText(R.string.txt_service_btn_start);
        }
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