package my.example.activityrecognition.app;

import android.content.Context;

import com.orm.SugarRecord;

/**
 * Created by ggauravr on 4/2/14.
 */
public class ActivityMaster extends SugarRecord<ActivityMaster> {

    private int mType;
    private String mDescription;

    public ActivityMaster(Context ctx){
        super(ctx);
    }

    public ActivityMaster(Context ctx,int _type, String _description ){
        super(ctx);

        this.mType = _type;
        this.mDescription = _description;
    }

}
