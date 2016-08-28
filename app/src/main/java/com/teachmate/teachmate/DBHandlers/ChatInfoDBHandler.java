
package com.teachmate.teachmate.DBHandlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.teachmate.teachmate.DBClasses.DbHelper;
import com.teachmate.teachmate.DBClasses.DbTableStrings;
import com.teachmate.teachmate.models.ChatInfo;

import java.util.ArrayList;
import java.util.List;

public class ChatInfoDBHandler {

    private static DbHelper       dbHelper;
    private static SQLiteDatabase db;

    public static void InsertChatInfo(Context context, ChatInfo chatInfo) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DbTableStrings.MESSAGE, chatInfo.message);
            contentValues.put(DbTableStrings.SENTBY, chatInfo.sentBy);
            contentValues.put(DbTableStrings.TIMESTAMP, chatInfo.timeStamp);
            contentValues.put(DbTableStrings.CHATID, chatInfo.chatId);

            dbHelper = new DbHelper((context.getApplicationContext()));
            db = dbHelper.getWritableDatabase();
            db.insert(DbTableStrings.TABLE_NAME_CHAT_INFO, null, contentValues);
        } catch (Exception e) {
            Log.e("ChatInfoDBHandler", e.getMessage());
        } finally {
            db.close();
        }
    }

    public static List<ChatInfo> GetPreviousChat(Context context, String chatID) {

        try {
            dbHelper = new DbHelper(context.getApplicationContext());
            db = dbHelper.getWritableDatabase();

            Cursor c = db.rawQuery("Select * from " + DbTableStrings.TABLE_NAME_CHAT_INFO
                    + " where " + DbTableStrings.CHATID + " = '" + chatID + "'", null);

            List<ChatInfo> listOfChats = new ArrayList<ChatInfo>();

            ChatInfo chat;

            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        chat = new ChatInfo();
                        chat.chatId = c.getString(c.getColumnIndex(DbTableStrings.CHATID));
                        chat.message = c.getString(c.getColumnIndex(DbTableStrings.MESSAGE));
                        chat.sentBy = c.getInt(c.getColumnIndex(DbTableStrings.SENTBY)) > 0;
                        chat.timeStamp = c.getString(c.getColumnIndex(DbTableStrings.TIMESTAMP));
                        listOfChats.add(chat);
                    } while (c.moveToNext());
                }
                return listOfChats;
            }
            return null;
        } catch (Exception e) {
            Log.e("ChatInfoDBHandler", e.getMessage());
            return null;
        } finally {
            db.close();
        }
    }
}
