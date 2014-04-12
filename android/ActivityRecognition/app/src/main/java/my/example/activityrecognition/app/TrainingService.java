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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;
import org.la4j.vector.Vector;
import org.la4j.vector.dense.BasicVector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author : Gaurav Ramesh
 * @email : gggauravr@gmail.com
 * 
 * @class : TrainingService
 * @description: does the processing of the sample, like learning the model,
 * sending and receiving updated models and samples
 */

public class TrainingService extends Service {

    private static final String
            TAG = "TrainingService",
            DEFAULT_API_URL = "http://www.ml-training-backup.appspot.com/activity_api";

    private static final double LAMBDA = 0.001;
    private static final int N_ACTIVITIES_PER_SAMPLE = 5;

    private RequestQueue mRequestQueue;
    private Sample mSample;
    private HelperClass mHelperInstance;
    private Vector
            mModelVector = new BasicVector(new double[Constants.N_DIMENSIONS]),
            mGradient = new BasicVector(new double[Constants.N_DIMENSIONS]);
    private List<String> mSamples = new ArrayList<String>();

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

        mHelperInstance = HelperClass.getInstance();

        mSample = new Sample(
                extras.getInt("activity_type"),
                extras.getInt("ringer_mode"),
                extras.getInt("day_of_week"),
                extras.getInt("am_pm"),
                extras.getInt("hour_of_day")
        );

        if (getActivityCount() == 0) {
            clearStoredSample();
        }

        getOriginalLabelForUserActivity();
        updateSample();

        if (getActivityCount() == N_ACTIVITIES_PER_SAMPLE) {
            // handle the sample only when n activities are collected
            setOriginalLabel();
            handle();
        }
        else{
            stopSelf();
        }

