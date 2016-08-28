package com.teachmate.teachmate.DBClasses;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper{
    public DbHelper(Context context) {
        super(context, DatabaseHelperMeta.DB_NAME, null, DatabaseHelperMeta.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.v("creating " , "DB");
        db.execSQL(Schema.CREATE_TABLE_DEVICE_INFO);
        db.execSQL(Schema.CREATE_TABLE_Profile);
        db.execSQL(Schema.CREATE_TABLE_REQUESTS);
        db.execSQL(Schema.CREATE_TABLE_QUESTION_MODEL);
        db.execSQL(Schema.CREATE_TABLE_ANSWER_MODEL);
        db.execSQL(Schema.CREATE_TABLE_MY_QUESTIONS);
        db.execSQL(Schema.CREATE_TABLE_CHAT_INFO);
        db.execSQL(Schema.CREATE_TABLE_CHAT_ID_MAPPING);
        db.execSQL(Schema.CREATE_TABLE_NS_ITEM_MODEL);
        db.execSQL(Schema.INSERT_NOTIFICATION_CONSTANT1);
        db.execSQL(Schema.INSERT_NOTIFICATION_CONSTANT2);
        db.execSQL(Schema.INSERT_NOTIFICATION_CONSTANT3);
        db.execSQL(Schema.INSERT_NOTIFICATION_CONSTANT4);
        Log.v("created " , "DB");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
