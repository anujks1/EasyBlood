
package com.teachmate.teachmate.Responses;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.teachmate.teachmate.Chat.ChatActivity;
import com.teachmate.teachmate.DBHandlers.ChatIdMapDBHandler;
import com.teachmate.teachmate.DBHandlers.RequestsDBHandler;
import com.teachmate.teachmate.DBHandlers.UserModelDBHandler;
import com.teachmate.teachmate.R;
import com.teachmate.teachmate.TempDataClass;
import com.teachmate.teachmate.models.ChatIdMap;
import com.teachmate.teachmate.models.Requests;
import com.teachmate.teachmate.models.Responses;
import com.teachmate.teachmate.models.UserModel;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ResponseDisplayActivity extends Fragment {

    Responses currentResponse;
    Requests  currentRequest;

    Button    acceptResponse;

    String    notificationRequestId;

    TextView  requestString;

    TextView  responseUserName;
    TextView  responseUserProfession;
    TextView  responseString;

    ImageView profilePhoto;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentActivity activity = (FragmentActivity) super.getActivity();
        RelativeLayout layout = (RelativeLayout) inflater.inflate(
                R.layout.activity_response_display, container, false);

        acceptResponse = (Button) layout.findViewById(R.id.buttonAccept);

        profilePhoto = (ImageView) layout.findViewById(R.id.imageViewUserProfilePhoto);

        requestString = (TextView) layout.findViewById(R.id.textViewResponseRequestString);
        responseUserName = (TextView) layout.findViewById(R.id.textViewResponseDisplayUserName);
        responseUserProfession = (TextView) layout
                .findViewById(R.id.textViewResponseDisplayUserProfession);
        responseString = (TextView) layout.findViewById(R.id.textViewResponseDisplayString);

        currentResponse = new Responses();
        Bundle args = getArguments();

        try {
            notificationRequestId = args.getString("NotificationResponseId");
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
        if (notificationRequestId == null) {
            currentRequest = new Requests();

            currentRequest.RequestID = args.getString("RequestId");
            currentRequest.RequestString = args.getString("RequestString");
            currentRequest.RequestTime = args.getString("RequestTime");

            currentResponse.RequestId = args.getString("RequestId");
            currentResponse.ResponseId = args.getString("ResponseId");
            currentResponse.ResponseString = args.getString("ResponseString");
            currentResponse.ResponseTime = args.getString("ResponseTime");
            currentResponse.ResponseUserId = args.getString("ResponseUserId");
            currentResponse.ResponseUserName = args.getString("ResponseUserName");
            currentResponse.ResponseUserProfession = args.getString("ResponseUserProfession");
            currentResponse.ResponseUserProfilePhotoServerPath = args
                    .getString("ResponseUserProfilePhotoServerPath");

            requestString.setText(currentRequest.RequestString);
            responseUserName.setText(currentResponse.ResponseUserName);
            responseUserProfession.setText(currentResponse.ResponseUserProfession);
            responseString.setText(currentResponse.ResponseString);
        }
        else {
            UserModel user = UserModelDBHandler.ReturnValue(getActivity().getApplicationContext());
            TempDataClass.serverUserId = user.ServerUserId;

            currentResponse.ResponseId = args.getString("NotificationResponseId");
            currentResponse.RequestId = args.getString("NotificationRequestId");
            currentResponse.ResponseUserId = args.getString("NotificationResponseUserId");
            currentResponse.ResponseUserName = args.getString("NotificationResponseUserName");
            currentResponse.ResponseString = args.getString("NotificationResponseMessage");
            currentResponse.ResponseUserProfession = args
                    .getString("NotificationResponseUserProfession");
            currentResponse.ResponseUserProfilePhotoServerPath = args
                    .getString("NotificationResponseUserProfilePhotoServerPath");

            currentRequest = RequestsDBHandler.GetRequest(getActivity().getApplicationContext(),
                    currentResponse.RequestId);

            requestString.setText(currentRequest.RequestString);
            responseUserName.setText(currentResponse.ResponseUserName);
            responseUserProfession.setText(currentResponse.ResponseUserProfession);
            responseString.setText(currentResponse.ResponseString);
        }

        if(!currentResponse.ResponseUserProfilePhotoServerPath.isEmpty() && currentResponse.ResponseUserProfilePhotoServerPath != null){
            Picasso.with(activity.getApplicationContext()).load(currentResponse.ResponseUserProfilePhotoServerPath).into(profilePhoto);
        }

        acceptResponse.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int existingChatId = ChatIdMapDBHandler.CheckUserIdAndReturnChatId(getActivity()
                        .getApplicationContext(), currentResponse.ResponseUserId);
                if (existingChatId > 0) {
                    Intent i = new Intent(getActivity().getApplicationContext(), ChatActivity.class);
                    i.putExtra("ChatId", "" + existingChatId);
                    startActivity(i);
                } else {
                    GetChatId chat = new GetChatId();
                    chat.execute("http://teach-mate.azurewebsites.net/Chat/ChatReg");
                }
            }
        });

        return layout;

    }

    /*
     * private class HttpGetter extends AsyncTask<String, Void, String> {
     * @Override protected String doInBackground(String... urls) { StringBuilder
     * builder = new StringBuilder(); HttpClient client = new
     * DefaultHttpClient(); HttpGet httpGet = new HttpGet(urls[0]); String line
     * = ""; try { HttpResponse response = client.execute(httpGet); StatusLine
     * statusLine = response.getStatusLine(); int statusCode =
     * statusLine.getStatusCode(); if (statusCode == 200) { HttpEntity entity =
     * response.getEntity(); InputStream content = entity.getContent();
     * BufferedReader reader = new BufferedReader( new
     * InputStreamReader(content)); while ((line = reader.readLine()) != null) {
     * builder.append(line); } Log.v("Getter", "Your data: " +
     * builder.toString()); //response data } else { Log.e("Getter",
     * "Failed to get data"); } } catch (ClientProtocolException e) {
     * e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); }
     * return builder.toString(); }
     * @Override protected void onPostExecute(String result) { currentResponse =
     * GetObjectsFromResponse(result); if(currentResponse != null){
     * responseUserName.setText(currentResponse.ResponseUserName);
     * responseString.setText(currentResponse.ResponseString);
     * responseUserProfession.setText(currentResponse.ResponseUserProfession); }
     * } } private Responses GetObjectsFromResponse(String result) { try {
     * //JSONObject employee =(new
     * JSONObject(response)).getJSONObject("Requests"); JSONObject temp = (new
     * JSONObject(result)).getJSONObject("Response"); Responses response = new
     * Responses(); response.RequestId = temp.getString("RequestId") != null ?
     * temp.getString("RequestId") : null; response.ResponseId=
     * temp.getString("ResponseId") != null ? temp.getString("ResponseId"):
     * null; response.ResponseString= temp.getString("ResponseString") != null ?
     * temp.getString("ResponseString"): null; response.ResponseUserId =
     * temp.getString("ResponseUserId") != null ?
     * temp.getString("ResponseUserId"): null; response.ResponseUserName =
     * temp.getString("ResponseUserName") != null ?
     * temp.getString("ResponseUserName"): null; response.ResponseUserProfession
     * = temp.getString("ResponseUserProfession") != null ?
     * temp.getString("ResponseUserProfession"): null;
     * response.ResponseUserProfilePhotoServerPath =
     * temp.getString("ResponseUserProfilePhotoServerPath") != null ?
     * temp.getString("ResponseUserProfilePhotoServerPath"): null; return
     * response; } catch(Exception e){
     * Toast.makeText(getActivity().getApplicationContext(), e.getMessage(),
     * Toast.LENGTH_LONG).show(); return null; } }
     */

    /*
     * @Override public boolean onCreateOptionsMenu(Menu menu) { // Inflate the
     * menu; this adds items to the action bar if it is present.
     * getMenuInflater().inflate(R.menu.menu_response, menu); return true; }
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String POST(String url) {
        InputStream inputStream = null;
        String result = "";
        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            String json = "";
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("SenderId", TempDataClass.serverUserId);
            jsonObject.put("ReceiverId", currentResponse.ResponseUserId);

            json = jsonObject.toString();

            StringEntity se = new StringEntity(json);

            httpPost.setEntity(se);

            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            HttpResponse httpResponse = httpclient.execute(httpPost);

            inputStream = httpResponse.getEntity().getContent();

            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "";

            return result;

        } catch (Exception e) {
            Log.v("Getter", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

    private class GetChatId extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            ChatIdMap newChatId = new ChatIdMap();

            newChatId.chatId = result;
            newChatId.userId = currentResponse.ResponseUserId;
            newChatId.userName = currentResponse.ResponseUserName;

            ChatIdMapDBHandler.InsertChatIdMap(getActivity().getApplicationContext(), newChatId);

            Intent i = new Intent(getActivity().getApplicationContext(), ChatActivity.class);
            i.putExtra("ChatId", newChatId.chatId);
            startActivity(i);
        }
    }
}
