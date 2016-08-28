
package com.teachmate.teachmate.DBHandlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.teachmate.teachmate.DBClasses.DbHelper;
import com.teachmate.teachmate.DBClasses.DbTableStrings;
import com.teachmate.teachmate.models.ChatIdMap;

import java.util.ArrayList;
import java.util.List;

public class ChatIdMapDBHandler {

    private static DbHelper       dbHelper;
    private static SQLiteDatabase db;

    public static void InsertChatIdMap(Context context, ChatIdMap chatIdMap) {
        try {
            if (CheckUserIdAndReturnChatId(context, chatIdMap.userId) == 0) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(DbTableStrings.CHATID, chatIdMap.chatId);
                contentValues.put(DbTableStrings.USERID, chatIdMap.userId);
                contentValues.put(DbTableStrings.USERNAME, chatIdMap.userName);

                dbHelper = new DbHelper((context.getApplicationContext()));
                db = dbHelper.getWritableDatabase();
                db.insert(DbTableStrings.TABLE_NAME_CHAT_ID_MAPPING, null, contentValues);
            }
        } catch (Exception e) {
            Log.e("ChatIdMapDBHandler", e.getMessage());
        } finally {
            db.close();
        }
    }

    public static int CheckUserIdAndReturnChatId(Context context, String userID) {

        try {
            dbHelper = new DbHelper(context.getApplicationContext());
            db = dbHelper.getWritableDatabase();

            Cursor c = db.rawQuery("Select * from " + DbTableStrings.TABLE_NAME_CHAT_ID_MAPPING
                    + " where " + DbTableStrings.USERID + " = '" + userID + "'", null);
            if (c != null) {
                if (c.moveToFirst()) {
                    int val = Integer
                            .parseInt(c.getString(c.getColumnIndex(DbTableStrings.CHATID)));
                    return val;
                }
            }
            else {
                return 0;
            }

        } catch (Exception e) {
            Log.e("ChatIdMapDBHandler", e.getMessage());
            return 0;
        } finally {
            db.close();
        }
        return 0;
    }

    public static List<ChatIdMap> getPreviousChatRecords(Context context) {
        List<ChatIdMap> previousChats = new ArrayList<ChatIdMap>();
        try {
            dbHelper = new DbHelper(context.getApplicationContext());
            db = dbHelper.getWritableDatabase();

            Cursor c = db.rawQuery("Select * from " + DbTableStrings.TABLE_NAME_CHAT_ID_MAPPING,
                    null);
            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        ChatIdMap chatIdMap = new ChatIdMap();
                        chatIdMap.chatId = c.getString(c.getColumnIndex(DbTableStrings.CHATID));
                        chatIdMap.userId = c.getString(c.getColumnIndex(DbTableStrings.USERID));
                        chatIdMap.userName = c.getString(c.getColumnIndex(DbTableStrings.USERNAME));
                        previousChats.add(chatIdMap);
                    } while (c.moveToNext());
                }
            }
        } catch (Exception e) {
            Log.e("ChatIdMapDBHandler", e.getMessage());
        } finally {
            db.close();
        }
        return previousChats;
    }

    public static String chattingWith(Context context, String chatId) {

        try {
            dbHelper = new DbHelper(context.getApplicationContext());
            db = dbHelper.getWritableDatabase();

            Cursor c = db.rawQuery("Select * from " + DbTableStrings.TABLE_NAME_CHAT_ID_MAPPING
                    + " where " + DbTableStrings.CHATID + " = '" + chatId + "'", null);
            if (c != null) {
                if (c.moveToFirst()) {
                    String name = c.getString(c.getColumnIndex(DbTableStrings.USERNAME));
                    return name;
                }
            }
            else {
                return null;
            }

        } catch (Exception e) {
            Log.e("ChatIdMapDBHandler", e.getMessage());
            return null;
        } finally {
            db.close();
        }
        return null;
    }
}
