package com.teachmate.teachmate.Requests;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.teachmate.teachmate.DBHandlers.UserModelDBHandler;
import com.teachmate.teachmate.FragmentTitles;
import com.teachmate.teachmate.MainActivity;
import com.teachmate.teachmate.R;
import com.teachmate.teachmate.TempDataClass;
import com.teachmate.teachmate.models.Requests;
import com.teachmate.teachmate.models.UserModel;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;


public class RequestDisplayActivity extends Fragment {

    Requests currentRequest;
    String notificationRequestId;

    String responseMessageString;

    Button sendResponseButton;

    TextView requestUserName;
    TextView requestString;
    TextView requestTime;
    TextView requestUserProfession;
    ImageView profilePhoto;

    ProgressDialog progressDialog;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(FragmentTitles.REQUEST);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentActivity activity = (FragmentActivity) super.getActivity();
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.activity_request_display, container, false);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);

        currentRequest = new Requests();

        sendResponseButton = (Button) layout.findViewById(R.id.buttonRespond);

        requestUserName = (TextView) layout.findViewById(R.id.textViewRequestUserName);
        requestString = (TextView) layout.findViewById(R.id.textViewRequestString);
        requestTime = (TextView) layout.findViewById(R.id.textViewTime);
        requestUserProfession = (TextView) layout.findViewById(R.id.textViewRequestUserProfession);

        profilePhoto = (ImageView) layout.findViewById(R.id.imageViewProfilePhoto);

        try {
            Bundle args = getArguments();
            notificationRequestId = args.getString("NotificationRequestId");
        }catch(Exception e){
            Log.e("Error", e.getMessage());
        }
        if(notificationRequestId == null){
            Bundle args = getArguments();

            currentRequest.RequestID = args.getString("RequestID");
            currentRequest.RequesteUserId = args.getString("RequesteUserId");
            currentRequest.RequestUserName = args.getString("RequestUserName");
            currentRequest.RequestString = args.getString("RequestString");
            currentRequest.RequestUserProfession = args.getString("RequestUserProfession");
            currentRequest.RequestUserProfilePhotoServerPath= args.getString("RequestUserProfilePhotoServerPath");
            currentRequest.RequestTime = args.getString("RequestTime");

            requestUserName.setText(currentRequest.RequestUserName);
            requestString.setText(currentRequest.RequestString);
            requestTime.setText(currentRequest.RequestTime);
            requestUserProfession.setText(currentRequest.RequestUserProfession);

            if(!currentRequest.RequestUserProfilePhotoServerPath.isEmpty() && currentRequest.RequestUserProfilePhotoServerPath != null){
                Picasso.with(activity.getApplicationContext()).load(currentRequest.RequestUserProfilePhotoServerPath).into(profilePhoto);
            }
        }
        else{
            progressDialog.setMessage("Loading Request...");
            progressDialog.show();
            UserModel cUser = UserModelDBHandler.ReturnValue(getActivity().getApplicationContext());
            TempDataClass.serverUserId = cUser.ServerUserId;
            HttpGetter getter = new HttpGetter();
            getter.execute("http://teach-mate.azurewebsites.net/Request/GetRequestDetails?id=" + notificationRequestId);
            //TODO
        }

        sendResponseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SendResponse(v);
            }
        });

        return layout;

    }

    private class HttpGetter extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder builder = new StringBuilder();
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(urls[0]);
            String line = "";

            try {
                HttpResponse response = client.execute(httpGet);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(content));

                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    Log.v("Getter", "Your data: " + builder.toString()); //response data
                } else {
                    Log.e("Getter", "Failed to get data");
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return builder.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            currentRequest = GetObjectsFromResponse(result);
            if(currentRequest != null){
                requestUserName.setText(currentRequest.RequestUserName);
                requestString.setText(currentRequest.RequestString);
                requestTime.setText(currentRequest.RequestTime);
                requestUserProfession.setText(currentRequest.RequestUserProfession);
            }
            progressDialog.dismiss();
        }
    }

    private Requests GetObjectsFromResponse(String response) {
        try {

            JSONObject currentJsonObject = (new JSONObject(response));

            Requests request = new Requests();

            request.RequestID = currentJsonObject.getString("RequestId") != null ? currentJsonObject.getString("RequestId") : null;
            request.RequestUserName = currentJsonObject.getString("RequestUserName") != null ? currentJsonObject.getString("RequestUserName"): null;
            request.RequestString = currentJsonObject.getString("RequestMessage") != null ? currentJsonObject.getString("RequestMessage"): null;
            request.RequesteUserId = currentJsonObject.getString("RequesteUserId") != null ? currentJsonObject.getString("RequesteUserId"): null;
            request.RequestTime = currentJsonObject.getString("RequestedTime") != null ? currentJsonObject.getString("RequestedTime"): null;
            request.RequestUserProfession = currentJsonObject.getString("RequestUserProfession") != null ? currentJsonObject.getString("RequestUserProfession"): null;
            request.RequestUserProfilePhotoServerPath = currentJsonObject.getString("RequestUserProfilePhotoServerPath") != null ? currentJsonObject.getString("RequestUserProfilePhotoServerPath"): null;

            return request;
        }
        catch(Exception e){
            //.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("Request", e.getMessage());
            return null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void SendResponse(View v){
        LayoutInflater li = LayoutInflater.from(getActivity().getApplicationContext());
        View promptsView = li.inflate(R.layout.alert_prompt_send_response, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptsView);
        final EditText responseEditText = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
        alertDialogBuilder.setMessage("Are you sure you want to respond to this request?");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if(responseEditText.getText().toString().equals("") || responseEditText.getText() == null){
                            Toast.makeText(getActivity().getApplicationContext(), "Please enter a Response Message!", Toast.LENGTH_LONG).show();
                        }
                        else{
                            responseMessageString = responseEditText.getText().toString();
                            HttpAsyncTask post = new HttpAsyncTask();
                            post.execute("http://teach-mate.azurewebsites.net/Response/SendResponseNotification");
                            progressDialog.setMessage("Generating Response...");
                            progressDialog.show();
                        }
                    }
                });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    public String POST(String url){
        InputStream inputStream = null;
        String result = "";
        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            String json = "";
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("RequestId", currentRequest.RequestID);
            jsonObject.put("UserId", TempDataClass.serverUserId);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH-mm-ss");
            String currentDateandTime = sdf.format(new Date());
            jsonObject.put("TimeOfResponse", currentDateandTime);
            jsonObject.put("Message", responseMessageString);
            json = jsonObject.toString();

            StringEntity se = new StringEntity(json);

            httpPost.setEntity(se);

            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");


            HttpResponse httpResponse = httpclient.execute(httpPost);

            inputStream = httpResponse.getEntity().getContent();

            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

            return result;

        } catch (Exception e) {
            Log.v("Getter", e.getLocalizedMessage());
        }

        return result;
    }
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return POST(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(getActivity().getBaseContext(), "Data Sent! -" + result.toString(), Toast.LENGTH_LONG).show();
            Toast.makeText(getActivity().getBaseContext(), "Generated Response Successfully.", Toast.LENGTH_LONG).show();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            progressDialog.dismiss();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.container, TempDataClass.fragmentStack.lastElement());
            TempDataClass.fragmentStack.pop();
            ft.commit();
        }
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

}
