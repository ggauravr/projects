package my.example.activityrecognition.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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

    private static final int
            TXT_SERVICE_STOP_BTN = R.string.txt_service_stop_btn,
            TXT_SERVICE_START_BTN = R.string.txt_service_start_btn,
            TXT_SERVICE_RUNNING = R.string.txt_service_running,
            TXT_SERVICE_NOT_RUNNING = R.string.txt_service_not_running;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collector);

        mHelperInstance = HelperClass.getInstance();

        mBtnService = (Button) findViewById(R.id.btn_service);
        mTxtServiceStatus = (TextView) findViewById(R.id.txt_service_status);

        mContext = this;

        registerEventCallbacks();

    }

    @Override
    protected void onStart() {
        super.onStart();

       setMessages();
    }

    public void registerEventCallbacks() {

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
                    stopActivityUpdates(!mHelperInstance.getServiceStatus());
                    setMessages(false);
                    Toast.makeText(mContext, "Service killed. Stopping data collection.", Toast.LENGTH_SHORT).show();
                }
                else{
                    startActivityUpdates();
                    setMessages(true);
                    Toast.makeText(mContext, "Service started. Starting data collection.", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public void setMessages(){
        setMessages(mHelperInstance.getServiceStatus());
    }

    public void setMessages(boolean status){
        if (status) {
            mTxtServiceStatus.setText(TXT_SERVICE_RUNNING);
            mBtnService.setText(TXT_SERVICE_STOP_BTN);
        }
        else{
            mTxtServiceStatus.setText(TXT_SERVICE_NOT_RUNNING);
            mBtnService.setText(TXT_SERVICE_START_BTN);
        }
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
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