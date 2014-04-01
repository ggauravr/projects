package my.example.testnetworking.app;

import android.app.DownloadManager;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ggauravr on 3/31/14.
 */
public class NetworkHandler extends Service {

    private static final String TAG = "NetworkHandler";
    private RequestQueue mRequestQueue;
    private static final String API_URL = "http://www.ml-training.appspot.com/test";

    @Override
    public void onCreate() {
        super.onCreate();

        mRequestQueue = Volley.newRequestQueue(this);
        mRequestQueue.start();

//        fetchDataFromServer();
        sendDataToServer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        cancelPendingRequests(TAG);
        mRequestQueue.stop();
        mRequestQueue = null;
    }

    public RequestQueue getRequestQueue(){
        if(mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addRequestToQueue(Request<T> request, String tag){
        request.setTag(TextUtils.isEmpty(tag) ? TAG : tag);

        VolleyLog.d("Adding request to Queue.. %s\n", request.getUrl());

        getRequestQueue().add(request);
    }

    public void cancelPendingRequests(Object tag){
        if(mRequestQueue != null){
            mRequestQueue.cancelAll(tag);
        }

    }

    public void fetchDataFromServer(){
        JsonObjectRequest jsonGETRequest;

        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                handleDataFromServer(response);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handleError(error);
            }
        };

        jsonGETRequest = new JsonObjectRequest(API_URL, null, responseListener, errorListener);

        addRequestToQueue(jsonGETRequest, "");

    }

    public void sendDataToServer(){
        JsonObjectRequest jsonPUTRequest;
        StringRequest stringPUTRequest = new StringRequest(Request.Method.POST,API_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                handleDataFromServer(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handleError(error);
            }
        }) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("cmd", "TestPostCmd");
                params.put("msg", "TestPostMsg");

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };

        addRequestToQueue(stringPUTRequest, "");

    }

    public void handleDataFromServer(String response){
        Log.d(TAG, "Response from the server received .. " + response);
//        VolleyLog.v(response.toString());
    }

    public void handleDataFromServer(JSONObject response){
        Log.d(TAG, "Response from the server received .. " + response.toString());
//        VolleyLog.v(response.toString());
    }

    public void handleError(VolleyError error){
       Log.d(TAG, "Error.. " + error.getMessage());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
