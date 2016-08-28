package com.teachmate.teachmate.DBHandlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.teachmate.teachmate.DBClasses.DbHelper;
import com.teachmate.teachmate.DBClasses.DbTableStrings;
import com.teachmate.teachmate.models.DeviceInfoModel;

/**
 * Created by ASreenivasa on 05-Jan-15.
 */
public class DeviceInfoDBHandler {

    private static DbHelper dbHelper;
    private static SQLiteDatabase db;

    public static void InsertDeviceInfo(Context context, DeviceInfoModel deviceInfo) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DbTableStrings.KEY, deviceInfo.Key);
            contentValues.put(DbTableStrings.VALUE, deviceInfo.Value);

            dbHelper = new DbHelper(context);
            db = dbHelper.getWritableDatabase();
            db.insert(DbTableStrings.TABLE_NAME_DEVICE_INFO, null, contentValues);
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static String GetValueForKey(Context context, String key){

        try {
            dbHelper = new DbHelper(context.getApplicationContext());
            db = dbHelper.getWritableDatabase();


            Cursor c = db.rawQuery("Select * from " + DbTableStrings.TABLE_NAME_DEVICE_INFO + " where "+ DbTableStrings.KEY +" = '" + key + "'", null);
            if (c.moveToFirst()) {

                String val = c.getString(c.getColumnIndex(DbTableStrings.VALUE));
                return val;
            }
            else{
                return null;
            }
        }
        catch(Exception e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public static String CheckKeyAndReturnValue(Context context, String key){

        try {
            dbHelper = new DbHelper(context.getApplicationContext());
            db = dbHelper.getWritableDatabase();


            Cursor c = db.rawQuery("Select * from " + DbTableStrings.TABLE_NAME_DEVICE_INFO + " where "+ DbTableStrings.KEY +" = '" + key + "'", null);
            if (c.moveToFirst()) {

                String val = c.getString(c.getColumnIndex(DbTableStrings.VALUE));
                return val;
            }
            else{
                return null;
            }

        }
        catch(Exception e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public static boolean UpdateValueForExistingKey(Context context, String key){

        try {
            dbHelper = new DbHelper(context.getApplicationContext());
            db = dbHelper.getWritableDatabase();

            DeviceInfoModel deviceInfo = new DeviceInfoModel();

            Cursor c = db.rawQuery("Select * from " + DbTableStrings.TABLE_NAME_DEVICE_INFO + " where "+ DbTableStrings.KEY +" = '" + key + "'", null);
            if (c != null ) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(DbTableStrings.KEY, deviceInfo.Key);
                contentValues.put(DbTableStrings.VALUE, deviceInfo.Value);

                return db.update(DbTableStrings.TABLE_NAME_DEVICE_INFO,contentValues,DbTableStrings.KEY + "=" + key, null)>0;
            }
            else{
                return false;
            }

        }
        catch(Exception e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
