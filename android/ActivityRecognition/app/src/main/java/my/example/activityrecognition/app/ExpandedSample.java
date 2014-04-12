package my.example.activityrecognition.app;

import android.content.Context;

import com.orm.SugarRecord;

import java.io.Serializable;

/**
 * Created by ggauravr on 4/10/14.
 */
public class ExpandedSample extends SugarRecord<ExpandedSample> implements Serializable {

    // this will be sent to the server as a sample, and also will be stored in the local/phone DB
    private String mSampleArray;

    public ExpandedSample(Context ctx){
        super(ctx);
    }

    public ExpandedSample(Context ctx, double[] sampleArray){
        super(ctx);

        // bias
        sampleArray[Constants.N_DIMENSIONS - 1] = 1;
        // predicted label
        sampleArray[Constants.N_DIMENSIONS] = sampleArray[Constants.N_DIMENSIONS];
        // original label
        sampleArray[Constants.N_DIMENSIONS+1] = sampleArray[Constants.N_DIMENSIONS+1];

        mSampleArray = HelperClass.getInstance().getGson().toJson(sampleArray);
    }

    public String getSampleArray(){
        return mSampleArray;
    }

}
