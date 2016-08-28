package com.teachmate.teachmate.DBHandlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.teachmate.teachmate.DBClasses.DbHelper;
import com.teachmate.teachmate.DBClasses.DbTableStrings;
import com.teachmate.teachmate.TempDataClass;
import com.teachmate.teachmate.models.Requests;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by ASreenivasa on 05-Jan-15.
 */
public class RequestsDBHandler {

    private static DbHelper dbHelper;
    private static SQLiteDatabase db;

    public static void InsertRequests(Context context,Requests requests){
        try{
            ContentValues contentValues = new ContentValues();
            contentValues.put(DbTableStrings.REQUEST_ID, requests.RequestID);
            contentValues.put(DbTableStrings.REQUEST_EUSER_ID, requests.RequesteUserId);
            contentValues.put(DbTableStrings.REQUEST_USERNAME, requests.RequestUserName);
            contentValues.put(DbTableStrings.REQUEST_STRING, requests.RequestString);
            contentValues.put(DbTableStrings.REQUEST_USER_PROFESSION, requests.RequestUserProfession);
            contentValues.put(DbTableStrings.REQUEST_USER_PROFILE_PHOTO_SERVER_PATH, requests.RequestUserProfilePhotoServerPath);
            contentValues.put(DbTableStrings.REQUEST_TIME,requests.RequestTime);
            contentValues.put(DbTableStrings.REQUEST_DAY_OF_THE_YEAR,requests.requestDayOfTheYear);
            contentValues.put(DbTableStrings.REQUEST_YEAR,requests.requestYear);

            dbHelper = new DbHelper((context.getApplicationContext()));
            db = dbHelper.getWritableDatabase();
            db.insert(DbTableStrings.TABLE_NAME_REQUESTS,null,contentValues);
        }
        catch (Exception e)
        {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static Requests[] GetAllRequests(Context context)
    {
        dbHelper = new DbHelper(context.getApplicationContext());
        db = dbHelper.getWritableDatabase();

        Cursor c = db.rawQuery("Select * from " + DbTableStrings.TABLE_NAME_REQUESTS, null);

        int count = c.getCount();

        Requests[] requests = new Requests[count];

        if (c.getCount() != 0) {
            if(c.getCount() != -1) {
                int i = 0;
                if (c.moveToLast()) {
                    do {
                        requests[i] = new Requests();
                        requests[i].RequesteUserId = c.getString(c.getColumnIndex(DbTableStrings.REQUEST_EUSER_ID));
                        requests[i].RequestID = c.getString(c.getColumnIndex(DbTableStrings.REQUEST_ID));
                        requests[i].RequestString = c.getString(c.getColumnIndex(DbTableStrings.REQUEST_STRING));
                        requests[i].RequestTime = c.getString(c.getColumnIndex(DbTableStrings.REQUEST_TIME));
                        requests[i].RequestUserName = c.getString(c.getColumnIndex(DbTableStrings.REQUEST_USERNAME));
                        requests[i].RequestUserProfession = c.getString(c.getColumnIndex(DbTableStrings.REQUEST_USER_PROFESSION));
                        requests[i].RequestUserProfilePhotoServerPath = TempDataClass.profilePhotoServerPath;
                        i++;
                    } while (c.moveToPrevious());
                }
                return requests;
            }
        }
        return null;
    }

    public static void DeleteRequest(Context context,int requestID){
        try{

            dbHelper = new DbHelper((context.getApplicationContext()));
            db = dbHelper.getWritableDatabase();

            db.delete(DbTableStrings.TABLE_NAME_REQUESTS, DbTableStrings.REQUEST_ID + "=" + requestID, null);
        }
        catch (Exception e)
        {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static Requests GetRequest(Context context, String requestID)
    {
        try{
            dbHelper = new DbHelper(context.getApplicationContext());
            db = dbHelper.getWritableDatabase();

            Cursor c = db.rawQuery("Select * from " + DbTableStrings.TABLE_NAME_REQUESTS+ " where "+ DbTableStrings.REQUEST_ID+" = '" + requestID + "'", null);

            Requests request = new Requests();

            if  (c.moveToFirst()) {
                request.RequesteUserId = c.getString(c.getColumnIndex(DbTableStrings.REQUEST_EUSER_ID));
                request.RequestID = c.getString(c.getColumnIndex(DbTableStrings.REQUEST_ID));
                request.RequestString = c.getString(c.getColumnIndex(DbTableStrings.REQUEST_STRING));
                request.RequestTime = c.getString(c.getColumnIndex(DbTableStrings.REQUEST_TIME));
                request.RequestUserName = c.getString(c.getColumnIndex(DbTableStrings.REQUEST_USERNAME));
                request.RequestUserProfession = c.getString(c.getColumnIndex(DbTableStrings.REQUEST_USER_PROFESSION));
                request.RequestUserProfilePhotoServerPath = c.getString(c.getColumnIndex(DbTableStrings.REQUEST_USER_PROFILE_PHOTO_SERVER_PATH));
                return request;
            }
            return null;
        }catch(Exception ex){
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public static String[] GetAllRequestsBeforeThreeDays(Context context)
    {
        dbHelper = new DbHelper(context.getApplicationContext());
        db = dbHelper.getWritableDatabase();

        Calendar calendar = Calendar.getInstance();
        int cYear = calendar.get(Calendar.YEAR);
        int cDayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        cDayOfYear -= 3;

        Cursor c = db.rawQuery("Select * from " + DbTableStrings.TABLE_NAME_REQUESTS, null);

        Requests request = new Requests();
        List<Requests> listOfRequests = new ArrayList<Requests>();
        if (c != null ) {
            if  (c.moveToFirst()) {
                do {
                    int reqDayOfYear = c.getInt(c.getColumnIndex(DbTableStrings.REQUEST_DAY_OF_THE_YEAR));
                    int reqYear = c.getInt(c.getColumnIndex(DbTableStrings.REQUEST_YEAR));
                    if(reqDayOfYear <=cDayOfYear && reqYear == cYear) {
                        request = new Requests();
                        request.RequesteUserId = c.getString(c.getColumnIndex(DbTableStrings.REQUEST_EUSER_ID));
                        request.RequestID = c.getString(c.getColumnIndex(DbTableStrings.REQUEST_ID));
                        request.RequestString = c.getString(c.getColumnIndex(DbTableStrings.REQUEST_STRING));
                        request.RequestTime = c.getString(c.getColumnIndex(DbTableStrings.REQUEST_TIME));
                        request.RequestUserName = c.getString(c.getColumnIndex(DbTableStrings.REQUEST_USERNAME));
                        request.RequestUserProfession = c.getString(c.getColumnIndex(DbTableStrings.REQUEST_USER_PROFESSION));
                        request.RequestUserProfilePhotoServerPath = c.getString(c.getColumnIndex(DbTableStrings.REQUEST_USER_PROFILE_PHOTO_SERVER_PATH));
                        request.requestYear = c.getInt(c.getColumnIndex(DbTableStrings.REQUEST_YEAR));
                        request.requestDayOfTheYear = c.getInt(c.getColumnIndex(DbTableStrings.REQUEST_DAY_OF_THE_YEAR));
                        listOfRequests.add(request);
                    }
                }while (c.moveToNext());
            }
            if(listOfRequests.size() > 0) {
                int i = 0;
                String[] requestIds = new String[listOfRequests.size()];
                for (Requests request1 : listOfRequests) {
                    requestIds[i] = request1.RequestID;
                    i++;
                }
                return requestIds;
            }
            else{
                return null;
            }
        }
        return null;
    }
}
