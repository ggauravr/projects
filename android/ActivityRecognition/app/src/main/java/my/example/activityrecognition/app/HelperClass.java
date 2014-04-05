package my.example.activityrecognition.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

/**
 * Created by ggauravr on 4/3/14.
 */
public class HelperClass {

    private static HelperClass instance;
    private Gson mGson;
    private static Context mContext;
    private SharedPreferences mPreference;

    public static HelperClass getInstance(){

        return getInstance(mContext);

    }

    public static HelperClass getInstance(Context ctx){
        mContext = ctx;
        if(instance == null){
            instance = new HelperClass();

            instance.mGson = new Gson();
            instance.mPreference = PreferenceManager.getDefaultSharedPreferences(ctx);
        }

        return instance;
    }

    public void saveToPreferences(String key, Object value){
        SharedPreferences.Editor editor = mPreference.edit();

        editor.putString(key, getGson().toJson(value));
        editor.apply();
    }

    public String getFromPreferences(String key, String defaultValue){
        return mPreference.getString(key, defaultValue);
    }

    public Gson getGson(){
        if(mGson == null){
            mGson = new Gson();
        }

        return mGson;
    }

    public boolean getServiceStatus() {

        return Boolean.parseBoolean(instance.getFromPreferences( "is_service_running", "false"));

    }

    public void setServiceStatus(boolean status){
        instance.saveToPreferences( "is_service_running", String.valueOf(status));
//        showToast("Setting Preferences: " + String.valueOf(status));
    }

}
