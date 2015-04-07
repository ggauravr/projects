package com.example.healthapp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import android.webkit.JavascriptInterface;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class JSJavaBinder {
	
	private static final String TAG = "JsJavaBinder";
	
	private Context mContext;
	
	public JSJavaBinder(Context ctx){
		mContext = ctx;
	}
	
	@JavascriptInterface
	public void getOauthCredentials(String oauthToken, String oauthVerifier){
		Log.i(TAG, "JS Data: " + oauthToken + " :: " + oauthVerifier);
		
		// this token is the same token as the response from the initial call
		OauthManager.tempOauthToken = oauthToken;
		
		OauthManager.oauthVerifier = oauthVerifier;
		
		final RequestQueue rQueue = ConnectionManager.getInstance(mContext).getRequestQueue();
        StringRequest sr = new StringRequest(Request.Method.POST,OauthManager.ACCESS_TOKEN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            	Log.i(TAG, "Response: " + response);
            	
            	parseTokens(response);
            	
            	// save tokens in shared preferences
            	SharedPreferences sPrefs = mContext.getSharedPreferences(mContext.getString(R.string.FITBIT_PREFERENCE_KEY), Context.MODE_PRIVATE);
            	SharedPreferences.Editor editor = sPrefs.edit();
            	
            	editor.putString(FitbitConstants.ACCESS_TOKEN, OauthManager.accessOauthToken);
            	editor.putString(FitbitConstants.ACCESS_TOKEN_SECRET, OauthManager.accessOauthTokenSecret);
            	editor.putString(FitbitConstants.ENCODED_USER_ID, OauthManager.encodedUserId);
            	
            	editor.commit();
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
                params.put("Authorization","OAuth " + OauthManager.getHeaders(OauthManager.REQ_TYPE_ACCESS_TOKEN, null));
                
                Log.i(TAG, params.toString());
                
                return params;
            }
        };
        
        rQueue.add(sr);
	}
	
	private void parseTokens(String response){
		String[] responseParams = response.split("&");
    	
    	Log.i(TAG, "ResponseParams: " + Arrays.toString(responseParams));
    	
    	OauthManager.accessOauthToken = responseParams[0].split("=")[1];
    	OauthManager.accessOauthTokenSecret = responseParams[1].split("=")[1];
    	OauthManager.encodedUserId = responseParams[2].split("=")[1];
	}
}
