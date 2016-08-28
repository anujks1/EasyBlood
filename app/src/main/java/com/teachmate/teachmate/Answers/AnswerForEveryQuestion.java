package com.teachmate.teachmate.Answers;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.teachmate.teachmate.R;

/**
 * Created by ARavi on 1/30/2015.
 */
public class AnswerForEveryQuestion extends Fragment {
    public TextView qusername,qanswer,answeredby,answertime,askedtime,question1;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentActivity faActivity  = (FragmentActivity) super.getActivity();


        LinearLayout linearLayout=(LinearLayout)inflater.inflate(R.layout.answerlist_onclick,container,false);
        qusername=(TextView)linearLayout.findViewById(R.id.tvusernameanswerlist_onclick);
        question1=(TextView)linearLayout.findViewById(R.id.tvquestionanswerlist_onclick);
        qanswer=(TextView)linearLayout.findViewById(R.id.tvansweranswerlist_onclick);
        answeredby=(TextView)linearLayout.findViewById(R.id.tvansweredbyanswerlist_onclick);
        answertime=(TextView)linearLayout.findViewById(R.id.tvanswertimeanswerlist_onclick);
        askedtime=(TextView)linearLayout.findViewById(R.id.tvaskedtimeanswerlist_onclick);

        Bundle args=getArguments();
        question1.setText(args.getString("question"));
        qusername.setText(args.getString("username")+" asked");
        qanswer.setText(args.getString("answer"));
        answeredby.setText(args.getString("answeredby")+" replied");
        answertime.setText(args.getString("answeredtime"));
        askedtime.setText(args.getString("askedtime"));


        return linearLayout;
    }
}
