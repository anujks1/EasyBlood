package com.app.easyblood.DBHandlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.app.easyblood.DBClasses.DbHelper;
import com.app.easyblood.DBClasses.DbTableStrings;

/**
 * Created by archon on 25-01-2015.
 */
public class NotifsTableDbHandler {
    private static DbHelper dbHelper;
    private static SQLiteDatabase db;
    public  void addtonotifsdb(Context context, String QuestionIdnotifs,String answeridnotifs){
        try{
            dbHelper = new DbHelper((context.getApplicationContext()));
            db = dbHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(DbTableStrings.QUESTIONID_NOTIF, QuestionIdnotifs);
            contentValues.put(DbTableStrings.ANSWERID_NOTIF,answeridnotifs);


            db.insert(DbTableStrings.TABLE_NAME_NOTIFS_TABLE,null,contentValues);
            db.close();
        }
        catch (Exception e)
        {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
