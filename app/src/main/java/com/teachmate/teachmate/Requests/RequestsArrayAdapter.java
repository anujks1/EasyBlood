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
import com.teachmate.teachmate.TempDataClass;
import com.teachmate.teachmate.models.Requests;

/**
 * Created by NiRavishankar on 12/15/2014.
 */
public class RequestsArrayAdapter extends ArrayAdapter<Requests> {
    private final Context context;
    private final Requests[] values;

    public RequestsArrayAdapter(Context context, Requests[] values) {
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

        textViewRequestUserName.setText(values[position].RequestUserName);
        textViewRequestString.setText(values[position].RequestString);
        textViewTime.setText(values[position].RequestTime);

        if(!values[position].RequestUserProfilePhotoServerPath.isEmpty() && values[position].RequestUserProfilePhotoServerPath != null){
            Picasso.with(context).load(values[position].RequestUserProfilePhotoServerPath).into(image);
        }
        // Change the icon for Windows and iPhone

        return rowView;
    }
}
