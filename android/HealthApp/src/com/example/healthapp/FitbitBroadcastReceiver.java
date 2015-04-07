package com.example.healthapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class FitbitBroadcastReceiver extends BroadcastReceiver {
	
	public static final String TAG = "FitbitBroadcstReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "Received Broadcast");
	}

}
