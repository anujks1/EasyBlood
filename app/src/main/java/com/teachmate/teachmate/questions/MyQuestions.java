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
import com.teachmate.teachmate.clicked;
import com.teachmate.teachmate.models.Answer_Model;
import com.teachmate.teachmate.models.Question_Model;

import java.util.ArrayList;

/**
 * Created by archon on 25-01-2015.
 */
public class MyQuestions extends Fragment {

        public ListView list;
        public String username,question,question_id,category,image,asked_time;
        EditText etsearch;
        public ArrayList<Question_Model> questionlist2;
        public ArrayList<Answer_Model> answerlist;
        ImageView imageView;
        Question_Model question1=new Question_Model();
        Answer_Model answer1=new Answer_Model();
    private static DbHelper dbHelper;
    private static SQLiteDatabase db;
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentActivity fragmentActivity=(FragmentActivity)super.getActivity();
        RelativeLayout relativeLayout=(RelativeLayout)inflater.inflate(R.layout.dblistview,container,false);
        imageView = (ImageView)relativeLayout.findViewById(R.id.imageView);
        list = (ListView)relativeLayout.findViewById(R.id.listViewdb);
        questionlist2=new ArrayList<Question_Model>();
        //openandquerydatabase();
        populatemyquestionsfromdb();

     //   displayresultlist();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String user=((TextView)view.findViewById(R.id.tvusername_myquestions)).getText().toString();
                String category=((TextView)view.findViewById(R.id.tvcategory_myquestions)).getText().toString();
                String qid=((TextView)view.findViewById(R.id.tvquestion_id_myquestions)).getText().toString();
                //String url=((TextView)view.findViewById(R.id.)).getText().toString();
                String quest=((TextView)view.findViewById(R.id.tvquestion_myquestions)).getText().toString();
                String asked=((TextView)view.findViewById(R.id.tvaskedtime_myquestions)).getText().toString();
              //  String cat=((TextView)view.findViewById(R.id.tv)).getText().toString();





                Bundle data=new Bundle();
                data.putString("username", user);
                data.putString("question", quest);
                data.putString("asked_time", asked);
                data.putString("question_id", qid);
                data.putString("category",category);
                data.putString("image", "image url");
                data.putString("myquestions","myquestions");


                Fragment clicked_fragment=new clicked();
                clicked_fragment.setArguments(data);

                Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.container);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                TempDataClass.fragmentStack.lastElement().onPause();
                TempDataClass.fragmentStack.push(currentFragment);
                fragmentManager.beginTransaction()
                        .replace(R.id.container, clicked_fragment)
                        .commit();

              /*  Intent data =new Intent(getActivity().getApplicationContext(), clicked_activity.class);
                data.putExtra("username", user);
                data.putExtra("question", quest);
                data.putExtra("asked_time", asked);
                data.putExtra("question_id", qid);
                data.putExtra("cat","cat");
                data.putExtra("image", "image url");
                startActivity(data);*/
            }
        });


        return relativeLayout;

    }
    public void populatemyquestionsfromdb(){

        dbHelper = new DbHelper(getActivity());
        db = dbHelper.getWritableDatabase();
        Cursor cursor=db.rawQuery("SELECT * FROM "+ DbTableStrings.TABLE_NAME_MY_QUESTIONS+" ORDER BY "+DbTableStrings.MYQUESTION_ID+" DESC ",null);
        String[] fromcolums=new String[]{DbTableStrings.MYUSERNAME,DbTableStrings.MYQUESTION,DbTableStrings.MYQUESTION_ID, DbTableStrings.MYASKED_TIME,DbTableStrings.MYCATEGORY};
        int[] tofeilds=new int[]{R.id.tvusername_myquestions,R.id.tvquestion_myquestions,R.id.tvquestion_id_myquestions,R.id.tvaskedtime_myquestions,R.id.tvcategory_myquestions};
        SimpleCursorAdapter simpleCursorAdapter;
        simpleCursorAdapter=new SimpleCursorAdapter(getActivity(),R.layout.singlerow_myquestions,cursor,fromcolums,tofeilds,0);
        list.setAdapter(simpleCursorAdapter);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(FragmentTitles.MY_QUESTIONS);
        //TODO
    }

    public void openandquerydatabase(){
        try {

            DbHelper dbHelper=new DbHelper(getActivity());
            db=dbHelper.getWritableDatabase();
            Cursor c=db.query(DbTableStrings.TABLE_NAME_MY_QUESTIONS,null,null,null,null,null,null);

            if(c!=null){
                if(c.moveToFirst()){
                    do{

                         username=c.getString(c.getColumnIndex("myusername"));
                         question=c.getString(c.getColumnIndex("myquestion"));
                         question_id=c.getString(c.getColumnIndex("myquestion_id"));
                         category=c.getString(c.getColumnIndex("mycategory"));
                         image=c.getString(c.getColumnIndex("myimage"));
                         asked_time=c.getString(c.getColumnIndex("myasked_time"));

                        question1.setUsername(username);
                        question1.setImage(image);
                        question1.setQuestion(question);
                        question1.setQuestion_id(question_id);
                        question1.setAsked_time(asked_time);
                        question1.setCategory(category);

                        questionlist2.add(question1);




                    }while (c.moveToNext());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();

        /*Bundle data=new Bundle();
        data.putString("usernamedb", username);
        data.putString("questiondb", qusetion);
        data.putString("asked_timedb", asked_time);
        data.putString("question_iddb", question_id);
        data.putString("imagedb", image);*/
    }

    public void displayresultlist(){
        MyQuestions_Adapter myQuestions_adapter = new MyQuestions_Adapter(getActivity().getApplicationContext(), R.layout.singlerow_myquestions, questionlist2);
        myQuestions_adapter.addAll(questionlist2);
        list.setAdapter(myQuestions_adapter);

    }

   /* public void openandquerydetaileddb(){
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

    }

    public void displayresultdetaileddb(){
        Answer_Adapter myansweradapter = new Answer_Adapter(getActivity().getApplicationContext(), R.layout.answer_single, answerlist);
        list.setAdapter(myansweradapter);
        myansweradapter.notifyDataSetChanged();

    }*/

}