        return START_NOT_STICKY;
    }

    public int getSampleCount() {
        int count = Integer.parseInt(mHelperInstance.getFromPreferences(R.string.key_sample_count, "1"));
        return count;
    }

    public void updateSampleCount() {
        int count = getSampleCount();

        count += 1;
        mHelperInstance.saveToPreferences(R.string.key_activity_count, count);
    }

    public int getActivityCount() {
        int count = Integer.parseInt(mHelperInstance.getFromPreferences(R.string.key_activity_count, "0"));
        return count;
    }

    public void updateActivityCount() {
        int count = Integer.parseInt(mHelperInstance.getFromPreferences(R.string.key_activity_count, "0"));

        // TODO : make this number configurable from server..
        // check commands and values below.. for API and LAMBDA updates, do this in the same way
        if (count % N_ACTIVITIES_PER_SAMPLE == 0) {
            count = 0;
        }

        count += 1;
        mHelperInstance.saveToPreferences(R.string.key_activity_count, count);
    }

    public void clearStoredSample() {
        mHelperInstance.saveToPreferences(R.string.key_sample, new double[0]);
    }

    public void addSample() {
        double[] sampleArray = getStoredSample();
        ExpandedSample expandedSample = new ExpandedSample(getApplicationContext(), sampleArray);

        expandedSample.save();
        Log.d(TAG, "Sample saved in DB: " + expandedSample.getId());
        mHelperInstance.saveToPreferences(R.string.key_sample, new double[0]);
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

    public void updateSample() {
        double[] sampleArray = getStoredSample();

        Vector
                currentSample = mSample.getSampleVector();

        Vector storedSample = new BasicVector(sampleArray),
                updatedSample = currentSample.add(storedSample);

        mHelperInstance.saveToPreferences(R.string.key_sample, ((BasicVector) updatedSample).toArray());
        Log.d(TAG, "Current sample : " + Arrays.toString(((BasicVector) currentSample).toArray()));
        Log.d(TAG, "Saved Sample : " + mHelperInstance.getFromPreferences(R.string.key_sample, "[]"));

        updateActivityCount();

    }

    public void setOriginalLabel() {
        double[] sampleArray = getStoredSample();
        int length = sampleArray.length;

        // original label is at last but one position
        Log.d(TAG, "Original Label " + sampleArray[length - 1]);
        if (sampleArray[length - 1] >= (N_ACTIVITIES_PER_SAMPLE/2)+1) {
            // if majority, set it to 1, else 0
            sampleArray[length - 1] = 1;
        }
        else{
            sampleArray[length - 1] = 0;
        }
        mHelperInstance.saveToPreferences(R.string.key_sample, sampleArray);
    }

    public int getOriginalLabel() {
        double[] sampleArray = getStoredSample();
        int length = sampleArray.length;

        // last element in the array is the original label
        return (int) sampleArray[length - 1];
    }

    public double[] getStoredSample() {
        String storedSampleString = mHelperInstance.getFromPreferences(R.string.key_sample, "[]");
        double[] sampleArray = mHelperInstance.getGson().fromJson(storedSampleString, double[].class);

        if (sampleArray.length == 0) {
            sampleArray = new double[Constants.N_DIMENSIONS + 2];
        }

        return sampleArray;
    }

    public Vector getSampleForProcessing() {
        double[] sampleArray = getStoredSample();
        double[] sampleForProcessing = new double[Constants.N_DIMENSIONS];

        System.arraycopy(sampleArray, 0, sampleForProcessing, 0, sampleForProcessing.length);

        return new BasicVector(sampleForProcessing);
    }

    public void getOriginalLabelForUserActivity() {
        int hour, minute, row, col, position, label = 0;
        int minHour = Constants.INIT_HR, maxHour;
        String stringSchedule;
        boolean[] schedule = new boolean[Constants.N_GRIDS];
        Calendar rightNow = Calendar.getInstance();

        // fetching true label for the given time from the saved schedule preferences
        hour = rightNow.get(Calendar.HOUR_OF_DAY);
        minute = rightNow.get(Calendar.MINUTE);

        if (Constants.N_ROWS % 2 == 0) {
            // if even number of rows
            maxHour = Constants.INIT_HR + (Constants.N_ROWS / 2) - 1;
        } else {
            maxHour = Constants.INIT_HR + (Constants.N_ROWS - 1) / 2 - 1;
        }

        Log.d(TAG, "Max Hour : " + maxHour);

        if (hour < Constants.INIT_HR || hour > maxHour) {
            label = 1;
        } else {

            // calculate position of the current hour and min in the schedule saved
            row = Math.abs(Constants.INIT_HR - hour) * 2 + 1;
            row = (minute / 30) == 0 ? row : row + 1;
            col = mSample.getOriginalDayOfWeek();
            position = row * Constants.N_COLS + col;
            stringSchedule = mHelperInstance.getFromPreferences(R.string.key_schedule, "");

            Log.d(TAG, "Row, Col, Position : " + row + ", " + col + ", " + position);

            if (stringSchedule == "") {
                Log.d(TAG, "schedule string is empty.. returning");
                return;
            }

            schedule = mHelperInstance.getGson().fromJson(stringSchedule, boolean[].class);

            if (position < Constants.N_GRIDS && schedule[position]) {
                label = 1;
            }
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
            fetchRemoteModel();
        } else {
            /**
             * compute the gradient, and train the model
             *
             */

            fetchLocalModel();
            processModel();
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
        Vector x = getSampleForProcessing();
        double
                wx = x.innerProduct(mModelVector),
                probability = 1 / (1 + Math.exp(-wx)),
                // loss function
                factor = -((double) getOriginalLabel() - probability) * probability * (1 - probability);

        mGradient = x.multiply(factor);
        Log.d(TAG, "Computed Gradient : " + getVectorAsString(mGradient));
        saveGradient();
    }

    public void saveGradient() {
        mHelperInstance.saveToPreferences(R.string.key_latest_gradient, getVectorAsArray(mGradient));
    }

    public void updateModel() {

        // if lambda is stored in preferences, use that, else use the default lambda
        double lambda = Double.parseDouble(mHelperInstance.getFromPreferences(R.string.key_lambda, Double.toString(LAMBDA)));

        updateGradient();
        // lambda/t - t is the number of samples processed
        // OR lambda/root t

        mModelVector = mModelVector.subtract(mGradient.multiply(lambda / getSampleCount()));

        Log.d(TAG, "Sample count " + getSampleCount());
        Log.d(TAG, "Updated Gradient" + getVectorAsString(mGradient));
        Log.d(TAG, "Updated Model " + getVectorAsString(mModelVector));

        saveModel();
        updateSampleCount();
    }

    public void saveModel() {
        // mSample.setModel(getVectorAsString(mModelVector));
        mHelperInstance.saveToPreferences(R.string.key_latest_model, getVectorAsArray(mModelVector));
        Log.d(TAG, "Saved Model " + getVectorAsString(mModelVector));
    }

    public double[] getVectorAsArray(Vector vector) {
        return (new BasicVector(vector)).toArray();
    }

    public String getVectorAsString(Vector vector) {
        return mHelperInstance.getGson().toJson(getVectorAsArray(vector));
    }

    public void fetchRemoteModel() {

        StringRequest stringGETRequest;

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "model received from server : " + response);

                try {
                    double[] modelArray = mHelperInstance.getGson().fromJson(response, double[].class);
                    mModelVector = new BasicVector(modelArray);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                processModel();
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                fetchLocalModel();
                processModel();
            }
        };

        stringGETRequest = new StringRequest(Request.Method.GET, mHelperInstance.getFromPreferences(R.string.key_api, DEFAULT_API_URL), responseListener, errorListener);

        addRequestToQueue(stringGETRequest, "");
    }

    public void fetchLocalModel() {
        String stringModel = HelperClass.getInstance().getFromPreferences(R.string.key_latest_model, "");
        // if some preferences are stored, restore it
        if (stringModel != "") {
            mModelVector = new BasicVector(mHelperInstance.getGson().fromJson(stringModel, double[].class));
        } else {
            mModelVector = new BasicVector(new double[Constants.N_DIMENSIONS]);
        }
    }

    public void processModel() {
        // store the fetched model as the latest
        saveModel();
        // update the model, after updating gradient
        // updates and saves gradient, computes new model and calls saveModel() again
        predict();
        updateModel();

        addSample();

        if (isConnectedToNetwork()) {
            
            syncSamples();

        }
    }

    /**
     *
     * send the gradient first, then the unsent samples
     * deletes the sent samples after receiving a response
     * 
     */
    public void syncSamples() {
        sendGradientToServer();

        // send the device id
        // email and the timestamp .. 
        List<ExpandedSample> samples = ExpandedSample.findWithQuery(ExpandedSample.class, "Select * from Expanded_Sample ORDER BY id");
        ExpandedSample sample;

        mSamples.clear();

        for (Iterator<ExpandedSample> iter = samples.iterator(); iter.hasNext(); ) {
            sample = iter.next();
            mSamples.add(sample.getSampleArray());

        }
        Log.d(TAG, "Sending samples : " + mSamples);
        sendDataToServer();

    }

    public void predict() {
        Vector x = getSampleForProcessing();

        double wx = x.innerProduct(mModelVector);
        double probability = 1 / (1 + Math.exp(-wx));
        double[] sampleArray = getStoredSample();
        int label = 0;

        Log.i(TAG, " Probability.. " + probability);
        if (probability > 0.5) {
            label = 1;
        }

        sampleArray[Constants.N_DIMENSIONS] = label;
        mHelperInstance.saveToPreferences(R.string.key_sample, sampleArray);
    }

    public void sendDataToServer() {
        StringRequest stringPUTRequest = new StringRequest(Request.Method.POST, mHelperInstance.getFromPreferences(R.string.key_api, DEFAULT_API_URL), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                mSamples.clear();
                handlePOSTResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "POSTing samples failed.. ");
                handleError(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("sample", "[" + TextUtils.join(", ", mSamples) + "]");
                params.put("device_id", mHelperInstance.getFromPreferences(R.string.key_device_id, ""));

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        Log.i(TAG, "Making data post request..");
        addRequestToQueue(stringPUTRequest, "");

    }

    public void sendGradientToServer() {

        StringRequest stringPUTRequest = new StringRequest(Request.Method.POST, mHelperInstance.getFromPreferences(R.string.key_api, DEFAULT_API_URL), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                handlePOSTResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "POSTing model failed.. ");
                handleError(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                String stringGradient = mHelperInstance.getFromPreferences(R.string.key_latest_gradient, "");

                Log.d(TAG, "Gradient sent : " + stringGradient);
                params.put("gradient", stringGradient);
                params.put("device_id", mHelperInstance.getFromPreferences(R.string.key_device_id, ""));

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        Log.i(TAG, "Making gradient post request..");
        addRequestToQueue(stringPUTRequest, "");

    }

    public void handlePOSTResponse(String response) {
        JSONObject jsonResponse = null;

        /** response from POST request */
        Log.i(TAG, "POST response .. " + response);
        try {
            jsonResponse = new JSONObject(response);

            String cmd = jsonResponse.getString("cmd");

            if (cmd.equalsIgnoreCase("delete")) {

                ExpandedSample.deleteAll(ExpandedSample.class);

                // stop the service..
                stopSelf();
            } else if (cmd.equalsIgnoreCase(getString(R.string.key_api))) {
                mHelperInstance.saveToPreferences(R.string.key_api, jsonResponse.getString("value"));
            } else if (cmd.equalsIgnoreCase(getString(R.string.key_lambda))) {
                mHelperInstance.saveToPreferences(R.string.key_lambda, jsonResponse.getString("value"));
            } else if (cmd.equalsIgnoreCase(getString(R.string.key_n_activities))) {
                mHelperInstance.saveToPreferences(R.string.key_n_activities, jsonResponse.getString("value"));
            } else if (cmd.equalsIgnoreCase(getString(R.string.key_sample_frequency))) {
                mHelperInstance.saveToPreferences(R.string.key_sample_frequency, jsonResponse.getString("value"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addRequestToQueue(Request<T> request, String tag) {
        request.setTag(TextUtils.isEmpty(tag) ? TAG : tag);

        VolleyLog.d("Adding request to Queue.. %s\n", request.getUrl());

        /* by default Volley cached JSON responses */
        request.setShouldCache(false);
        getRequestQueue().add(request);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }

    }

    public void handleError(VolleyError error) {
        Log.d(TAG, "Error.. " + error.getMessage());
    }
}