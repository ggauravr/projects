package com.example.healthapp;

import java.net.URLEncoder;
import java.security.SignatureException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import android.util.Log;

public class HmacSignManager {
	
	public static final String TAG = "HMACSigner";
	public static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
	
	@SuppressWarnings("deprecation")
	public static String getHMACSHA1Sign(String baseString, String secret) throws SignatureException{
		
		String sign;
		
		Log.i(TAG, "Base String: " + baseString);
		Log.i(TAG, "Secret: " + secret);
		
		try {
		
			// get an hmac_sha1 key from the raw key bytes
			SecretKeySpec signingKey = new SecretKeySpec(secret.getBytes(), HMAC_SHA1_ALGORITHM);
			
			// get an hmac_sha1 Mac instance and initialize with the signing key
			Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
			mac.init(signingKey);
			
			// compute the hmac on input data bytes
			byte[] rawHmac = mac.doFinal(baseString.getBytes());
			
			// base64-encode the hmac
//			sign = Base64.encodeBase64URLSafeString(rawHmac);
			sign = URLEncoder.encode(new String(Base64.encodeBase64(rawHmac)));
			
		} catch (Exception e) {
			throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
		}
		
		Log.i(TAG, "Sign: " + sign);
		
		return sign;
	}
	
}
