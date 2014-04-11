package my.example.activityrecognition.app;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;

import java.util.Iterator;
import java.util.List;

/**
 * Created by ggauravr on 4/3/14.
 */
public class HelperClass {

    private static HelperClass instance;
    private Gson mGson;
    private static Context mContext;
    private SharedPreferences mPreference;

    private static final String BG_SERVICE_NAME = "my.example.activityrecognition.app.BackgroundService";

    public static HelperClass getInstance(){

        return getInstance(mContext);

    }

    public static HelperClass getInstance(Context ctx){
        // initialized in the globalContextApp class, during application initialization
        // so any other activity or service can just call getInstance(), without the context
        // and still get the same instance..
        mContext = ctx;
        if(instance == null){
            instance = new HelperClass();

            Log.i("HelperClass", "Initializing HelperClass Instance");

            instance.mGson = new Gson();
            // shared preferences is now used in a global context, so accessing this from anywhere
            // should give the same results
            instance.mPreference = PreferenceManager.getDefaultSharedPreferences(ctx);
        }

        return instance;
    }

    public void saveToPreferences(int keyID, Object value){
        SharedPreferences.Editor editor = mPreference.edit();

        editor.putString(mContext.getString(keyID), getGson().toJson(value));
        editor.apply();
    }

    public String getFromPreferences(int keyID, String defaultValue){
        return mPreference.getString(mContext.getString(keyID), defaultValue);
    }

    public Gson getGson(){
        if(mGson == null){
            mGson = new Gson();
        }

        return mGson;
    }

    public boolean getServiceStatus(){
        return getServiceStatus(BG_SERVICE_NAME);
    }

    private boolean getServiceStatus(String serviceName){
        boolean serviceRunning = false;
        ActivityManager am = (ActivityManager) mContext.getSystemService("activity");
        List<ActivityManager.RunningServiceInfo> l = am.getRunningServices(60);
        Iterator<ActivityManager.RunningServiceInfo> i = l.iterator();

        while (i.hasNext()) {
            ActivityManager.RunningServiceInfo runningServiceInfo = (ActivityManager.RunningServiceInfo) i.next();
            if(runningServiceInfo.service.getClassName().equals(serviceName)){
                serviceRunning = true;
            }
        }
        return serviceRunning;

        /*BackgroundService bgServiceInstance = BackgroundService.getInstance();
        boolean status = bgServiceInstance != null && !Boolean.parseBoolean(getFromPreferences(R.string.key_service_status, "false"));

        Log.d("HelperClass", "Background Service Status .. " + String.valueOf(status));

        return status;*/
    }

    public void setServiceStatus(boolean status){
        instance.saveToPreferences(R.string.key_service_status, status);
    }

}
