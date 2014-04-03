package my.example.activityrecognition.app;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by ggauravr on 3/15/14.
 */
public class CalendarAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private int mResource, mTextViewResourceId;
    private String[] mValues;

    public CalendarAdapter(Context context, int resource, int textViewResourceId, String[] objects) {
        super(context, resource, textViewResourceId, objects);

        mContext = context;
        mResource = resource;
        mTextViewResourceId = textViewResourceId;
        mValues = objects;
    }

    @Override
    public String getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getPosition(String item) {
        return super.getPosition(item);
    }

    @Override
    public int getCount() {
        return 200;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if(view == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            ViewHolder viewHolder = new ViewHolder();

            view = inflater.inflate(mResource, null);

            viewHolder.textView = (TextView) view.findViewById(mTextViewResourceId);
            view.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.textView.setText(mValues[position]);

        if(CalendarActivity.tempSelected[position] == true){
            view.setBackgroundColor(Color.RED);
        }
        else{
            view.setBackgroundColor(Color.WHITE);
        }

        return view;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    static class ViewHolder{
        public TextView textView;
    }
}
