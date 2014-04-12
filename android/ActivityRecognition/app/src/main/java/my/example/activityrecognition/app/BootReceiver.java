package my.example.activityrecognition.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 *  @author : Gaurav Ramesh
 *  @email : gggauravr@gmail.com         
 * 
 *  @class : BootReceiver
 *  @description: listens for system boot broadcast to start the background service, 
 *                      if was previously running before reboot
 * 
 */

public class BootReceiver extends BroadcastReceiver {
    // executed when boot is completed
    @Override
    public void onReceive(Context context, Intent intent) {

        Context globalContext = context.getApplicationContext();
        HelperClass helperInstance = HelperClass.getInstance(globalContext);

        Log.d("BroadcastReceiver", helperInstance.getFromPreferences(R.string.key_service_status, "false"));

        //we double check here for only boot complete event
        if(intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED))
        {
            if(Boolean.parseBoolean(helperInstance.getFromPreferences(R.string.key_service_status, "false"))){
                //here we start the service
                Intent serviceIntent = new Intent(globalContext, BackgroundService.class);
                context.startService(serviceIntent);
            }

        }
    }

}
