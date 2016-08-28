package com.teachmate.teachmate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.teachmate.teachmate.models.NsItemModel;

/**
 * Created by NiRavishankar on 1/15/2015.
 */
public class NsMenuAdapter extends ArrayAdapter<NsItemModel> {

    private final Context context;
    private final NsItemModel[] resource;

    public NsMenuAdapter(Context context, NsItemModel[] resource) {
        super(context, R.layout.notification_custom_list_item, resource);
        this.context = context;
        this.resource = resource;
    }

    public static class ViewHolder {
        public final TextView textHolder;
        public final ImageView imageHolder;
        public final TextView textCounterHolder;

        public ViewHolder(TextView text1, ImageView image1,TextView textcounter1) {
            this.textHolder = text1;
            this.imageHolder = image1;
            this.textCounterHolder=textcounter1;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        NsItemModel item = resource[position];
        ViewHolder holder = null;
        View view = convertView;

        int layout = R.layout.notification_custom_list_item;

        view = LayoutInflater.from(getContext()).inflate(layout, null);

        TextView text1 = (TextView) view.findViewById(R.id.ns_title);
        ImageView image1 = (ImageView) view.findViewById(R.id.ns_icon);
        TextView textcounter1 = (TextView) view.findViewById(R.id.ns_counter);
        view.setTag(new ViewHolder(text1, image1,textcounter1));

        if(item.title.equals("Home")){
            image1.setImageResource(R.drawable.home);
        }
        if(item.title.equals("Chat")){
            image1.setImageResource(R.drawable.chat);
        }
        if(item.title.equals("About")){
            image1.setImageResource(R.drawable.info);
        }
        if(item.title.equals("Questions")){
            image1.setImageResource(R.drawable.question);
        }
        if(item.title.equals("Requests")){
            image1.setImageResource(R.drawable.requests);
        }
        if(item.title.equals("My Questions")){
            image1.setImageResource(R.drawable.my_questions);
        }
        if(item.title.equals("My Requests")){
            image1.setImageResource(R.drawable.my_requests);
        }

        text1.setText(item.title);
        if(item.counter == 0){
            textcounter1.setVisibility(View.GONE);
        }
        else {
            textcounter1.setText(String.valueOf(item.counter));
        }

/*        if (holder == null && view != null) {
            Object tag = view.getTag();
            if (tag instanceof ViewHolder) {
                holder = (ViewHolder) tag;
            }
        }


        if(item != null && holder != null){

            if (holder.textHolder != null)
                holder.textHolder.setText(item.title);

            //Counter
            if (holder.textCounterHolder != null){
                if (item.counter > 0){
                    holder.textCounterHolder.setVisibility(View.VISIBLE);
                    holder.textCounterHolder.setText(""+item.counter);
                }else{
                    //Hide counter if == 0
                    holder.textCounterHolder.setVisibility(View.GONE);
                }
            }

            if (holder.imageHolder != null) {
                if (item.iconRes > 0) {
                    holder.imageHolder.setVisibility(View.VISIBLE);
                    holder.imageHolder.setImageResource(item.iconRes);
                } else {
                    holder.imageHolder.setVisibility(View.GONE);
                }
            }
        }*/

        return view;
    }
}
