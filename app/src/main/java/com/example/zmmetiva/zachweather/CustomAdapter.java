package com.example.zmmetiva.zachweather;

/**
 * Created by zmmetiva on 12/11/15.
 */

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter implements View.OnClickListener {

    private Activity activity;
    private ArrayList data;
    private static LayoutInflater inflater=null;
    //public Resources res;
    ListModel tempValues=null;
    int i=0;

    public CustomAdapter(Activity a, ArrayList d) {

        activity = a;
        data=d;

        inflater = ( LayoutInflater )activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public int getCount() {

        if(data.size()<=0)
            return 1;
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder{

        public TextView dateView;
        public TextView highView;
        public TextView lowView;
        public ImageView image;

    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){

            vi = inflater.inflate(R.layout.weather_list_item, null);

            holder = new ViewHolder();
            holder.dateView = (TextView) vi.findViewById(R.id.dateTextView);
            holder.highView = (TextView) vi.findViewById(R.id.highTextView);
            holder.lowView = (TextView)vi.findViewById(R.id.lowTextView);
            holder.image = (ImageView)vi.findViewById(R.id.forecastImageView);

            vi.setTag( holder );
        }
        else
            holder=(ViewHolder)vi.getTag();

        if(data.size()<=0)
        {
            holder.highView.setText("No Data");

        }
        else
        {

            tempValues = null;
            tempValues = ( ListModel ) data.get( position );

            holder.dateView.setText(tempValues.getDate());
            holder.highView.setText(tempValues.getHigh());
            holder.lowView.setText(tempValues.getLow());
            holder.image.setBackgroundResource(tempValues.getImage());

            vi.setOnClickListener(new OnItemClickListener( position ));
        }
        return vi;
    }

    @Override
    public void onClick(View v) {
        Log.v("CustomAdapter", "=====Row button clicked=====");
    }

    private class OnItemClickListener  implements View.OnClickListener {
        private int mPosition;

        OnItemClickListener(int position){
            mPosition = position;
        }

        @Override
        public void onClick(View arg0) {
            MainActivity sct = (MainActivity)activity;
            sct.onItemClick(mPosition);
        }
    }
}