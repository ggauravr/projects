package my.example.activityrecognition.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;

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
        String stringStatus = instance.getFromPreferences( "is_service_running", "false");
        Log.d("HelperClass", "Getting Service Status.. " + stringStatus);

        return Boolean.parseBoolean(stringStatus);

    }

    public void setServiceStatus(boolean status){
        Log.d("HelperClass", "Setting Service Status to.. " + status);
        instance.saveToPreferences( "is_service_running", status);
    }

}
