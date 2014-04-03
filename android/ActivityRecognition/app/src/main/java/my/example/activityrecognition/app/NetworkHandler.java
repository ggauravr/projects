package my.example.activityrecognition.app;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;

import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by ggauravr on 4/2/14.
 */
public class NetworkHandler extends Service {

    private static final String API_URL = "http://www.ml-training.appspot.com/test";
    private static final String TAG = "NetworkHandler";

    private static NetworkHandler instance;
    private static RequestQueue mRequestQueue;

    public void createHTTPObject(){
        if(mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(this);
            mRequestQueue.start();
        }
    }

    public void destroyHTTPObject(){
        if(mRequestQueue != null){
            mRequestQueue.cancelAll(TAG);
        }

        mRequestQueue.stop();
        mRequestQueue = null;
    }



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
