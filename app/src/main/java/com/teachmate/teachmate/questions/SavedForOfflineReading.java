package com.teachmate.teachmate.questions;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.teachmate.teachmate.DBClasses.DbHelper;
import com.teachmate.teachmate.DBClasses.DbTableStrings;
import com.teachmate.teachmate.FragmentTitles;
import com.teachmate.teachmate.MainActivity;
import com.teachmate.teachmate.R;
import com.teachmate.teachmate.TempDataClass;
import com.teachmate.teachmate.detaileddbview;
import com.teachmate.teachmate.models.Answer_Model;
import com.teachmate.teachmate.models.Question_Model;

import java.util.ArrayList;

/**
 * Created by archon on 16-01-2015.
 */


public class SavedForOfflineReading extends Fragment {
    public ListView listviewdb;
    public String username,qusetion,question_id,category,image,asked_time;
    EditText etsearch;
    public ArrayList<Question_Model> questionlist3;
   // public ArrayList<Question_Model> questionlist3;
    public ArrayList<Answer_Model> answerlist;
    ImageView imageView;
    TextView tvqid;
    Question_Model question1=new Question_Model();
    Answer_Model answer1=new Answer_Model();
    private static DbHelper dbHelper;
    private static SQLiteDatabase db;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(FragmentTitles.SAVED_QUESTIONS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FragmentActivity fragmentActivity=(FragmentActivity)super.getActivity();
        RelativeLayout relativeLayout=(RelativeLayout)inflater.inflate(R.layout.dblistview,container,false);
        RelativeLayout hide=(RelativeLayout)relativeLayout.findViewById(R.id.userInfoNavigationDrawer);
        hide.setVisibility(View.GONE);
        imageView = (ImageView)relativeLayout.findViewById(R.id.imageView);
       // tvqid=(TextView)relativeLayout.findViewById(R.id.tvquestion_id);
      //  tvqid.setVisibility(View.INVISIBLE);
        listviewdb = (ListView)relativeLayout.findViewById(R.id.listViewdb);

        questionlist3=new ArrayList<Question_Model>();
        populatelistviewfromdb();


       // questionlist3=new ArrayList<Question_Model>();

    //    openandquerydatabase();

     //   displayresultlist();


        listviewdb.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String user=((TextView)view.findViewById(R.id.tvusernamefromdb)).getText().toString();



                String qid=((TextView)view.findViewById(R.id.tvquestion_idfromdb)).getText().toString();
               // String url=((ImageView)view.findViewById(R.id.imageView)).getText().toString();
                String quest=((TextView)view.findViewById(R.id.tvquestionfromdb)).getText().toString();
                String asked=((TextView)view.findViewById(R.id.tvaskedtimefromdb)).getText().toString();
                //String cat=getText(R.id.).toString();
/*
                Intent data=new Intent(SavedForOfflineReading.this.getActivity(),Detaileddbview_activity.class);
                data.putExtra("usernamedb", user);
                data.putExtra("questiondb", quest);
                data.putExtra("asked_timedb", asked);
                data.putExtra("question_iddb", qid);
                data.putExtra("cat","cat");*/
              //  data.putExtra("imagedb", "image url");
               // startActivity(data);

                Bundle data=new Bundle();
                data.putString("usernamedb", user);
                data.putString("questiondb", quest);
                data.putString("asked_timedb", asked);
                data.putString("question_iddb", qid);
              //  data.putString("image", url);

                Fragment detaileddbfragment=new detaileddbview();
                // Fragment click= getFragmentManager().findFragmentById(R.id.pager);
                detaileddbfragment.setArguments(data);
                Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.container);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                TempDataClass.fragmentStack.lastElement().onPause();
                TempDataClass.fragmentStack.push(currentFragment);
                fragmentManager.beginTransaction()
                        .replace(R.id.container, detaileddbfragment)
                        .commit();

            }
        });


        return relativeLayout;

    }
    public void populatelistviewfromdb(){
        dbHelper = new DbHelper(getActivity());
        db = dbHelper.getWritableDatabase();
        Cursor cursor=db.rawQuery("SELECT * FROM "+ DbTableStrings.TABLE_NAME_QUESTION_MODEL+" ORDER BY "+DbTableStrings.QUESTION_ID+" DESC ",null);
        String[] fromcolums=new String[]{DbTableStrings.USERNAME,DbTableStrings.QUESTION,DbTableStrings.QUESTION_ID,DbTableStrings.IMAGE,DbTableStrings.ASKED_TIME};
        int[] tofeilds=new int[]{R.id.tvusernamefromdb,R.id.tvquestionfromdb,R.id.tvquestiodidstring,R.id.image,R.id.tvaskedtimefromdb};

        SimpleCursorAdapter simpleCursorAdapter;
        simpleCursorAdapter=new SimpleCursorAdapter(getActivity(),R.layout.singlerowfordblistview,cursor,fromcolums,tofeilds,0);
        listviewdb.setAdapter(simpleCursorAdapter);



    }
    public void openandquerydatabase(){
        try {

            DbHelper dbHelper=new DbHelper(getActivity());
            db=dbHelper.getWritableDatabase();

            //Cursor c2=db.query(DbTableStrings.TABLE_NAME_QUESTION_MODEL,null,null,null,null,null,null);
            Cursor c2=db.rawQuery("SELECT * FROM "+DbTableStrings.TABLE_NAME_QUESTION_MODEL,null);

            if(c2!=null){
                if(c2.moveToFirst()){
                    do{

                        String username=c2.getString(c2.getColumnIndex("username"));
                        String question=c2.getString(c2.getColumnIndex("question"));
                        String question_id=c2.getString(c2.getColumnIndex("question_id"));
                        String category=c2.getString(c2.getColumnIndex("category"));
                        String image=c2.getString(c2.getColumnIndex("image"));
                        String asked_time=c2.getString(c2.getColumnIndex("asked_time"));

                        question1.setUsername(username);
                        question1.setImage(image);
                        question1.setQuestion(question);
                        question1.setQuestion_id(question_id);
                        question1.setAsked_time(asked_time);
                        //question1.setCategory(category);

                        questionlist3.add(question1);




                    }while (c2.moveToNext());
               }
           }
            c2.close();
            db.close();



        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void displayresultlist(){


        Question_Adapter duplicate = new Question_Adapter(getActivity().getApplicationContext(), R.layout.singlerow, questionlist3);

        duplicate.notifyDataSetChanged();
    //    myadapter2.addAll(questionlist2);
        listviewdb.setAdapter(duplicate);
      //  myadapter2.notifyDataSetChanged();



    }

   /* public void openandqueryanswerdb(){
        try {

            DbHelper dbHelper=new DbHelper(getActivity());
            db=dbHelper.getWritableDatabase();
            Cursor c=db.query(DbTableStrings.TABLE_NAME_ANSWER_MODEL,null,null,null,null,null,null);

            if(c!=null){
                if(c.moveToFirst()){
                    do{

                        String answer=c.getString(c.getColumnIndex("actualanswer"));
                        String answer_id=c.getString(c.getColumnIndex("answer_id"));
                        String answeredby=c.getString(c.getColumnIndex("answeredby"));
                        String answeredtime=c.getString(c.getColumnIndex("answeredtime"));
                       // String questionid=c.getString(c.getColumnIndex("questionid"));


                       answer1.setActualanswer(answer);
                        answer1.setAnswer_id(answer_id);
                        answer1.setAnsweredby(answeredby);
                        answer1.setAnsweredtime(answeredtime);
                        answerlist.add(answer1);




                    }while (c.moveToNext());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();

    }

    public void diplayanswerlistdb(){
        Answer_Adapter myansweradapter = new Answer_Adapter(getActivity(), R.layout.answer_single, answerlist);
        listviewdb.setAdapter(myansweradapter);
        myansweradapter.notifyDataSetChanged();

    }*/
}
