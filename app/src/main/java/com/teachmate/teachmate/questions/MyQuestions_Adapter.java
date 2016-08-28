package com.teachmate.teachmate.questions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.teachmate.teachmate.R;
import com.teachmate.teachmate.models.Question_Model;

import java.util.ArrayList;

/**
 * Created by archon on 26-01-2015.
 */
public class MyQuestions_Adapter extends ArrayAdapter<Question_Model> {
    ArrayList<Question_Model> questionmodellist_myquestions;
    int Resource;
    public Context ctxt;
    LayoutInflater vi;

    public MyQuestions_Adapter(Context context, int resource, ArrayList<Question_Model> objects) {
        super(context, resource, objects);
        questionmodellist_myquestions=objects;
        Resource=resource;
        ctxt=context;
        vi=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            convertView= vi.inflate(Resource,null);
            holder=new ViewHolder();
            holder.imageView_myquestions=(ImageView)convertView.findViewById(R.id.imageView_myquestions);
            //Picasso.with(ctxt).load("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTwiGED7VkaP496dzprPvXdYQ3yx2odZ4sSdW6ih2JWPthQlhcFB9m64ECS"+).into(holder.imageView);

            holder.tvusername_myquestions=(TextView)convertView.findViewById(R.id.tvusername_myquestions);
            holder.tvquestion_myquestions=(TextView)convertView.findViewById(R.id.tvquestion_myquestions);
            holder.tvquestion_id_myquestions=(TextView)convertView.findViewById(R.id.tvquestion_id_myquestions);
            holder.tvaskedtime_myquestions=(TextView)convertView.findViewById(R.id.tvaskedtime_myquestions);
            holder.imageView_myquestions = (ImageView) convertView.findViewById(R.id.imageView_myquestions);



            convertView.setTag(holder);
        }
        else{
            holder=(ViewHolder)convertView.getTag();
        }
            Question_Model q=questionmodellist_myquestions.get(position);

        if(q!=null) {
            Picasso.with(ctxt).load(questionmodellist_myquestions.get(position).getImage()).into(holder.imageView_myquestions);

            holder.tvusername_myquestions.setText(questionmodellist_myquestions.get(position).getUsername());
            holder.tvquestion_myquestions.setText(questionmodellist_myquestions.get(position).getQuestion());
           // holder.tvquestion_id_myquestions.setText(questionmodellist_myquestions.get(position).getQuestion_id());
            holder.tvaskedtime_myquestions.setText(questionmodellist_myquestions.get(position).getAsked_time());
        }



        return convertView;
    }

    static class ViewHolder{
        public ImageView imageView_myquestions;
        public TextView tvusername_myquestions;
        public TextView tvquestion_id_myquestions;
        public TextView tvaskedtime_myquestions;
        public TextView tvquestion_myquestions;


    }
}
