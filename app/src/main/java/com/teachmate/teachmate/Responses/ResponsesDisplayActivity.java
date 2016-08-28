package com.teachmate.teachmate.Responses;

import android.app.ProgressDialog;
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
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.teachmate.teachmate.R;
import com.teachmate.teachmate.Requests.ResponsesArrayAdapter;
import com.teachmate.teachmate.models.Responses;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class ResponsesDisplayActivity extends Fragment {

    ListView listViewRequests;
    ListAdapter listAdapter;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentActivity activity = (FragmentActivity) super.getActivity();
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.activity_response_display, container, false);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        listViewRequests = (ListView) layout.findViewById(R.id.listViewResponses);

/*        //Debug Code
        String result = "{'UserId':1,'Requests':[{'RequestId':1,'RequesteUserId':'2', 'RequestUserName':'Umang', 'RequestMessage':'Help me, baby!', 'RequestUserProfession':'Software Engineer', 'RequestUserProfilePhotoServerPath':'C:/profile.png', 'RequestedTime':'12/13/14 9.48 a.m.'},{'RequestId':2,'RequesteUserId':3, 'RequestUserName':'Anuj', 'RequestMessage':'Get me out of here', 'RequestUserProfession':'Priest', 'RequestUserProfilePhotoServerPath':'C:/profile.png', 'RequestedTime':'12/14/14 8.48 a.m.'}]}";
        List<Responses> list = GetObjectsFromResponse(result);
        if (list != null) {
            populateListView(list);
        }*/

        /*HttpGetter getter = new HttpGetter();
        getter.execute("http://10.163.180.110/doctool/Main/GetHelpRequests?id=1");*/

        return layout;
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

    private class HttpGetter extends AsyncTask<String, Void, String> {

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
            List<Responses> list = GetObjectsFromResponse(result);
            if(list != null){
                populateListView(list);
            }
        }
    }

    private void populateListView(List<Responses> list) {

        final Responses[] responsesArray = new Responses[list.size()];
        for(int i = 0; i < list.size(); i++){
            responsesArray[i] = list.get(i);
        }
        listAdapter = new ResponsesArrayAdapter(getActivity(), responsesArray);
        listViewRequests.setAdapter(listAdapter);

        listViewRequests.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View
                    view, int position, long id) {

                try {
                    Intent i = new Intent(getActivity().getApplicationContext(), ResponsesDisplayActivity.class);
                    i.putExtra("RequestId", responsesArray[position].RequestId);
                    i.putExtra("ResponseId", responsesArray[position].ResponseId);
                    i.putExtra("ResponseString", responsesArray[position].ResponseString);
                    i.putExtra("ResponseTime", responsesArray[position].ResponseTime);
                    i.putExtra("ResponseUserId", responsesArray[position].ResponseUserId);
                    i.putExtra("ResponseUserName", responsesArray[position].ResponseUserName);
                    i.putExtra("ResponseUserProfession", responsesArray[position].ResponseUserProfession);
                    i.putExtra("ResponseUserProfilePhotoServerPath", responsesArray[position].ResponseUserProfilePhotoServerPath);
                    startActivity(i);
                    //overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                }
                catch(Exception ex){
                    Toast.makeText(getActivity().getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
                }

            }

        });

    }

    private List<Responses> GetObjectsFromResponse(String result) {
        try {

            //JSONObject employee =(new JSONObject(response)).getJSONObject("Requests");
            JSONArray contacts = (new JSONObject(result)).getJSONArray("Responses");

            List<Responses> list = new ArrayList<Responses>();


            for(int i = 0; i < contacts.length(); i++){
                Responses response = new Responses();
                JSONObject temp = contacts.getJSONObject(i);

                response.RequestId = temp.getString("RequestId") != null ? temp.getString("RequestId") : null;
                response.ResponseId= temp.getString("ResponseId") != null ? temp.getString("ResponseId"): null;
                response.ResponseString= temp.getString("ResponseString") != null ? temp.getString("ResponseString"): null;
                response.ResponseUserId = temp.getString("ResponseUserId") != null ? temp.getString("ResponseUserId"): null;
                response.ResponseUserName = temp.getString("ResponseUserName") != null ? temp.getString("ResponseUserName"): null;
                response.ResponseUserProfession = temp.getString("ResponseUserProfession") != null ? temp.getString("ResponseUserProfession"): null;
                response.ResponseUserProfilePhotoServerPath = temp.getString("ResponseUserProfilePhotoServerPath") != null ? temp.getString("ResponseUserProfilePhotoServerPath"): null;

                list.add(response);

            }

            return list;
        }
        catch(Exception e){
            Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
    }
}
