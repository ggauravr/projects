package my.example.testbackgroundservice2.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.location.DetectedActivity;

/**
 * Created by ggauravr on 3/2/14.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "activity.db";
    public static final int DB_VERSION = 3;

    public static final String TBL_ACTIVITY = "tbl_activity";
    public static final String CLMN_ID = "_id";
    public static final String CLMN_TYPE = "type";
    public static final String CLMN_DESC = "description";

    public static final String TBL_PLACE = "tbl_place";

    public static final String TBL_FEATURE = "tbl_feature";
    public static final String CLMN_ACTIVITY_TYPE = "activity_type";
    public static final String CLMN_APPROX_TIME = "approx_time";
    public static final String CLMN_HOUR = "hour";
    public static final String CLMN_DAY = "day";
    public static final String CLMN_TIMESTAMP = "timestamp";

    public static final String TBL_LABEL = "tbl_label";
    public static final String CLMN_ACTIVITY_ID = "activity_id";
    public static final String CLMN_PREDICTED = "predicted";
    public static final String CLMN_ORIGINAL = "original";

    private static final String CREATE_TBL_ACTIVITY = "create table "
            + TBL_ACTIVITY
            + " ( "
            + CLMN_ID + " integer primary key autoincrement, "
            + CLMN_TYPE + " integer, "
            + CLMN_DESC + " text not null "
            + " ); ";
    private static final String CREATE_TBL_PLACE = "create table "
            + TBL_PLACE
            + " ( "
            + CLMN_ID + " integer primary key autoincrement, "
            + CLMN_TYPE + " integer, "
            + CLMN_DESC + " text not null "
            + " ); ";
    private static final String CREATE_TBL_FEATURE = "create table "
            + TBL_FEATURE
            + " ( "
            + CLMN_ID + " integer primary key autoincrement, "
            + CLMN_ACTIVITY_TYPE + " integer, "
            + CLMN_APPROX_TIME + " integer, "
            + CLMN_HOUR + " integer, "
            + CLMN_DAY + " integer, "
            + CLMN_TIMESTAMP + " real, "
            + "foreign key ("+ CLMN_ACTIVITY_TYPE + ") references "+ TBL_ACTIVITY +"("+CLMN_TYPE+")"
            + " ); ";
    private static final String CREATE_TBL_LABEL = "create table "
            + TBL_LABEL
            + " ( "
            + CLMN_ID + " integer primary key autoincrement, "
            + CLMN_ACTIVITY_ID + " integer, "
            + CLMN_PREDICTED + " integer, "
            + CLMN_ORIGINAL + " integer, "
            + "foreign key ("+ CLMN_ACTIVITY_ID + ") references "+ TBL_ACTIVITY +"("+CLMN_TYPE+"), "
            + "foreign key ("+ CLMN_PREDICTED + ") references "+ TBL_PLACE +"("+CLMN_TYPE+"), "
            + "foreign key ("+ CLMN_ORIGINAL + ") references "+ TBL_PLACE +"("+CLMN_TYPE+")"
            + " ); ";

    private static final String DROP_TBL_ACTIVITY = "drop table if exists " + TBL_ACTIVITY;
    private static final String DROP_TBL_PLACE = "drop table if exists " + TBL_PLACE;
    private static final String DROP_TBL_FEATURE = "drop table if exists " + TBL_FEATURE;
    private static final String DROP_TBL_LABEL = "drop table if exists " + TBL_LABEL;

    public DBHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DBHelper", "creating tables");
        /*
        * create feature, label, activity and places table
        * */
        db.execSQL(CREATE_TBL_ACTIVITY);
        db.execSQL(CREATE_TBL_PLACE);
        db.execSQL(CREATE_TBL_FEATURE);
        db.execSQL(CREATE_TBL_LABEL);

     }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(CREATE_TBL_ACTIVITY);
        db.execSQL(CREATE_TBL_PLACE);
        db.execSQL(CREATE_TBL_FEATURE);
        db.execSQL(CREATE_TBL_LABEL);
        onCreate(db);
    }

    public void fillActivityTable(SQLiteDatabase db){
        long insertedID;
        ContentValues cv = new ContentValues();

        cv.put(CLMN_TYPE, DetectedActivity.STILL);
        cv.put(CLMN_DESC, "Still");

        insertedID = db.insert(TBL_ACTIVITY, "null", cv);

        cv.put(CLMN_TYPE, DetectedActivity.TILTING);
        cv.put(CLMN_DESC, "Tilting");

        insertedID = db.insert(TBL_ACTIVITY, "null", cv);

        cv.put(CLMN_TYPE, DetectedActivity.UNKNOWN);
        cv.put(CLMN_DESC, "Unknown");

        insertedID = db.insert(TBL_ACTIVITY, "null", cv);

        cv.put(CLMN_TYPE, DetectedActivity.ON_FOOT);
        cv.put(CLMN_DESC, "Moving");

        insertedID = db.insert(TBL_ACTIVITY, "null", cv);

        cv.put(CLMN_TYPE, DetectedActivity.IN_VEHICLE);
        cv.put(CLMN_DESC, "Moving");

        insertedID = db.insert(TBL_ACTIVITY, "null", cv);
    }

    public void fillPlaceTable(SQLiteDatabase db){
        long insertedID;
        ContentValues cv = new ContentValues();

        cv.put(CLMN_TYPE, 1);
        cv.put(CLMN_DESC, "Class");

        insertedID = db.insert(TBL_PLACE, "null", cv);

        cv.put(CLMN_TYPE, 2);
        cv.put(CLMN_DESC, "Home");

        insertedID = db.insert(TBL_PLACE, "null", cv);
    }

    public static void saveFeature(SQLiteDatabase db, Feature f){
        long insertID;
        ContentValues cv = new ContentValues();

        cv.put(CLMN_ACTIVITY_TYPE, f.getActivity());
        cv.put(CLMN_APPROX_TIME, f.getApproxTime());
        cv.put(CLMN_HOUR, f.getHour());
        cv.put(CLMN_DAY, f.getDayOfWeek());
        cv.put(CLMN_TIMESTAMP, f.getTimestamp());

        insertID = db.insert(TBL_FEATURE, "null", cv);

        db.close();
    }

}
