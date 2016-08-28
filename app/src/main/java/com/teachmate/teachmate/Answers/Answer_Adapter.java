package com.teachmate.teachmate.Answers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.teachmate.teachmate.R;
import com.teachmate.teachmate.models.Answer_Model;

import java.util.ArrayList;

/**
 * Created by archon on 11-01-2015.
 */
public class Answer_Adapter  extends ArrayAdapter<Answer_Model> {
    ArrayList<Answer_Model> answermodellist;
    int Resource1;
    public Context ctxt;    LayoutInflater v;

    public Answer_Adapter(Context context, int resource, ArrayList<Answer_Model> objects) {
        super(context,resource,objects);
        answermodellist=objects;
        Resource1=resource;
        ctxt =context;
        v=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder1 holder;
        if(convertView==null){
            convertView= v.inflate(Resource1,null);
            holder=new ViewHolder1();
           /* holder.imageView=(ImageView)convertView.findViewById(R.id.imageView);*/
            holder.tvansweredby=(TextView)convertView.findViewById(R.id.tvansweredby);
            holder.tvansweredtime=(TextView)convertView.findViewById(R.id.tvansweredtime);
            holder.tvrealanswer=(TextView)convertView.findViewById(R.id.tvrealanswer);
            holder.tvanswerid=(TextView)convertView.findViewById(R.id.tvanswerid);


            convertView.setTag(holder);
        }
        else{
            holder=(ViewHolder1)convertView.getTag();
        }
        holder.tvansweredby.setText(answermodellist.get(position).getAnsweredby());
        holder.tvansweredtime.setText(answermodellist.get(position).getAnsweredtime());
        holder.tvrealanswer.setText(answermodellist.get(position).getActualanswer());
       // holder.tvanswerid.setText(answermodellist.get(position).getAnswer_id());




        return convertView;
    }

    public static class ViewHolder1{
        public TextView tvansweredby;
        public TextView tvansweredtime;
        public TextView tvrealanswer;
        public TextView tvanswerid;


    }
}
