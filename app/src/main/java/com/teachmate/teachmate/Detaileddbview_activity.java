package com.teachmate.teachmate;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.teachmate.teachmate.DBClasses.DbHelper;
import com.teachmate.teachmate.DBClasses.DbTableStrings;
import com.teachmate.teachmate.models.Answer_Model;
import com.teachmate.teachmate.models.Question_Model;

import java.util.ArrayList;


public class Detaileddbview_activity extends ActionBarActivity {
    ListView answerlistview;
    public String question_id;
    ArrayList<Answer_Model> answerlistdb;
    TextView tvusernamedb,tvquestiondb,tvaskedtimedb,tvquestion_iddb;
    public ArrayList<Answer_Model> answerlist;
    Question_Model question1=new Question_Model();
    Answer_Model answerdb=new Answer_Model();
    private static DbHelper dbHelper;
    private static SQLiteDatabase db;
    ImageView imageViewdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detaileddb);
        tvusernamedb=(TextView)findViewById(R.id.tvusernamedb);
        tvquestiondb=(TextView)findViewById(R.id.tvquestiondb);
        tvaskedtimedb=(TextView)findViewById(R.id.tvaskedtimedb);
        tvquestion_iddb=(TextView)findViewById(R.id.tvquestion_iddb);
        answerlistview=(ListView)findViewById(R.id.listViewdbans);
        //answerlistdb=new ArrayList<>();

       // String img=getIntent().getStringExtra("imagedb");
        String l=getIntent().getStringExtra("cat");
       question_id=getIntent().getStringExtra("question_iddb");
        tvquestiondb.setText(getIntent().getStringExtra("questiondb"));
        tvaskedtimedb.setText(getIntent().getStringExtra("asked_timedb"));
        tvusernamedb.setText(getIntent().getStringExtra("usernamedb"));
        tvquestion_iddb.setText(getIntent().getStringExtra("question_iddb"));
        populateanswerlistfromdb();
        db.close();
       // openandqueryanswerdb();
       // diplayanswerlistdb();



    }
    public void populateanswerlistfromdb(){
        dbHelper = new DbHelper(this);
        db = dbHelper.getWritableDatabase();
        Cursor cursor= null;
        try {
            cursor = db.rawQuery("SELECT * FROM "+ DbTableStrings.TABLE_NAME_ANSWER_MODEL+" WHERE "+DbTableStrings.QUESTION_ID+" = "+Integer.parseInt(question_id),null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] fromcolumns=new String[]{DbTableStrings.ANSWERED_BY,DbTableStrings.ANSWERED_TIME,DbTableStrings.ANSWER_ID,DbTableStrings.ACTUAL_ANSWER};
        int[] tofeilds=new int[]{R.id.tvansweredby,R.id.tvansweredtime,R.id.tvanswerid,R.id.tvrealanswer};
        SimpleCursorAdapter simpleCursorAdapter1;
        simpleCursorAdapter1=new SimpleCursorAdapter(getBaseContext(),R.layout.answer_single,cursor,fromcolumns,tofeilds,0);

        answerlistview.setAdapter(simpleCursorAdapter1);



    }
    /*public void openandqueryanswerdb(){
        try {

            DbHelper dbHelper=new DbHelper(getApplicationContext());
            db=dbHelper.getWritableDatabase();
            //Cursor c=db.query(DbTableStrings.TABLE_NAME_ANSWER_MODEL,null,null,null,null,null,null);
            Cursor c1=db.rawQuery("SELECT * FROM "+DbTableStrings.TABLE_NAME_ANSWER_MODEL +" where "+DbTableStrings.QUESTION_ID +" = "+question_id,null);

            if(c1!=null){
                if(c1.moveToFirst()){
                    do{

                        String answer=c1.getString(c1.getColumnIndex("actual_answer"));
                        String answer_id=c1.getString(c1.getColumnIndex("answer_id"));
                        String answeredby=c1.getString(c1.getColumnIndex("answered_by"));
                        String answeredtime=c1.getString(c1.getColumnIndex("answered_time"));
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
        Answer_Adapter myansweradapter = new Answer_Adapter(getApplicationContext(), R.layout.answer_single, answerlistdb);
        answerlistview.setAdapter(myansweradapter);
        myansweradapter.notifyDataSetChanged();

    }*/



}
