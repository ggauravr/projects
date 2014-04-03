package my.example.activityrecognition.app;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

/**
 * Created by ggauravr on 4/3/14.
 */
public class HelperClass {

    private static HelperClass instance;
    private Gson mGson;


    public static HelperClass getInstance(){
        if(instance == null){
            instance = new HelperClass();
            instance.mGson = new Gson();
        }

        return instance;
    }

    public void saveToPreferences(Context ctx, String key, Object value){
        SharedPreferences.Editor editor = ctx.getSharedPreferences(Constants.PREFERENCES_FILE, 0).edit();

        editor.putString("model", getGson().toJson(value));
        editor.apply();
    }

    public String getFromPreferences(Context ctx, String key, String defaultValue){

        return ctx.getSharedPreferences(Constants.PREFERENCES_FILE, 0).getString(key, defaultValue);
    }

    public Gson getGson(){
        if(mGson == null){
            mGson = new Gson();
        }

        return mGson;
    }

}
