package com.teachmate.teachmate;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
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

public class UpdateUserFragment extends Fragment {

    TextView textViewEmailId;

    EditText editTextFirstName;
    EditText editTextLastName;
    EditText editTextPhoneNumber;

    Button updateUser;

    String _editTextFirstName = "";
    String _editTextLastName = "";
    String _editTextPhoneNumber = "";

    ProgressDialog progressDialog;
    UserModel user;

    FragmentActivity activity;

    public UpdateUserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.fragment_update_user, container, false);

        activity = (FragmentActivity) super.getActivity();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);

        textViewEmailId = (TextView) layout.findViewById(R.id.textVIewUpdateEmailId);

        editTextFirstName = (EditText) layout.findViewById(R.id.editTextUpdateFirstName);
        editTextLastName = (EditText) layout.findViewById(R.id.editTextUpdateLastName);
        editTextPhoneNumber = (EditText) layout.findViewById(R.id.editTextUpdatePhoneNumber);

        updateUser = (Button) layout.findViewById(R.id.buttonUpdate);

        user = UserModelDBHandler.ReturnValue(getActivity().getApplicationContext());
        TempDataClass.serverUserId = user.ServerUserId;

        textViewEmailId.setText(user.EmailId);

        editTextFirstName.setText(user.FirstName);
        editTextLastName.setText(user.LastName);
        editTextPhoneNumber.setText(user.PhoneNumber);

        updateUser.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                progressDialog.show();

                _editTextFirstName = editTextFirstName.getText().toString();
                if(_editTextFirstName.isEmpty()){
                    progressDialog.dismiss();
                    editTextFirstName.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                    Toast.makeText(activity.getApplicationContext(), "Please enter " + editTextFirstName.getHint().toString(), Toast.LENGTH_SHORT).show();
                    return;
                }

                _editTextLastName = editTextLastName.getText().toString();
                if(_editTextLastName.isEmpty()){
                    progressDialog.dismiss();
                    editTextLastName.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                    Toast.makeText(activity.getApplicationContext(), "Please enter " + editTextLastName.getHint().toString(), Toast.LENGTH_SHORT).show();
                    return;
                }

                _editTextPhoneNumber = editTextPhoneNumber.getText().toString();
                if(_editTextPhoneNumber.isEmpty()){
                    progressDialog.dismiss();
                    editTextPhoneNumber.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                    Toast.makeText(activity.getApplicationContext(), "Please enter " + editTextPhoneNumber.getHint().toString(), Toast.LENGTH_SHORT).show();
                    return;
                }
                HttpAsyncTask post = new HttpAsyncTask();
                post.execute("http://teach-mate.azurewebsites.net/User/UpdateUser");
            }
        });

        return layout;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(FragmentTitles.UPDATE_PROFILE);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
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

            jsonObject.put("UserName", _editTextFirstName + " " + _editTextLastName);
            jsonObject.put("PhoneNumber", _editTextPhoneNumber);
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
            //.makeText(activity.getBaseContext(), "Data Sent! -" + result.toString(), Toast.LENGTH_LONG).show();

            if(result != null){
                if(!(result.isEmpty() || result.contains("ERROR"))){
                    Toast.makeText(activity.getApplicationContext(), "Update Successful", Toast.LENGTH_SHORT).show();

                    user.FirstName = _editTextFirstName;
                    user.LastName = _editTextLastName;
                    user.PhoneNumber = _editTextPhoneNumber;

                    UserModelDBHandler.UpdateUserData(activity.getApplicationContext(), user);

                    TempDataClass.userName = _editTextFirstName + " " + _editTextLastName;

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

}
