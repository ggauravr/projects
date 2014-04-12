package my.example.activityrecognition.app;

import android.util.Log;

import com.orm.SugarApp;

public class GlobalContextApp extends SugarApp {

    @Override
    public void onCreate() {
        super.onCreate();

        initHelperInstance();
    }

    protected void initHelperInstance(){
        HelperClass.getInstance(this);
    }

}
