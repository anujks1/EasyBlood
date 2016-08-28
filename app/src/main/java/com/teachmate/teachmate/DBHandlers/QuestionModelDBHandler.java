package com.teachmate.teachmate.DBHandlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.teachmate.teachmate.DBClasses.DbHelper;
import com.teachmate.teachmate.DBClasses.DbTableStrings;
import com.teachmate.teachmate.models.Question_Model;


/**
 * Created by ASreenivasa on 05-Jan-15.
 */
public class QuestionModelDBHandler {

    private static DbHelper dbHelper;
    private static SQLiteDatabase db;

    public  void InsertQuestionModel(Context context, Question_Model question_model){
        try{
            dbHelper = new DbHelper((context.getApplicationContext()));
            db = dbHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(DbTableStrings.USERNAME, question_model.username);
            contentValues.put(DbTableStrings.QUESTION, question_model.question);
            contentValues.put(DbTableStrings.IMAGE, question_model.image);
            contentValues.put(DbTableStrings.QUESTION_ID, question_model.question_id);
            contentValues.put(DbTableStrings.CATEGORY, question_model.Category);
            contentValues.put(DbTableStrings.ASKED_TIME, question_model.asked_time);


            db.insert(DbTableStrings.TABLE_NAME_QUESTION_MODEL,null,contentValues);
            db.close();
        }
        catch (Exception e)
        {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT);
        }
    }
    public void Insertintomyquestionstable(Context context,Question_Model question_model){
        try{
            dbHelper = new DbHelper((context.getApplicationContext()));
            db = dbHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(DbTableStrings.MYUSERNAME, question_model.username);
            contentValues.put(DbTableStrings.MYQUESTION, question_model.question);
            contentValues.put(DbTableStrings.MYIMAGE, question_model.image);
            contentValues.put(DbTableStrings.MYQUESTION_ID, question_model.question_id);
            contentValues.put(DbTableStrings.MYCATEGORY, question_model.Category);
            contentValues.put(DbTableStrings.MYASKED_TIME, question_model.asked_time);


            db.insert(DbTableStrings.TABLE_NAME_MY_QUESTIONS,null,contentValues);
        }
        catch (Exception e)
        {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}
