package com.teachmate.teachmate;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.teachmate.teachmate.Answers.AnswerForEveryQuestion;
import com.teachmate.teachmate.Answers.Answer_Adapter;
import com.teachmate.teachmate.DBClasses.DbHelper;
import com.teachmate.teachmate.DBClasses.DbTableStrings;
import com.teachmate.teachmate.models.Answer_Model;
import com.teachmate.teachmate.models.Question_Model;

import java.util.ArrayList;

/**
 * Created by archon on 18-01-2015.
 */
public class detaileddbview extends Fragment {
    ListView answerlistview;
    public String question_id;
    ArrayList<Answer_Model> answerlistdb;
    TextView tvusernamedb,tvquestiondb,tvaskedtimedb,tvquestion_iddb;
    public ArrayList<Answer_Model> answerlist;
    Question_Model question1=new Question_Model();
    Answer_Model answerdb=new Answer_Model();
    ImageView imageViewdb;
    private static DbHelper dbHelper;
    private static SQLiteDatabase db;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentActivity fragmentActivity=(FragmentActivity)super.getActivity();
        RelativeLayout relativeLayout=(RelativeLayout)inflater.inflate(R.layout.detaileddb,container,false);
        tvusernamedb=(TextView)relativeLayout.findViewById(R.id.tvusernamedb);
        tvquestiondb=(TextView)relativeLayout.findViewById(R.id.tvquestiondb);
        tvaskedtimedb=(TextView)relativeLayout.findViewById(R.id.tvaskedtimedb);
        tvquestion_iddb=(TextView)relativeLayout.findViewById(R.id.tvquestion_iddb);
        answerlistview=(ListView)relativeLayout.findViewById(R.id.listViewdbans);
        /*answerlistdb=new ArrayList<>();
        openandqueryanswerdb();        diplayanswerlistdb();
        //openandquerydetaileddb();
        //displayresultfromdb();*/

        Bundle data= getArguments();


        try {
            String img=data.getString("imagedb");
            String l=data.getString("cat");
            question_id=data.getString("question_iddb");
            tvquestiondb.setText(data.getString("questiondb"));
            tvaskedtimedb.setText(data.getString("asked_timedb"));
            tvusernamedb.setText(data.getString("usernamedb"));
          // tvquestion_iddb.setText(data.getString("question_iddb"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        populateanswerlistfromdb();




       return relativeLayout;
    }
    public void populateanswerlistfromdb(){
        dbHelper = new DbHelper(getActivity());
        db = dbHelper.getWritableDatabase();
        Cursor cursor= null;
        try {
            cursor = db.rawQuery("SELECT * FROM "+ DbTableStrings.TABLE_NAME_ANSWER_MODEL+" WHERE "+DbTableStrings.QUESTION_ID+" = "+question_id,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] fromcolumns=new String[]{DbTableStrings.ANSWERED_BY,DbTableStrings.ANSWERED_TIME,DbTableStrings.ANSWER_ID,DbTableStrings.ACTUAL_ANSWER};
        int[] tofeilds=new int[]{R.id.tvansweredby,R.id.tvansweredtime,R.id.tvanswerid,R.id.tvrealanswer};
        SimpleCursorAdapter simpleCursorAdapter1;
        simpleCursorAdapter1=new SimpleCursorAdapter(getActivity(),R.layout.answer_single,cursor,fromcolumns,tofeilds,0);

        answerlistview.setAdapter(simpleCursorAdapter1);



    }


   /*public void openandquerydetaileddb(){
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
                       // String question_id=c.getString(c.getColumnIndex("question_id"));


                        answerdb.setActualanswer(answer);
                        answerdb.setAnswer_id(answer_id);
                        answerdb.setAnsweredby(answeredby);
                        answerdb.setAnsweredtime(answeredtime);
                       // answerdb.setQuestion_id("3");
                        answerlistdb.add(answerdb);






                    }while (c.moveToNext());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void displayresultfromdb(){
        Answer_Adapter myansweradapter = new Answer_Adapter(getActivity(),R.layout.answer_single,answerlistdb);
        answerlistview.setAdapter(myansweradapter);
       // myansweradapter.notifyDataSetChanged();
    }*/
   public void openandqueryanswerdb(){
       try {

           DbHelper dbHelper=new DbHelper(getActivity());
           db=dbHelper.getWritableDatabase();
           //Cursor c=db.query(DbTableStrings.TABLE_NAME_ANSWER_MODEL,null,null,null,null,null,null);
           Cursor c1=db.rawQuery("SELECT * FROM "+DbTableStrings.TABLE_NAME_MY_QUESTIONS +"where "+DbTableStrings.QUESTION_ID +"= "+question_id,null);

           if(c1!=null){
               if(c1.moveToFirst()){
                   do{

                       String answer=c1.getString(c1.getColumnIndex("actualanswer"));
                       String answer_id=c1.getString(c1.getColumnIndex("answer_id"));
                       String answeredby=c1.getString(c1.getColumnIndex("answeredby"));
                       String answeredtime=c1.getString(c1.getColumnIndex("answeredtime"));
                       // String questionid=c.getString(c.getColumnIndex("questionid"));


                       answerdb.setActualanswer(answer);
                       answerdb.setAnswer_id(answer_id);
                       answerdb.setAnsweredby(answeredby);
                       answerdb.setAnsweredtime(answeredtime);
                       answerlistdb.add(answerdb);






                   }while (c1.moveToNext());
               }
           }

       } catch (Exception e) {
           e.printStackTrace();
       }
       db.close();

   }

    public void diplayanswerlistdb(){
        Answer_Adapter myansweradapter = new Answer_Adapter(getActivity(), R.layout.answer_single, answerlistdb);
        answerlistview.setAdapter(myansweradapter);
        myansweradapter.notifyDataSetChanged();

    }
}
