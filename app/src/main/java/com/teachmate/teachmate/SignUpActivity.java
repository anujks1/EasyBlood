package com.teachmate.teachmate;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.teachmate.teachmate.DBHandlers.DeviceInfoDBHandler;
import com.teachmate.teachmate.DBHandlers.UserModelDBHandler;
import com.teachmate.teachmate.models.DeviceInfoKeys;
import com.teachmate.teachmate.models.DeviceInfoModel;
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
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;


public class SignUpActivity extends ActionBarActivity {

    ProgressDialog progressDialog;

    UserModel userData;

    EditText editTextFirstName;
    EditText editTextLastName;
    EditText editTextPhoneNumber;
    EditText editTextProfession;
    EditText editTextEmailId;
    EditText editTextPassword;
    EditText editTextReEnteredPassword;
    EditText editTextAddress1;
    EditText editTextPinCode1;
    EditText editTextAddress2;
    EditText editTextPinCode2;

    String _editTextFirstName = "";
    String _editTextLastName = "";
    String _editTextPhoneNumber = "";
    String _editTextProfession = "";
    String _editTextEmailId = "";
    String _editTextPassword = "";
    String _editTextReEnteredPassword = "";
    String _editTextAddress1 = "";
    String _editTextPinCode1 = "";
    String _editTextAddress2 = "";
    String _editTextPinCode2 = "";

    String pinCode1Status = "";
    String pinCode2Status = "";

    boolean isPinCode1Tested = false;
    boolean isPinCode2Tested = false;

    float lattitude1, longitude1, lattitude2, longitude2;

    boolean isPinCode1Verified = false;
    boolean isPinCode2Verified = false;

    String pinCode1HttpStatus = "unknown";
    String pinCode2HttpStatus = "unknown";

    ImageButton image;
    InputStream inputStream;

    private static int RESULT_LOAD_IMAGE = 2;
    private final String FILEPATH = "FilePath";

    SharedPreferences prefs;

    private static String profilePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editTextFirstName = (EditText) findViewById(R.id.editTextFirstName);
        editTextLastName = (EditText) findViewById(R.id.editTextLastName);
        editTextPhoneNumber = (EditText) findViewById(R.id.editTextPhoneNumber);
        editTextProfession = (EditText) findViewById(R.id.editTextProfession);
        editTextEmailId = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextReEnteredPassword = (EditText) findViewById(R.id.editTextReEnterPassword);
        editTextAddress1 = (EditText) findViewById(R.id.editTextAddress1);
        editTextPinCode1 = (EditText) findViewById(R.id.editTextPinCode1);
        editTextAddress2= (EditText) findViewById(R.id.editTextAddress2);
        editTextPinCode2 = (EditText) findViewById(R.id.editTextPinCode2);

        image = (ImageButton) findViewById(R.id.imageButtonProfilePhoto);
        prefs = getSharedPreferences("com.example.app", Context.MODE_PRIVATE);

        this.setTitle("Sign Up");


