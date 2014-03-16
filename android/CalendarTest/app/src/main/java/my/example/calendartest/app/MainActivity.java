package my.example.calendartest.app;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends Activity implements ActionMode.Callback {

    public static final String PREFS_FILE = "UserPreferences";
    public static final int INIT_HR = 7;
    public static final int N_ROWS = 27;
    public static final int N_COLS = 8;
    public static final int N_GRIDS = N_ROWS * N_COLS;

    public static String[] content = new String[N_GRIDS];
    public static boolean[] selected = new boolean[N_GRIDS];
    public static boolean[] tempSelected = new boolean[N_GRIDS];

    private ActionMode mActionMode;
    private GridView mGrid;

    static {
        String hour, min;
        int row;
        boolean isEven = true;

        for(int i=0; i < N_GRIDS; ++i){
            hour = "";
            min = "";
            row = (int) (i/N_COLS);

            if(i%N_COLS == 0){
                hour = String.format("%02d", INIT_HR + (int)(row/2));
                min = isEven ? "00" : "30";
                isEven = !isEven;
            }

            content[i] = hour +" "+min;
        }

//        initialize the temporary selections to the actual preferences
        System.arraycopy(selected, 0, tempSelected, 0, selected.length);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        * restore calendar preferences
        * */
        SharedPreferences settings = getSharedPreferences(PREFS_FILE, 0);
        String schedule = settings.getString("schedule", "");

//        if some preferences are stored, restore it
        if(schedule != ""){
            Gson gson = new Gson();
            selected = gson.fromJson(schedule, boolean[].class);
            System.arraycopy(selected, 0, tempSelected, 0, selected.length);
        }

        Log.d("MainActivity", "In onCreate: selected = " + schedule);

        mGrid = (GridView) findViewById(R.id.gridView);

        mGrid.setAdapter(new CalendarAdapter(this, R.layout.grid_cell, R.id.textView, content));

        mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                if(mActionMode == null){
                    mActionMode = MainActivity.this.startActionMode(MainActivity.this);
                }

                if(tempSelected[position] == true){
                    view.setBackgroundColor(Color.WHITE);
                }
                else{
                    view.setBackgroundColor(Color.RED);
                }

                tempSelected[position] = !tempSelected[position];

//                Log.d("MainActivity", Arrays.toString(tempSelected));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
    }


    /*
    * overriding actionmode.callback methods
    *
    * */

     @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
         MenuInflater menuInflater = actionMode.getMenuInflater();
         menuInflater.inflate(R.menu.context_menu, menu);

        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {

        switch(menuItem.getItemId()){
            case R.id.action_save:
                saveCalendar();
                break;

            case R.id.action_ignore:
                resetCalendar();
                break;

            default:
                return false;
        }

        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        mActionMode = null;
    }

    /*
    * calendar action methods
    *
    * */

    public void saveCalendar(){
        SharedPreferences settings = getSharedPreferences(PREFS_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        String schedule;
        Gson gson = new Gson();

        System.arraycopy(tempSelected, 0, selected, 0, tempSelected.length);
        schedule = gson.toJson(selected);

        editor.putString("schedule", schedule);
        editor.commit();

        Toast.makeText(this, "Calendar successfully saved " + schedule, Toast.LENGTH_SHORT).show();
    }

    public void resetCalendar(){
        System.arraycopy(selected, 0, tempSelected, 0, selected.length);
        mGrid.invalidateViews();

        Toast.makeText(this, "Changed ignored !", Toast.LENGTH_SHORT).show();
    }

    public void clearCalendar(){
        Arrays.fill(selected, false);
        mGrid.invalidateViews();

        Toast.makeText(this, "Cleared Calendar !", Toast.LENGTH_SHORT).show();
    }

 }
