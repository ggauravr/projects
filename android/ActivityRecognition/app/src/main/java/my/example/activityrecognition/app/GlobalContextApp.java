package my.example.activityrecognition.app;

import android.util.Log;

import com.orm.SugarApp;

/**
 * Created by ggauravr on 4/5/14.
 */
public class GlobalContextApp extends SugarApp {

    @Override
    public void onCreate() {
        super.onCreate();

        initHelperInstance();
    }

    protected void initHelperInstance(){
        Log.i("GlobalApplicationContext", "Initializing HelperClass Instance");
        HelperClass.getInstance(this);
    }

}