        image.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, 2);
            }
        });

        userData = new UserModel();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri SelectedImage = data.getData();
            String[] FilePathColumn = {MediaStore.Images.Media.DATA };

            Cursor SelectedCursor = getContentResolver().query(SelectedImage, FilePathColumn, null, null, null);
            SelectedCursor.moveToFirst();

            int columnIndex = SelectedCursor.getColumnIndex(FilePathColumn[0]);
            String picturePath = SelectedCursor.getString(columnIndex);
            profilePath = picturePath;
            SelectedCursor.close();

            image.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            CommonMethods.scaleImage(getApplicationContext(), image, 100);
            //Toast.makeText(getApplicationContext(), picturePath, Toast.LENGTH_SHORT).show();

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
        return true;
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

    public void SignUpUser(View v){
        progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading..");
        progressDialog.show();

        _editTextFirstName = editTextFirstName.getText().toString();
        if(_editTextFirstName.isEmpty()){
            progressDialog.dismiss();
            editTextFirstName.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            Toast.makeText(getApplicationContext(), "Please enter " + editTextFirstName.getHint().toString(), Toast.LENGTH_SHORT).show();
            return;
        }

        _editTextLastName = editTextLastName.getText().toString();
        if(_editTextLastName.isEmpty()){
            progressDialog.dismiss();
            editTextLastName.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            Toast.makeText(getApplicationContext(), "Please enter " + editTextLastName.getHint().toString(), Toast.LENGTH_SHORT).show();
            return;
        }

        _editTextPhoneNumber = editTextPhoneNumber.getText().toString();
        if(_editTextPhoneNumber.isEmpty()){
            progressDialog.dismiss();
            editTextPhoneNumber.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            Toast.makeText(getApplicationContext(), "Please enter " + editTextPhoneNumber.getHint().toString(), Toast.LENGTH_SHORT).show();
            return;
        }


        _editTextProfession = editTextProfession.getText().toString();
        if(_editTextProfession.isEmpty()){
            progressDialog.dismiss();
            editTextProfession.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            Toast.makeText(getApplicationContext(), "Please enter " + editTextProfession.getHint().toString(), Toast.LENGTH_SHORT).show();
            return;
        }

        _editTextEmailId = editTextEmailId.getText().toString();
        if(_editTextEmailId.isEmpty()){
            progressDialog.dismiss();
            editTextEmailId.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            Toast.makeText(getApplicationContext(), "Please enter " + editTextEmailId.getHint().toString(), Toast.LENGTH_SHORT).show();
            return;
        }

        _editTextPassword = editTextPassword.getText().toString();
        if(_editTextPassword.isEmpty()){
            progressDialog.dismiss();
            editTextPassword.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            Toast.makeText(getApplicationContext(), "Please enter " + editTextPassword.getHint().toString(), Toast.LENGTH_SHORT).show();
            return;
        }

        _editTextReEnteredPassword = editTextReEnteredPassword.getText().toString();
        if(_editTextReEnteredPassword.isEmpty()){
            progressDialog.dismiss();
            editTextReEnteredPassword.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            Toast.makeText(getApplicationContext(), "Please " + editTextReEnteredPassword.getHint().toString(), Toast.LENGTH_SHORT).show();
            return;
        }

        if(!_editTextPassword.equals(_editTextReEnteredPassword)){
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Passwords do not match. Please Re-Enter Passwords", Toast.LENGTH_SHORT).show();
            return;
        }

        _editTextAddress1 = editTextAddress1.getText().toString();
        if(_editTextAddress1.isEmpty()){
            progressDialog.dismiss();
            editTextAddress1.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            Toast.makeText(getApplicationContext(), "Please enter " + editTextAddress1.getHint().toString(), Toast.LENGTH_SHORT).show();
            return;
        }

        _editTextPinCode1 = editTextPinCode1.getText().toString();
        if(_editTextPinCode1.isEmpty()){
            progressDialog.dismiss();
            editTextPinCode1.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            Toast.makeText(getApplicationContext(), "Please enter " + editTextPinCode1.getHint().toString(), Toast.LENGTH_SHORT).show();
            return;
        }

        _editTextAddress2 = editTextAddress2.getText().toString();
        if(_editTextAddress2.isEmpty()){
            progressDialog.dismiss();
            editTextAddress2.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            Toast.makeText(getApplicationContext(), "Please enter " + editTextAddress2.getHint().toString(), Toast.LENGTH_SHORT).show();
            return;
        }

        _editTextPinCode2 = editTextPinCode2.getText().toString();
        if(_editTextPinCode2.isEmpty()){
            progressDialog.dismiss();
            editTextPinCode2.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            Toast.makeText(getApplicationContext(), "Please enter " + editTextPinCode2.getHint().toString(), Toast.LENGTH_SHORT).show();
            return;
        }

        userData.FirstName = _editTextFirstName;
        userData.LastName= _editTextLastName;
        userData.PhoneNumber = _editTextPhoneNumber;
        userData.Profession = _editTextProfession;
        userData.EmailId = _editTextEmailId;
        userData.Address1 = _editTextAddress1;
        userData.PinCode1 = _editTextPinCode1;
        userData.Address2 = _editTextAddress2;
        userData.PinCode2 = _editTextPinCode2;

        HttpGetterPinCode1Handler getter1 = new HttpGetterPinCode1Handler();
        getter1.execute("http://maps.google.com/maps/api/geocode/xml?address='" + _editTextPinCode1 + "'&sensor=false");

        HttpGetterPinCode2Handler getter2 = new HttpGetterPinCode2Handler();
        getter2.execute("http://maps.google.com/maps/api/geocode/xml?address='" + _editTextPinCode2 + "'&sensor=false");

        SignUpUsersIfPinCodesAreVerified signUpProcess = new SignUpUsersIfPinCodesAreVerified();
        signUpProcess.execute();
    }

    private class SignUpUsersIfPinCodesAreVerified extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {

            while(!(isPinCode1Verified && isPinCode2Verified)){
                if(isPinCode1Tested && isPinCode2Tested) {
                    break;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){

            if (pinCode1HttpStatus.equals("error") || pinCode1Status.equals("ZERO_RESULTS")) {
                Toast.makeText(getApplicationContext(), "Pin Code 1 not Valid.", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                return;
            }
            else if (pinCode2HttpStatus.equals("error") || pinCode2Status.equals("ZERO_RESULTS")) {
                Toast.makeText(getApplicationContext(), "Pin Code 2 not Valid.", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                return;
            }

            HttpSignUpAsyncTask signUpUser = new HttpSignUpAsyncTask();
            signUpUser.execute("http://teach-mate.azurewebsites.net/User/AddUser");

            return;
        }
    }

    public String POSTRegID(String url){
        InputStream inputStream = null;
        String result = "";
        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            String json = "";
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Id", userData.ServerUserId);
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
                Toast.makeText(getApplicationContext(), "Registration Successfull.", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);

                finish();

            }
        }
    }

    public String POSTSignUpDetails(String url){
        InputStream inputStream = null;
        String result = "";
        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            String json = "";
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("UserName", _editTextFirstName + " " + _editTextLastName);
            jsonObject.put("PhoneNumber", _editTextPhoneNumber);
            jsonObject.put("Profession", _editTextProfession);
            jsonObject.put("EmailId", _editTextEmailId);
            jsonObject.put("Password", _editTextPassword);
            jsonObject.put("Address1", _editTextAddress1);
            jsonObject.put("PinCode1", _editTextPinCode1);
            jsonObject.put("Latitude1", lattitude1);
            jsonObject.put("Longitude1", longitude1);
            jsonObject.put("Address2", _editTextAddress2);
            jsonObject.put("PinCode2", _editTextPinCode2);
            jsonObject.put("Latitude2", lattitude2);
            jsonObject.put("Longitude2", longitude2);
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
    private class HttpSignUpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return POSTSignUpDetails(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            Toast.makeText(getBaseContext(), "Data Sent! -" + result.toString(), Toast.LENGTH_LONG).show();

            if(result != null){
                if(!(result.isEmpty() || result.contains("ERROR"))){
                    //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();

                    TempDataClass.userName = userData.FirstName + userData.LastName;
                    TempDataClass.userProfession = userData.Profession;
                    TempDataClass.serverUserId = result;

                    userData.ServerUserId = result;

                    UserModelDBHandler.InsertProfile(getApplicationContext(), userData);

                    HttpPostRegIdToServer regIdPost = new HttpPostRegIdToServer();
                    regIdPost.execute("http://teach-mate.azurewebsites.net/User/UpdateRegId");

                    DeviceInfoModel model = new DeviceInfoModel();

                    if(!profilePath.isEmpty()) {
                        TempDataClass.profilePhotoLocalPath = profilePath;
                        model.Key = DeviceInfoKeys.PROFILE_PHOTO_LOCAL_PATH;
                        model.Value = profilePath;
                        DeviceInfoDBHandler.InsertDeviceInfo(getApplicationContext(), model);

                        model = new DeviceInfoModel();
                        model.Key = DeviceInfoKeys.PROFILE_PHOTO_SERVER_PATH;
                        model.Value = "http://teach-mate.azurewebsites.net/MyImages/"+TempDataClass.serverUserId+".jpg";
                        DeviceInfoDBHandler.InsertDeviceInfo(getApplicationContext(), model);
                        TempDataClass.profilePhotoServerPath = "http://teach-mate.azurewebsites.net/MyImages/"+TempDataClass.serverUserId+".jpg";
                        UploadImage(profilePath);
                    }
                    else{
                        TempDataClass.profilePhotoServerPath = "http://teach-mate.azurewebsites.net/MyImages/default.jpg";
                        model.Key = DeviceInfoKeys.PROFILE_PHOTO_SERVER_PATH;
                        model.Value = "http://teach-mate.azurewebsites.net/MyImages/default.jpg";
                        DeviceInfoDBHandler.InsertDeviceInfo(getApplicationContext(), model);
                    }

                }
                else{
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Registration Failed. Please try Again.", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Registration Failed. Please try Again.", Toast.LENGTH_SHORT).show();
            }
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




    private class HttpGetterPinCode1Handler extends AsyncTask<String, Void, String> {

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
                pinCode1HttpStatus = "error";
                e.printStackTrace();
            } catch (IOException e) {
                pinCode1HttpStatus = "error";
                e.printStackTrace();
            }

            return builder.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            pinCode1HttpStatus = "OK";
            pinCode1Status = getStatusPinCode1FromXml(result);
            isPinCode1Tested = true;
            if(pinCode1Status.equals("OK")) {
                isPinCode1Verified = true;
            }
            //Toast.makeText(getApplicationContext(), pinCode1Status, Toast.LENGTH_SHORT).show();
        }
    }

    private String getStatusPinCode1FromXml(String result) {

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(new StringReader(result));

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {

                String name = null;
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equalsIgnoreCase("status")) {
                            pinCode1Status = parser.nextText();
                            if(pinCode1Status.equals("ZERO_RESULTS")){
                                return pinCode1Status;
                            }
                        }
                        else if (name.equalsIgnoreCase("location")){
                            int subEvent = parser.nextTag();
                            if(subEvent == XmlPullParser.START_TAG){
                                String subName = parser.getName();
                                if(subName.equals("lat")){
                                    lattitude1 = Float.parseFloat(parser.nextText().toString());
                                }
                                subEvent = parser.nextTag();
                                if(subEvent == XmlPullParser.START_TAG){
                                    subName = parser.getName();
                                    if(subName.equals("lng")){
                                        longitude1 = Float.parseFloat(parser.nextText().toString());
                                    }
                                }

                            }

                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        break;
                }
                eventType = parser.next();
            }
        }catch(Exception ex){
            //Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("PinCode", ex.getMessage());
        }


        return pinCode1Status;
    }

    private class HttpGetterPinCode2Handler extends AsyncTask<String, Void, String> {

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
                pinCode2HttpStatus = "error";
                e.printStackTrace();
            } catch (IOException e) {
                pinCode2HttpStatus = "error";
                e.printStackTrace();
            }

            return builder.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            pinCode2HttpStatus = "OK";
            pinCode2Status = getStatusPinCode2FromXml(result);
            isPinCode2Tested = true;
            if(pinCode2Status.equals("OK")){
                isPinCode2Verified = true;
            }
            //Toast.makeText(getApplicationContext(), "p2:" + pinCode2Status, Toast.LENGTH_SHORT).show();
        }
    }

    private String getStatusPinCode2FromXml(String result) {

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(new StringReader(result));

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {

                String name = null;
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equalsIgnoreCase("status")) {
                            pinCode2Status = parser.nextText();
                            if(pinCode2Status.equals("ZERO_RESULTS")){
                                return pinCode2Status;
                            }
                        }
                        else if (name.equalsIgnoreCase("location")){
                            int subEvent = parser.nextTag();
                            if(subEvent == XmlPullParser.START_TAG){
                                String subName = parser.getName();
                                if(subName.equals("lat")){
                                    lattitude2 = Float.parseFloat(parser.nextText().toString());
                                }
                                subEvent = parser.nextTag();
                                if(subEvent == XmlPullParser.START_TAG){
                                    subName = parser.getName();
                                    if(subName.equals("lng")){
                                        longitude2 = Float.parseFloat(parser.nextText().toString());
                                    }
                                }

                            }

                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        break;
                }
                eventType = parser.next();
            }
        }catch(Exception ex){
            //.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("PinCode", ex.getMessage());
        }


        return pinCode2Status;
    }


    public void UploadImage(String image_location){

        Bitmap bitmap = BitmapFactory.decodeFile(image_location);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream); //compress to which format you want.
        final byte [] byte_arr = stream.toByteArray();
        final String image_str = Base64.encodeBytes(byte_arr);


        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                try{
                    JSONObject json=new JSONObject();
                    json.put("UserID", TempDataClass.serverUserId);
                    json.put("ImageArray",image_str);
                    String myjson="";

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost("http://teach-mate.azurewebsites.net/User/UploadImage");
                    myjson=json.toString();
                    StringEntity se = new StringEntity(myjson);
                    Log.e("Upload", myjson);
                    httppost.setEntity(se);
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity _response = response.getEntity(); // content will be consume only once
                    final  String the_string_response = convertResponseToString(_response);
                    Log.e("Upload", the_string_response);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            //Toast.makeText(getApplicationContext(), "Response " + the_string_response, Toast.LENGTH_LONG).show();
                        }
                    });

                }catch(Exception e){
                   /* runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(UploadImage.this, "ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });*/
                    Log.e("Upload Image","Error in http connection "+e.toString());
                    //Toast.makeText(getApplicationContext(), "" +e.toString(),Toast.LENGTH_LONG).show();
                }
            }
        });
        t.start();
    }

    public String convertResponseToString(HttpEntity response) throws IllegalStateException, IOException{

        String res = "";
        StringBuffer buffer = new StringBuffer();
        inputStream = response.getContent();
        final int contentLength = (int) response.getContentLength(); //getting content length…..
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                //Toast.makeText(getApplicationContext(), "contentLength : " + contentLength, Toast.LENGTH_LONG).show();
                Log.d("Upload", "Image Upload successful");
            }
        });

        if (contentLength < 0){
        }
        else{
            byte[] data = new byte[512];
            int len = 0;
            try
            {
                while (-1 != (len = inputStream.read(data)) )
                {
                    buffer.append(new String(data, 0, len)); //converting to string and appending  to stringbuffer…..

                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            try
            {
                inputStream.close(); // closing the stream…..
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            res = buffer.toString();     // converting stringbuffer to string…..
/*
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(UploadImage.this, "Result : " + res, Toast.LENGTH_LONG).show();
                }
            });*/
            System.out.println("Response => " +  EntityUtils.toString(response));
        }
        return res;
    }
}
