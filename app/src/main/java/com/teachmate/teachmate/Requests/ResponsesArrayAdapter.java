package com.teachmate.teachmate.Requests;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.teachmate.teachmate.R;
import com.teachmate.teachmate.models.Responses;

/**
 * Created by NiRavishankar on 12/24/2014.
 */
public class ResponsesArrayAdapter extends ArrayAdapter<Responses> {
    private final Context context;
    private final Responses[] values;

    public ResponsesArrayAdapter(Context context, Responses[] values) {
        super(context, R.layout.list_row_requests, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_row_requests, parent, false);
        TextView textViewRequestUserName = (TextView) rowView.findViewById(R.id.textViewRequestUserName);
        TextView textViewRequestString = (TextView) rowView.findViewById(R.id.textViewRequestString);
        TextView textViewTime = (TextView) rowView.findViewById(R.id.textViewTime);
        ImageView image = (ImageView) rowView.findViewById(R.id.list_image);

        textViewRequestUserName.setText(values[position].ResponseUserName);
        textViewRequestString.setText(values[position].ResponseString);
        textViewTime.setText(values[position].ResponseTime);
        // Change the icon for Windows and iPhone


        if(!values[position].ResponseUserProfilePhotoServerPath.isEmpty() && values[position].ResponseUserProfilePhotoServerPath != null){
            Picasso.with(context).load(values[position].ResponseUserProfilePhotoServerPath).into(image);
        }

        return rowView;
    }
}
