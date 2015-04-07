package com.example.healthapp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class FitbitDataService extends IntentService {
	
	public static final String TAG = "FitbitDataService";
	
	public Context mContext;
	
	public FitbitDataService() {
		super("FitbitDataService");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		mContext = getBaseContext();
		
		Log.i(TAG, "Base Context: " + mContext);
		
		if(!isLoggedIn()){
			Log.i(TAG, "Not logged in");
			logIn();
		}
		else{
			Log.i(TAG, "Logged in");
			updateAPIParams();
			updateData();
		}
	}
	
	private boolean isLoggedIn(){
		boolean loginStatus = false;
		
		SharedPreferences sPrefs = mContext.getSharedPreferences(mContext.getString(R.string.FITBIT_PREFERENCE_KEY), Context.MODE_PRIVATE);
		
		if(sPrefs.getString(FitbitConstants.ENCODED_USER_ID, null) != null){
			loginStatus = true;
		}
		
		Log.i(TAG, "Login Status: " + loginStatus);
		
		return loginStatus;
	}
	
	private void logIn(){
		final RequestQueue rQueue = ConnectionManager.getInstance(mContext).getRequestQueue();
        StringRequest sr = new StringRequest(Request.Method.POST,FitbitConstants.TEMP_TOKEN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            	Log.i(TAG, "Response: " + response);
            	
            	parseTokens(response);
            	
            	Intent authenticationIntent = new Intent(mContext, AuthenticationActivity.class)
            										// important to start an activity from outside an activity
            										.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            	
               	getApplication().startActivity(authenticationIntent);
            	
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            	Log.i(TAG, "Error Response: " + error.toString());
            }
        }){
        	
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Authorization","OAuth " + OauthManager.getHeaders(OauthManager.REQ_TYPE_TEMP_TOKEN, null));
                
                Log.i(TAG, params.toString());
                
                return params;
            }
        };
        
        rQueue.add(sr);
	}
	
	private void parseTokens(String response){
		String[] responseParams = response.split("&");
    	
    	Log.i(TAG, "ResponseParams: " + Arrays.toString(responseParams));
    	
    	OauthManager.tempOauthToken = responseParams[0].split("=")[1];
    	OauthManager.tempOauthTokenSecret = responseParams[1].split("=")[1];
	}
	
	/*private void sendLoginStatus(){
		Log.i(TAG, "Sending login status");
		
		Intent loginStatusIntent = new Intent(FitbitConstants.BROADCAST_ACTION)
										.putExtra(FitbitConstants.LOGIN_STATUS, FitbitConstants.NOT_LOGGED_IN);
		
		LocalBroadcastManager.getInstance(this).sendBroadcast(loginStatusIntent);
	}*/
	
	private void updateAPIParams(){
		SharedPreferences sPrefs = mContext.getSharedPreferences(mContext.getString(R.string.FITBIT_PREFERENCE_KEY), Context.MODE_PRIVATE);
		
		OauthManager.accessOauthToken = sPrefs.getString(FitbitConstants.ACCESS_TOKEN, null);
		OauthManager.accessOauthTokenSecret = sPrefs.getString(FitbitConstants.ACCESS_TOKEN_SECRET, null);
		OauthManager.encodedUserId = sPrefs.getString(FitbitConstants.ENCODED_USER_ID, null);
		
		Log.i(TAG, OauthManager.encodedUserId);
	}
	
	private void updateData(){
//		fetch data from required API end-points
//		store in a persistent system
		final RequestQueue rQueue = ConnectionManager.getInstance(mContext).getRequestQueue();
        JsonObjectRequest sr = new JsonObjectRequest(Request.Method.GET,FitbitConstants.DAILY_GOAL_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
            	Log.i(TAG, "Response: " + response.toString());
            	
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            	Log.i(TAG, "Error Response: " + error.toString());
            }
        }){
        	
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Authorization","OAuth " + OauthManager.getHeaders(OauthManager.REQ_TYPE_RESOURCE, FitbitConstants.DAILY_GOAL_URL));
                
                Log.i(TAG, params.toString());
                
                return params;
            }
        };
        
        rQueue.add(sr);
	}
}
