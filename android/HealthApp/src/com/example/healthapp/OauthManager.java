package com.example.healthapp;

import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.util.Date;

import android.util.Log;

public class OauthManager {
	
	public static final String TAG = "OauthManager";
	
	public static final String KEY = "10075fd21175428bb4ce414a15f10bac";
	public static final String SECRET = "c01daf64c6e64dc3989bd9967180f0aa";
	public static final String TEMP_TOKEN_URL = "https://api.fitbit.com/oauth/request_token";
	public static final String AUTHORIZE_URL = "https://www.fitbit.com/oauth/authorize?oauth_token=";
	public static final String ACCESS_TOKEN_URL = "https://api.fitbit.com/oauth/access_token";
	
	public static final int REQ_TYPE_TEMP_TOKEN = 0;
	public static final int REQ_TYPE_ACCESS_TOKEN = 1;
	public static final int REQ_TYPE_RESOURCE = 2;
	
	public static Long timestamp = 0L;
	
	// first and second call response
	public static String tempOauthToken;
	
	// first call response
	public static String tempOauthTokenSecret;
	
	// second call response
	public static String oauthVerifier;
	
	// third/final call response
	public static String accessOauthToken;
	public static String accessOauthTokenSecret;
	public static String encodedUserId;
	
	public static String getBaseString(int type, String pURL){
		StringBuffer baseString = new StringBuffer("");
		
		String method = "GET&";
		String url = "";
		StringBuffer params = new StringBuffer("");
		
		params.append(URLEncoder.encode("oauth_consumer_key="+FitbitConstants.CONSUMER_KEY));
		params.append(URLEncoder.encode("&oauth_nonce="));
		params.append(URLEncoder.encode("&oauth_signature_method=HMAC-SHA1"));
		params.append(URLEncoder.encode("&oauth_timestamp="+getTimestamp()));
		
		switch(type){
		
			case REQ_TYPE_TEMP_TOKEN:
				
				method = "POST&";
				url = URLEncoder.encode(TEMP_TOKEN_URL)+"&";
				
				break;
				
			case REQ_TYPE_ACCESS_TOKEN:
				
				method = "POST&";
				url = URLEncoder.encode(ACCESS_TOKEN_URL)+"&";
				
				params.append(URLEncoder.encode("&oauth_token="+OauthManager.tempOauthToken));
				params.append(URLEncoder.encode("&oauth_verifier="+OauthManager.oauthVerifier));
				break;
				
			case REQ_TYPE_RESOURCE:
				
				method = "GET&";
				url = URLEncoder.encode(pURL)+"&";
				
				params = new StringBuffer("");
				
				params.append(URLEncoder.encode("oauth_consumer_key="+KEY));
				params.append(URLEncoder.encode("&oauth_nonce="));
				params.append(URLEncoder.encode("&oauth_signature_method=HMAC-SHA1"));
				params.append(URLEncoder.encode("&oauth_timestamp="+getTimestamp()));
				params.append(URLEncoder.encode("&oauth_token="+OauthManager.accessOauthToken));
				
				break;
			
		}
		
		params.append(URLEncoder.encode("&oauth_version=1.0"));
		Log.i(TAG, "Base String: " + method + url + params.toString());
		
		return baseString.append(method + url + params.toString()).toString();
	}
	
	public static String getNonce(){
		SecureRandom sr = new SecureRandom();
		byte[] output = new byte[16];
		sr.nextBytes(output);
		
		return output.toString();
	}
	
	public static Long getTimestamp(){
		if(timestamp == 0L){
			timestamp = new Date().getTime();
			
			timestamp /= 1000;
		}
		
		return timestamp;
	}
	
	public static String getHeadersForTempToken(){
		StringBuffer headers = new StringBuffer("");
		
		Log.i(TAG, "Timestamp: " + timestamp);
		
		headers.append("oauth_consumer_key=\""+KEY+"\"");
		headers.append(", oauth_nonce=\"\"");
		try {
			headers.append(", oauth_signature=\""+HmacSignManager.getHMACSHA1Sign(OauthManager.getBaseString(REQ_TYPE_TEMP_TOKEN, null), OauthManager.SECRET+"&")+"\"");
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		headers.append(", oauth_signature_method=\"HMAC-SHA1\"");
		headers.append(", oauth_timestamp=\""+getTimestamp()+"\"");
		headers.append(", oauth_version=\"1.0\"");
		
		Log.i(TAG, "Headers: " + headers.toString());
		
		return headers.toString();
	}
	
	public static String getSignature(int type, String pURL){
		String signature = "";
		
		try{
		switch(type){
			
			case REQ_TYPE_TEMP_TOKEN:
				signature = HmacSignManager.getHMACSHA1Sign(OauthManager.getBaseString(type, null), OauthManager.SECRET+"&");
				break;
				
			case REQ_TYPE_ACCESS_TOKEN:
				signature = HmacSignManager.getHMACSHA1Sign(OauthManager.getBaseString(type, null), OauthManager.SECRET+"&"+OauthManager.tempOauthTokenSecret);
				break;
				
			case REQ_TYPE_RESOURCE:
				signature = HmacSignManager.getHMACSHA1Sign(OauthManager.getBaseString(type, pURL), OauthManager.SECRET+"&"+OauthManager.accessOauthTokenSecret);
		}
			
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return signature;
	}
	
	public static String getHeadersForAccessToken(){
		StringBuffer headers = new StringBuffer("");
		
		Log.i(TAG, "Timestamp: " + timestamp);
		
		headers.append("oauth_consumer_key=\""+KEY+"\"");
		headers.append(", oauth_nonce=\"\"");
		try {
			headers.append(", oauth_signature=\""+HmacSignManager.getHMACSHA1Sign(OauthManager.getBaseString(REQ_TYPE_ACCESS_TOKEN, null), OauthManager.SECRET+"&"+OauthManager.tempOauthTokenSecret)+"\"");
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		headers.append(", oauth_signature_method=\"HMAC-SHA1\"");
		headers.append(", oauth_timestamp=\""+getTimestamp()+"\"");
		headers.append(", oauth_token=\""+OauthManager.tempOauthToken+"\"");
		headers.append(", oauth_verifier=\""+OauthManager.oauthVerifier+"\"");
		headers.append(", oauth_version=\"1.0\"");
		
		Log.i(TAG, "Headers: " + headers.toString());
		
		return headers.toString();
	}
	
	public static String getHeaders(int type, String pURL){
		StringBuffer headers = new StringBuffer("");
		
		Log.i(TAG, "Timestamp: " + timestamp);
		
		headers.append("oauth_consumer_key=\""+KEY+"\"");
		headers.append(", oauth_nonce=\"\"");
		headers.append(", oauth_signature=\""+getSignature(type, pURL)+"\"");
		headers.append(", oauth_signature_method=\"HMAC-SHA1\"");
		headers.append(", oauth_timestamp=\""+getTimestamp()+"\"");
		
		switch(type){
			case REQ_TYPE_ACCESS_TOKEN:
				headers.append(", oauth_token=\""+OauthManager.tempOauthToken+"\"");
				headers.append(", oauth_verifier=\""+OauthManager.oauthVerifier+"\"");
				
				break;
				
			case REQ_TYPE_RESOURCE:
				
				headers.append(", oauth_token=\""+OauthManager.accessOauthToken+"\"");
				
				break;
		}
		
		headers.append(", oauth_version=\"1.0\"");
		
		Log.i(TAG, "Headers: " + headers.toString());
		
		return headers.toString();
	}
	
	
}
