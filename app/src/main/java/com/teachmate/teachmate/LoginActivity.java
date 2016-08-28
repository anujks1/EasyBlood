package com.teachmate.teachmate;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.teachmate.teachmate.DBHandlers.DeviceInfoDBHandler;
import com.teachmate.teachmate.DBHandlers.RequestsDBHandler;
import com.teachmate.teachmate.DBHandlers.UserModelDBHandler;
import com.teachmate.teachmate.SignUp.NewSignUpActicity;
import com.teachmate.teachmate.models.DeviceInfoKeys;
import com.teachmate.teachmate.models.DeviceInfoModel;
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
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class LoginActivity extends Activity {

    EditText editTextEmailId;
    EditText editTextPassword;

    String _editTextEmail = "";
    String _editTextPassword = "";

    ProgressDialog progressDialog;

    String serverRegId;

    String profilePhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        editTextEmailId = (EditText) findViewById(R.id.editTextLoginEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextLoginPassword);

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        finish();
    }

    public void SignUpAction(View v){
        Intent i = new Intent(this, NewSignUpActicity.class);
        startActivity(i);
    }

    public void AuthenticateUser(View v){

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading..");
        progressDialog.show();

        _editTextEmail = editTextEmailId.getText().toString();
        if(_editTextEmail.isEmpty()){
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Please Enter Email Id", Toast.LENGTH_SHORT).show();
            return;
        }

        _editTextPassword = editTextPassword.getText().toString();
        if(_editTextPassword.isEmpty()){
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Please Enter Password Id", Toast.LENGTH_SHORT).show();
            return;
        }

        HttpAuthenticateUser authenticateUser = new HttpAuthenticateUser();
        authenticateUser.execute("http://teach-mate.azurewebsites.net/User/CheckUser");

    }

    public String POST(String url){
        InputStream inputStream = null;
        String result = "";
        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            String json = "";
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("EmailId", _editTextEmail);
            jsonObject.put("Password", _editTextPassword);
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
    private class HttpAuthenticateUser extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return POST(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(getBaseContext(), "Data Sent! -" + result.toString(), Toast.LENGTH_LONG).show();
            convertJsonToObject(result);

            HttpGetAllGeneratedRequests requests = new HttpGetAllGeneratedRequests();
            requests.execute("http://teach-mate.azurewebsites.net/Request/GetRequestsByUser?id=" + TempDataClass.serverUserId);

            if(!TempDataClass.deviceRegId.equals(serverRegId)){
                HttpPostRegIdToServer regIdPost = new HttpPostRegIdToServer();
                regIdPost.execute("http://teach-mate.azurewebsites.net/User/UpdateRegId");
            }
            else if(TempDataClass.deviceRegId.equals(serverRegId)){
                progressDialog.dismiss();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
        }
    }

    private void convertJsonToObject(String result) {
        try {
            String status = (new JSONObject(result)).get("Status").toString();

            if(status.equals("Success")){
                Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                JSONObject userDataJson = (new JSONObject(result)).getJSONObject("UserDetails");

                UserModel userData = new UserModel();
                String[] temp = new String[2];
                int i = 0;
                for (String retval: userDataJson.getString("Name").split(" ", 2)){
                    if(i == 0)
                        temp[0] = retval;
                    else
                        temp[1] = retval;

                    i++;
                }
                userData.FirstName = temp[0];
                userData.LastName = temp[1];
                TempDataClass.userName = userData.FirstName + " " + userData.LastName;
                userData.ServerUserId = userDataJson.get("Id").toString();
                TempDataClass.serverUserId = userData.ServerUserId;
                userData.PhoneNumber = userDataJson.get("PhoneNumber").toString();
                userData.Profession = userDataJson.get("Profession").toString();
                TempDataClass.userProfession = userData.Profession;
                userData.EmailId = userDataJson.get("EmailId").toString();
                userData.Address1 = userDataJson.get("Address1").toString();
                userData.PinCode1 = userDataJson.get("PinCode1").toString();
                userData.Address2 = userDataJson.get("Address2").toString();
                userData.PinCode2 = userDataJson.get("Pincode2").toString();

                profilePhotoPath = userDataJson.get("ProfilePhotoUrl").toString();

                UserModelDBHandler.InsertProfile(getApplicationContext(), userData);

                DeviceInfoModel model = new DeviceInfoModel();
                model.Key = DeviceInfoKeys.PROFILE_PHOTO_SERVER_PATH;
                model.Value = profilePhotoPath;
                DeviceInfoDBHandler.InsertDeviceInfo(getApplicationContext(), model);

                TempDataClass.profilePhotoServerPath = profilePhotoPath;

                serverRegId = userDataJson.get("RegistrationId").toString();
            }
            else{
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Wrong EmailId or Password. Try Again", Toast.LENGTH_SHORT).show();
            }
        }catch(Exception ex){
            progressDialog.dismiss();
            //Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), "Wrong EmailId or Password. Try Again", Toast.LENGTH_SHORT).show();
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

    public String POSTRegID(String url){
        InputStream inputStream = null;
        String result = "";
        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            String json = "";
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Id", TempDataClass.serverUserId);
            jsonObject.put("RegistrationId", TempDataClass.deviceRegId);
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
    private class HttpPostRegIdToServer extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return POSTRegID(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.equals("OK")){
                //Toast.makeText(getApplicationContext(), "Registration Successfull.", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);

                finish();

            }
        }
    }

    private class HttpGetAllGeneratedRequests extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            // TODO Auto-generated method stub
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
            if(result != null && !result.isEmpty()) {
                if (!result.equals("Empty")) {
                    List<Requests> list = GetObjectsFromResponse(result);
                    if (list != null) {
                        for(int i = 0; i < list.size(); i++){
                            Requests request = list.get(i);
                            request.requestYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
                            request.requestDayOfTheYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
                            RequestsDBHandler.InsertRequests(getApplicationContext(), request);
                        }
                    }
                }
            }
        }
    }

    private List<Requests> GetObjectsFromResponse(String response) {
        try {
            //JSONObject employee =(new JSONObject(response)).getJSONObject("Requests");
            JSONArray contacts = (new JSONObject(response)).getJSONArray("Requests");

            List<Requests> list = new ArrayList<Requests>();


            for(int i = contacts.length() - 1; i >= 0 ; i--){
                Requests request = new Requests();
                JSONObject temp = contacts.getJSONObject(i);

                request.RequestID = temp.getString("RequestId") != null ? temp.getString("RequestId") : null;
                request.RequestUserName = temp.getString("RequestUserName") != null ? temp.getString("RequestUserName"): null;
                request.RequestString = temp.getString("RequestMessage") != null ? temp.getString("RequestMessage"): null;
                request.RequesteUserId = temp.getString("RequesteUserId") != null ? temp.getString("RequesteUserId"): null;
                request.RequestTime = temp.getString("RequestedTime") != null ? temp.getString("RequestedTime"): null;
                request.RequestUserProfession = temp.getString("RequestUserProfession") != null ? temp.getString("RequestUserProfession"): null;
                request.RequestUserProfilePhotoServerPath = temp.getString("RequestUserProfilePhotoServerPath") != null ? temp.getString("RequestUserProfilePhotoServerPath"): null;

                list.add(request);

            }

            return list;
        }
        catch(Exception e){
            //.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("ERROR", e.getMessage());
            return null;
        }
    }

}
