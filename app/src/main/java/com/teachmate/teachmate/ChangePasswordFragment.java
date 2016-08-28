package com.teachmate.teachmate;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.teachmate.teachmate.DBHandlers.UserModelDBHandler;
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

public class ChangePasswordFragment extends Fragment {

    EditText editTextCurrentPassword;
    EditText editTextNewPassword;
    EditText editTextConfirmPassword;

    String _editTextCurrentPassword;
    String _editTextNewPassword;
    String _editTextConfirmPassword;

    ProgressDialog progressDialog;

    Button changePassword;

    FragmentActivity activity;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(FragmentTitles.CHANGE_PASSWORD);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.fragment_change_password, container, false);
        activity = (FragmentActivity) super.getActivity();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);

        editTextCurrentPassword = (EditText) layout.findViewById(R.id.editTextCurrentPassword);
        editTextNewPassword = (EditText) layout.findViewById(R.id.editTextNewPassword);
        editTextConfirmPassword = (EditText) layout.findViewById(R.id.editTextConfirmPassword);

        changePassword = (Button) layout.findViewById(R.id.buttonChangePassword);

        changePassword.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                progressDialog.show();

                _editTextCurrentPassword = editTextCurrentPassword.getText().toString();
                if(_editTextCurrentPassword.isEmpty()){
                    progressDialog.dismiss();
                    editTextCurrentPassword.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                    Toast.makeText(activity.getApplicationContext(), "Please enter " + editTextCurrentPassword.getHint().toString(), Toast.LENGTH_SHORT).show();
                    return;
                }

                _editTextNewPassword = editTextNewPassword.getText().toString();
                if(_editTextNewPassword.isEmpty()){
                    progressDialog.dismiss();
                    editTextNewPassword.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                    Toast.makeText(activity.getApplicationContext(), "Please enter " + editTextNewPassword.getHint().toString(), Toast.LENGTH_SHORT).show();
                    return;
                }

                _editTextConfirmPassword = editTextConfirmPassword.getText().toString();
                if(_editTextConfirmPassword.isEmpty()){
                    progressDialog.dismiss();
                    editTextConfirmPassword.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                    Toast.makeText(activity.getApplicationContext(), "Please enter " + editTextConfirmPassword.getHint().toString(), Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!_editTextNewPassword.equals(_editTextConfirmPassword)){
                    progressDialog.dismiss();
                    Toast.makeText(activity.getApplicationContext(), "Passwords do not match. Please Re-Enter Passwords", Toast.LENGTH_SHORT).show();
                    return;
                }

                HttpAuthenticateUser post = new HttpAuthenticateUser();
                post.execute("http://teach-mate.azurewebsites.net/User/CheckUser");
            }
        });

        return layout;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    public String POST(String url){
        InputStream inputStream = null;
        String result = "";
        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            String json = "";
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("Password", _editTextNewPassword);
            jsonObject.put("Id", TempDataClass.serverUserId);

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
            progressDialog.dismiss();
            //Toast.makeText(activity.getBaseContext(), "Data Sent! -" + result.toString(), Toast.LENGTH_LONG).show();

            if(result != null){
                if(!(result.isEmpty() || result.contains("ERROR"))){
                    progressDialog.dismiss();
                    Toast.makeText(activity.getApplicationContext(), "Update Successful", Toast.LENGTH_SHORT).show();

                    FragmentManager fragmentManager = activity.getSupportFragmentManager();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    ft.replace(R.id.container, TempDataClass.fragmentStack.lastElement());
                    TempDataClass.fragmentStack.pop();
                    ft.commit();

                }
                else{
                    progressDialog.dismiss();
                    Toast.makeText(activity.getApplicationContext(), "Update Failed. Please try Again.", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                progressDialog.dismiss();
                Toast.makeText(activity.getApplicationContext(), "Update Failed. Please try Again.", Toast.LENGTH_SHORT).show();
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



    public String Authenticate(String url){
        InputStream inputStream = null;
        String result = "";
        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            String json = "";
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("EmailId", TempDataClass.emailId);
            jsonObject.put("Password", _editTextCurrentPassword);
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
            return Authenticate(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(activity.getBaseContext(), "Data Sent! -" + result.toString(), Toast.LENGTH_LONG).show();

            boolean authentic = convertJsonToObject(result);

            if(authentic){
                HttpAsyncTask post = new HttpAsyncTask();
                post.execute("http://teach-mate.azurewebsites.net/User/UpdateUser");
            }

        }
    }

    private boolean convertJsonToObject(String result) {
        try {
            String status = (new JSONObject(result)).get("Status").toString();

            if(status.equals("Success")){
                return true;
            }
            else{
                progressDialog.dismiss();
                Toast.makeText(activity.getApplicationContext(), "Wrong Password. Try Again", Toast.LENGTH_SHORT).show();
                return false;
            }
        }catch(Exception ex){
            progressDialog.dismiss();
            //Toast.makeText(activity.getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("UPDATE", ex.getMessage());
            return false;
        }

    }

}
