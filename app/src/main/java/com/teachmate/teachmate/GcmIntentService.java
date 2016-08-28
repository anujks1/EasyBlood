/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.teachmate.teachmate;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.teachmate.teachmate.Chat.ChatActivity;
import com.teachmate.teachmate.DBHandlers.ChatIdMapDBHandler;
import com.teachmate.teachmate.DBHandlers.ChatInfoDBHandler;
import com.teachmate.teachmate.models.ChatIdMap;
import com.teachmate.teachmate.models.ChatInfo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    public static final String TAG = "GCM Demo";

    String message;
    String type;
    String requestId;
    String username;
    String askedby;
    String repliedby;
    String replymessage;
    String userid_questions;
    String category;
    String questionmessage;
    String asked_time_questions;
    String imagepath;
    String questionid;
    String userprofession_questions;
    String userId;
    int intType;
    String responseId;

    String responseUserProfession;
    String responseUserProfilePath;

    String chatSenderId;
    String chatChatId;
    String chatSenderName;
    String chatMessage;
    String chatMessageTime;

    String verifyUserId;

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM will be
             * extended in the future with new message types, just ignore any message types you're
             * not interested in, or that you don't recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " + extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                type = extras.getString("Type");
                intType = Integer.parseInt(type);
                switch (intType) {
                    case 1:
                        verifyUserId = extras.getString("UserId");
                        break;
                    case 2:

                        askedby = extras.getString("AskedBy");
                        category = extras.getString("Category");
                        repliedby = extras.getString("RepliedBy");
                        replymessage = extras.getString("ReplyMessage");
                        userid_questions = extras.getString("UserId");
                        questionmessage = extras.getString("QuestionMessage");
                        asked_time_questions = extras.getString("AskedTime");
                        imagepath = extras.getString("UserProfilePhotoServerPath");
                        questionid = extras.getString("QuestionId");
                        userprofession_questions = extras.getString("UserProfession");
                        break;
                    case 3: //New Request Notification
                        message = extras.getString("message");
                        requestId = extras.getString("requestId");
                        username = extras.getString("userName");
                        break;
                    case 4://New Response Notification
                        message = extras.getString("ResponseMessage");
                        username = extras.getString("ResponseUserName");
                        requestId = extras.getString("RequestId");
                        userId = extras.getString("ResponseUserId");
                        responseId = extras.getString("ResponseId");
                        responseUserProfession = extras.getString("Profession");
                        responseUserProfilePath = extras.getString("ProfilePhotoUrl");
                        break;
                    case 5:
                        chatChatId = extras.getString("ChatId");
                        chatMessage = extras.getString("Message");
                        chatMessageTime = extras.getString("SentOn");
                        chatSenderId = extras.getString("SenderId");
                        chatSenderName = extras.getString("UserName");
                        break;
                }

                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.
                sendNotification("Received: " + extras.toString());
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo_notif);


        if (intType == 2) {
            Intent question = new Intent(this, MainActivity.class);
            question.putExtra("type", "Replies");
            question.putExtra("askedby", askedby);
            question.putExtra("userid_questions", userid_questions);
            question.putExtra("questionmessage", questionmessage);
            question.putExtra("asked_time_questions", asked_time_questions);
            question.putExtra("imagepath", imagepath);
            question.putExtra("Category", category);
            question.putExtra("questionid", questionid);
            question.putExtra("userprofession_questions", userprofession_questions);

            PendingIntent contentIntent = PendingIntent.getActivity(this, Integer.parseInt(questionid),
                    question, PendingIntent.FLAG_ONE_SHOT);


            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.logo_notif)
                            .setLargeIcon(bitmap)
                            .setContentTitle("New Answer")
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(repliedby + " says " + replymessage))
                            .setContentText(repliedby + " says " + replymessage)
                            .setAutoCancel(true);

            mBuilder.setContentIntent(contentIntent);
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        } else if (intType == 3) {
            Intent requestIntent = new Intent(this, MainActivity.class);
            requestIntent.putExtra("type", "request");
            requestIntent.putExtra("NotificationRequestId", requestId);

            PendingIntent contentIntent = PendingIntent.getActivity(this, Integer.parseInt(requestId),
                    requestIntent, PendingIntent.FLAG_ONE_SHOT);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.logo_notif)
                            .setLargeIcon(bitmap)
                            .setContentTitle("New Request")
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(username + " : " + message))
                            .setContentText(username + " : " + message)
                            .setAutoCancel(true);

            mBuilder.setContentIntent(contentIntent);
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        } else if (intType == 4) {
            Intent responseIntent = new Intent(this, MainActivity.class);
            responseIntent.putExtra("type", "response");
            responseIntent.putExtra("NotificationResponseId", responseId);
            responseIntent.putExtra("NotificationRequestId", requestId);
            responseIntent.putExtra("NotificationResponseUserId", userId);
            responseIntent.putExtra("NotificationResponseUserName", username);
            responseIntent.putExtra("NotificationResponseMessage", message);
            responseIntent.putExtra("NotificationResponseUserProfession", responseUserProfession);
            responseIntent.putExtra("NotificationResponseUserProfilePhotoServerPath", responseUserProfilePath);

            PendingIntent contentIntent = PendingIntent.getActivity(this, Integer.parseInt(responseId),
                    responseIntent, PendingIntent.FLAG_ONE_SHOT);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.logo_notif)
                            .setContentTitle("New Response")
                            .setLargeIcon(bitmap)
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(username + " : " + message))
                            .setContentText(username + " : " + message)
                            .setAutoCancel(true);

            mBuilder.setContentIntent(contentIntent);
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        } else if (intType == 5) {
            Intent chatIntent = new Intent(getApplicationContext(), ChatActivity.class);
            chatIntent.putExtra("ChatId", chatChatId);
            chatIntent.putExtra("Message", chatMessage);
            chatIntent.putExtra("SentOn", chatMessageTime);
            chatIntent.putExtra("SenderId", chatSenderId);
            chatIntent.putExtra("UserName", chatSenderName);
            chatIntent.putExtra("received", true);
            ChatIdMap chatIdMap = new ChatIdMap();
            chatIdMap.chatId = chatChatId;
            chatIdMap.userId = chatSenderId;
            chatIdMap.userName = chatSenderName;
            ChatIdMapDBHandler.InsertChatIdMap(getApplicationContext(), chatIdMap);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    getApplicationContext(),
                    Integer.parseInt(chatChatId),
                    chatIntent,
                    PendingIntent.FLAG_ONE_SHOT);


            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String time = dateFormat.format(date);
            time = time.substring(11, time.lastIndexOf(':'));
            ChatInfo newMessage = new ChatInfo();
            newMessage.setMessage(chatMessage);
            newMessage.setSentBy(false);
            newMessage.setTimeStamp(time);
            newMessage.setChatId(chatChatId);

            ChatInfoDBHandler.InsertChatInfo(getApplicationContext(), newMessage);

            if (TempDataClass.alreadyAdded == true) {
                TempDataClass.alreadyAdded = false;
                return;
            }

            Notification mBuilder =
                    new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(R.drawable.logo_notif)
                            .setLargeIcon(bitmap)
                            .setContentTitle("Chat Notification")
                            .setContentIntent(pendingIntent)
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(chatSenderName + ":" + chatMessage))
                            .setContentText(chatSenderName + ":" + chatMessage)
                            .setAutoCancel(true)
                            .build();


            mNotificationManager =
                    (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder);
        } else {

        }
    }
}
