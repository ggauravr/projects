package my.example.activityrecognition.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Date;

/**
 *  @author : Gaurav Ramesh
 *  @email : gggauravr@gmail.com         
 * 
 *  @class : CalendarActivity
 *  @description: displays a week calendar for the user to store his class schedule
 * 
 */

public class CalendarActivity extends BaseUserActivity {

    private final String TAG = getClass().getSimpleName();

    public static String[]
        content = new String[Constants.N_GRIDS],
        days = new String[] {" ", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    
    public static boolean[]
        // selected : holds the saved calendar
        // tempSelected : holds the temporary changes to the calendar
        selected = new boolean[Constants.N_GRIDS],
        tempSelected = new boolean[Constants.N_GRIDS];
    
    private ActionBar mActionBar;
    private GridView mGrid, mHeaderGrid;

    static {
        String hour, min;
        int row, temp;
        boolean isEven = false;

        /* initialize days of the week */
        for(int i=0; i < Constants.N_COLS; ++i){
            content[i] = days[i];
        }

        // move ahead by one row, as the first row covers up the days
        for(int i=Constants.N_COLS; i < Constants.N_GRIDS; ++i){
            hour = "";
            min = "";
            row = (int) (i/Constants.N_COLS);
            temp = isEven ? (row-1)/2 : row/2;

            if(i%Constants.N_COLS == 0){
                hour = String.format("%02d", Constants.INIT_HR + temp);
                min = isEven ? "30" : "00";
                isEven = !isEven;
            }

            content[i] = hour +" "+min;
        }

        System.arraycopy(selected, 0, tempSelected, 0, selected.length);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        Bundle extras = getIntent().getExtras();
        String schedule;

        mHelperInstance = HelperClass.getInstance();

        if(mHelperInstance.getFromPreferences(R.string.key_device_id, "") == ""){
            // this ID will be unique for each user
            mHelperInstance.saveToPreferences(R.string.key_device_id, Settings.Secure.ANDROID_ID);
        }

        // restore calendar preferences
        schedule = mHelperInstance.getFromPreferences(R.string.key_schedule, "");

        // if some preferences are stored, restore it
        if(schedule != ""){
            selected = mHelperInstance.getGson().fromJson(schedule, boolean[].class);
            System.arraycopy(selected, 0, tempSelected, 0, selected.length);

            /**
             *  if the schedule is not null and this activity is triggered on startup, start the Collector Activity
             *  else, if user has manually chosen to enter Calendar Activity, continue
             * 
             */
            if(extras == null){
                // move on to the services/collector activity and close/finish the current calendar activity
                startCollectorActivity();
            }
        }
        else{
            // if not scheduled, stop any running activity updates
            // create a new background service to terminate existing udpates
            stopActivityUpdates(!mHelperInstance.getServiceStatus());
        }

        mGrid = (GridView) findViewById(R.id.gridView);
        mGrid.setAdapter(new CalendarAdapter(this, R.layout.grid_cell, R.id.textView, content));
        mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                if(mActionBar == null){
                    mActionBar = getSupportActionBar();
                    mActionBar.show();
                }

                // do nothing for first row and first column, which is the margin
                if(position%8 == 0 || position/8 == 0){
                    return;
                }

                if(tempSelected[position] == true){
                    view.setBackgroundColor(Color.WHITE);
                }
                else{
                    view.setBackgroundColor(Color.RED);
                }

                tempSelected[position] = !tempSelected[position];
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.calendar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.action_commit:
                saveCalendar();
                break;

            case R.id.action_ignore:
                resetCalendar();
                break;

            case R.id.action_clear:
                clearCalendar();
                break;

            case R.id.action_done:
                saveCalendar();
                startCollectorActivity();
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

    private void startCollectorActivity(){
        Intent intent = new Intent(this, CollectorActivity.class);
        startActivity(intent);
        finish();
    }
    
    public void saveCalendar(){
        System.arraycopy(tempSelected, 0, selected, 0, tempSelected.length);
        mHelperInstance.saveToPreferences(R.string.key_schedule, selected);

        Toast.makeText(this, R.string.msg_calendar_save, Toast.LENGTH_SHORT).show();
    }

    public void resetCalendar(){
        System.arraycopy(selected, 0, tempSelected, 0, selected.length);
        mGrid.invalidateViews();

        Toast.makeText(this, R.string.msg_calendar_reset, Toast.LENGTH_SHORT).show();
    }

    public void clearCalendar(){
        Arrays.fill(selected, false);
        Arrays.fill(tempSelected, false);
        
        mGrid.invalidateViews();

        Toast.makeText(this, R.string.msg_calendar_clear, Toast.LENGTH_SHORT).show();
    }

}