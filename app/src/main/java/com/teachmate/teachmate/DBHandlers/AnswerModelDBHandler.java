package com.teachmate.teachmate.DBHandlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.teachmate.teachmate.DBClasses.DbHelper;
import com.teachmate.teachmate.DBClasses.DbTableStrings;
import com.teachmate.teachmate.models.Answer_Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ASreenivasa on 05-Jan-15.
 */
public class AnswerModelDBHandler {

    private static DbHelper dbHelper;
    private static SQLiteDatabase db;

    public static void InsertAnswerModel(Context context, Answer_Model answer_model){
        try{
            ContentValues contentValues = new ContentValues();
            contentValues.put(DbTableStrings.ACTUAL_ANSWER, answer_model.actualanswer);
            contentValues.put(DbTableStrings.ANSWER_ID, answer_model.answer_id);
            contentValues.put(DbTableStrings.ANSWERED_BY, answer_model.answeredby);
            contentValues.put(DbTableStrings.ANSWERED_TIME, answer_model.answeredtime);
            contentValues.put(DbTableStrings.QUESTION_ID, answer_model.question_id);

            dbHelper = new DbHelper((context.getApplicationContext()));
            db = dbHelper.getWritableDatabase();
            db.insert(DbTableStrings.TABLE_NAME_ANSWER_MODEL,null,contentValues);
        }
        catch (Exception e)
        {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public  void InsertAnswerList(Context context, ArrayList<Answer_Model> answers){
        try{
            for(Answer_Model ans : answers){
                InsertAnswerModel(context,ans);
            }
        }
        catch (Exception e){
            Toast.makeText(context, e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    public static List<Answer_Model> GetAllAnswersByQuestionID(Context context,String questionID){
        try{
            dbHelper = new DbHelper(context.getApplicationContext());
            db = dbHelper.getWritableDatabase();

            Cursor c = db.rawQuery("Select * from " + DbTableStrings.TABLE_NAME_ANSWER_MODEL + " where "+ DbTableStrings.QUESTION_ID +" = '" + questionID + "'", null);

            List<Answer_Model> listOfAnswers = new ArrayList<Answer_Model>();

            Answer_Model answer_model;

            if (c != null ) {
                if  (c.moveToFirst()) {
                    do {
                        answer_model = new Answer_Model();
                        answer_model.question_id = c.getString(c.getColumnIndex(DbTableStrings.QUESTION_ID));
                        answer_model.answeredtime = c.getString(c.getColumnIndex(DbTableStrings.ANSWERED_TIME));
                        answer_model.answeredby = c.getString(c.getColumnIndex(DbTableStrings.ANSWERED_BY));
                        answer_model.answer_id = c.getString(c.getColumnIndex(DbTableStrings.ANSWER_ID));
                        answer_model.actualanswer = c.getString(c.getColumnIndex(DbTableStrings.ACTUAL_ANSWER));
                        listOfAnswers.add(answer_model);
                    }while (c.moveToNext());
                }
                return listOfAnswers;
            }
            return null;
        }
        catch(Exception e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}
