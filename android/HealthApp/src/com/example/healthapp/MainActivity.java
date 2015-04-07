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

import android.app.Activity;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class MainActivity extends Activity {

	public static final String TAG = "MainActivity";
	
	private IntentFilter mFitbitIntentFilter;
	private BroadcastReceiver mFitbitReceiver; 
	private Intent mFitbitIntent;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       
        registerFitbitReceiver();
        updateFitbitData();
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void registerFitbitReceiver(){
    	mFitbitReceiver = new FitbitBroadcastReceiver();
    	mFitbitIntentFilter = new IntentFilter(FitbitConstants.BROADCAST_ACTION);
    	
    	LocalBroadcastManager.getInstance(this).registerReceiver(mFitbitReceiver, mFitbitIntentFilter);
    }
    
    private void updateFitbitData(){
    	mFitbitIntent = new Intent(this, FitbitDataService.class);
    	
    	startService(mFitbitIntent);
    }
    
    public void something(){
        final RequestQueue rQueue = ConnectionManager.getInstance(this.getApplicationContext()).getRequestQueue();
        StringRequest sr = new StringRequest(Request.Method.POST,OauthManager.TEMP_TOKEN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            	Log.i(TAG, "Response: " + response);
            	
            	String[] responseParams = response.split("&");
            	
            	Log.i(TAG, "ResponseParams: " + Arrays.toString(responseParams));
            	
//            	OauthManager.oauthToken = responseParams[0].split("=")[1];
//            	OauthManager.oauthTokenSecret = responseParams[1].split("=")[1];
//            	
//            	Log.i(TAG, "OAuth Token" + OauthManager.oauthToken);
//            	Log.i(TAG, "OAuth Token Secret" + OauthManager.oauthTokenSecret);
            	
            	/*Intent authorizeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(OauthManager.AUTHORIZE_URL+OauthManager.oauthToken));
            	startActivity(authorizeIntent);*/
            	
            	Intent authenticationIntent = new Intent(MainActivity.this, AuthenticationActivity.class);
               	startActivity(authenticationIntent);
            	
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
                params.put("Authorization","OAuth " + OauthManager.getHeadersForTempToken());
                
                Log.i(TAG, params.toString());
                
                return params;
            }
        };
        
        rQueue.add(sr);
    }
}
