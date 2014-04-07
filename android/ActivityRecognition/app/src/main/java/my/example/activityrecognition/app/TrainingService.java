package my.example.activityrecognition.app;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.la4j.vector.Vector;
import org.la4j.vector.dense.BasicVector;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TrainingService extends Service {

    private static final String 
        TAG = "TrainingService",
        API_URL = "http://www.ml-training.appspot.com/test";
        // API_URL = "http://127.0.0.1:8080/test"

    private static final int 
        TYPE_SAMPLE = 1, 
        TYPE_MODEL = 2;

    private static final double LAMBDA = 0.0001;

    private RequestQueue mRequestQueue;
    private Sample mSample;
    private Gson mGson = null;
    private HelperClass mHelperInstance;
    private Vector
            mModelVector = new BasicVector(new double[]{0, 0, 0, 0, 0}),
            mGradient = new BasicVector(new double[]{0, 0, 0, 0, 0});

    public TrainingService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();

        /**
         *  create a request queue for http using Volley
         *
         */
        mRequestQueue = Volley.newRequestQueue(this);
        mRequestQueue.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle extras = intent.getExtras();

        mSample = new Sample(
                getApplicationContext(),
                extras.getInt("activity_type"),
                extras.getInt("ringer_mode"),
                extras.getInt("day_of_week"),
                extras.getInt("am_pm"),
                extras.getInt("hour_of_day")
        );
        mHelperInstance = HelperClass.getInstance();

        getOriginalLabel();

        handle();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        cancelPendingRequests(TAG);
        mRequestQueue.stop();
        mRequestQueue = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Training and Sample processing functions
     *
     * */
    public void getOriginalLabel(){
        int hour, minute, row, col, position, label = 0;
        String stringSchedule;
        boolean[] schedule = new boolean[Constants.N_GRIDS];
        Calendar rightNow = Calendar.getInstance();

        // fetching true label for the given time from the saved schedule preferences
        hour = rightNow.get(Calendar.HOUR_OF_DAY);
        minute = rightNow.get(Calendar.MINUTE);

        // calculate position of the current hour and min in the schedule saved
        row = Math.abs(Constants.INIT_HR - hour) * 2 + 1;
        row = (minute / 30) == 0 ? row : row + 1;
        col = mSample.getDayOfWeek();
        position = row * Constants.N_COLS + col;
        stringSchedule = mHelperInstance.getFromPreferences("schedule", "");

        if(stringSchedule == ""){
            Log.d(TAG, "schedule string is empty.. returning");
            return;
        }

        schedule = mHelperInstance.getGson().fromJson(stringSchedule, boolean[].class);

        if (position < Constants.N_GRIDS && schedule[position]) {
            label = 1;
        }

        mSample.setOriginalLabel(label);
    }

    public void handle() {

        if (isConnectedToNetwork()) {
            /**
             * fetch model parameters
             * save latest param in the preferences file ??
             * if there are previous entries stored in DB, send them to th server
             *
             * updateGradient() and updateModel() called in handleResponse
             */
            fetchModel();
//            saveModel();

            syncEntries();
        }
        else{
            /**
             * compute the gradient, and train the model
             *
             */

            updateModel();
        }

        if (isConnectedToNetwork()) {
            /**
             * sync any remaining DB entries, before updating the current one
             * sync data with the server
             *
             */
        } else {
            /**
             * store the computations in DB
             */
        }
    }

    public boolean isConnectedToNetwork() {

        ConnectivityManager connMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }

        return false;
    }

    public void updateGradient() {
        Vector x = mSample.getSampleVector();
        double
                wx = x.innerProduct(mModelVector),
                probability = 1 / (1 + Math.exp(-wx)),
                factor = -((double) mSample.getOriginalLabel() - probability) * probability * (1 - probability);

        mGradient = x.multiply(factor);

    }

    public void saveGradient(){
        mHelperInstance.saveToPreferences("gradient", (new BasicVector(mGradient)).toArray());
    }

    public void updateModel() {
        updateGradient();
        Log.d(TAG, "Update Model called .. ");
        mModelVector = mModelVector.subtract(mGradient.multiply(LAMBDA));

        saveModel();
    }

    public void saveModel() {
        mHelperInstance.saveToPreferences("model", (new BasicVector(mModelVector)).toArray());
    }

    public void fetchModel() {
        String stringModel = HelperClass.getInstance().getFromPreferences("model", "");
        /**
         * TO DO: fetch params from network
         *
         *  fetch model params from network
         *
         */
        fetchDataFromServer();

        // if some preferences are stored, restore it
        if (stringModel != "") {
            mModelVector = new BasicVector(mHelperInstance.getGson().fromJson(stringModel, double[].class));
        }
    }

    public void syncEntries() {

    }

    public void predict(){
        Vector x = mSample.getSampleVector();
        double wx = x.innerProduct(mModelVector);
        double probability = 1 / (1 + Math.exp(-wx));
        int label = 0;

        Log.d(TAG, " Probability.. " + probability);
        if(probability > 0.5){
            label = 1;
        }
        Log.d(TAG, "Probability .. " + probability + ", Predicted Class : " + label + ", Original Class : " + mSample.getOriginalLabel());
        mSample.setPredictedLabel(label);
    }

    /**
     *  Volley functions
     */
    public RequestQueue getRequestQueue(){
        if(mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addRequestToQueue(Request<T> request, String tag){
        request.setTag(TextUtils.isEmpty(tag) ? TAG : tag);

        VolleyLog.d("Adding request to Queue.. %s\n", request.getUrl());

        /* by default Volley cached JSON responses */
        request.setShouldCache(false);
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
                Gson gson = mHelperInstance.getGson();
                params.put("sample", gson.toJson((new BasicVector(mSample.getSampleVector())).toArray()));
                params.put("model", gson.toJson((new BasicVector(mModelVector)).toArray()));
                params.put("gradient", gson.toJson((new BasicVector(mGradient)).toArray()));

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };

        Log.d(TAG, "Making post request..");
        addRequestToQueue(stringPUTRequest, "");

    }

    public void handleDataFromServer(String response){
        /** response from POST request */
        Log.d(TAG, "POST response .. " + response);
    }

    public void handleDataFromServer(JSONObject response){
        /** response from GET request */
        Log.d(TAG, "GET Response .. " + response.toString());
        try {
            JSONArray jsonArray = response.getJSONArray("model");
            double[] modelArray = mHelperInstance.getGson().fromJson(jsonArray.toString(), double[].class);
            Log.d(TAG, " modelArray is " + Arrays.toString(modelArray));
            mModelVector = new BasicVector(modelArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        saveModel();
        /* update done after the model is fetched from server */
        updateModel();
        predict();

        sendDataToServer();
    }

    public void handleError(VolleyError error){
        Log.d(TAG, "Error.. " + error.getMessage());
    }
}
