package com.teachmate.teachmate.Requests;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.teachmate.teachmate.CommonMethods;
import com.teachmate.teachmate.DBHandlers.RequestsDBHandler;
import com.teachmate.teachmate.FragmentTitles;
import com.teachmate.teachmate.MainActivity;
import com.teachmate.teachmate.R;
import com.teachmate.teachmate.TempDataClass;
import com.teachmate.teachmate.models.Question_Model;
import com.teachmate.teachmate.models.Requests;
import com.teachmate.teachmate.questions.Question_Adapter;

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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;



public class RequestsDisplayActivity extends Fragment {

    ListView listViewRequests;
    ListAdapter listAdapter;

    String newRequestString;
    boolean isCurrentLocation;

    ProgressDialog progressDialog;

    Requests newRequest;

    FragmentActivity activity;

    RelativeLayout connectionLostLayout;

    private static List<Requests> resumeList = new ArrayList<Requests>();

    private boolean isFromOnResume = false;

    Button retryButton;

    public int lastviewposition;
    public int listCurrentPosition;

    int index;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = (FragmentActivity) super.getActivity();
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.activity_requests_display, container, false);

        newRequest = new Requests();
        listViewRequests = (ListView) layout.findViewById(R.id.listViewRequests);
        retryButton = (Button) layout.findViewById(R.id.buttonRetry);
        connectionLostLayout = (RelativeLayout) layout.findViewById(R.id.layout_connectionLost);

        Button loadmore = new Button(getActivity());
        loadmore.setBackgroundColor(Color.WHITE);
        loadmore.setTextColor(Color.BLACK);
        loadmore.setText("Load more items");
        listViewRequests.addFooterView(loadmore);

        loadmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Loading Requests...");
                progressDialog.show();
                listCurrentPosition = listViewRequests.getFirstVisiblePosition();
                lastviewposition = listViewRequests.getCount()-1;
                String lastRequestId;
                try {
                    lastRequestId = resumeList.get(listViewRequests.getCount() - 2).RequestID;

                    new loadmorelistview().execute("http://teach-mate.azurewebsites.net/Request/GetAllRequestsAssigned?id=" + TempDataClass.serverUserId + "&lastRequestId=" + lastRequestId);
                }catch(Exception ex){
                    progressDialog.dismiss();
                    //Log.e("Request", ex.getMessage());
                }



                //Toast.makeText(getActivity().getApplicationContext(), "" + listViewRequests.getCount(), Toast.LENGTH_LONG).show();
            }
        });

        setHasOptionsMenu(true);

        if(!isFromOnResume) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Loading Requests...");
            progressDialog.show();


            if(new CommonMethods().hasActiveInternetConnection(activity)){
                HttpGetter getter = new HttpGetter();
                getter.execute("http://teach-mate.azurewebsites.net/Request/GetAllRequestsAssigned?id=" + TempDataClass.serverUserId + "&lastRequestId=0");
            }
            else{
                progressDialog.dismiss();
                listViewRequests.setVisibility(View.INVISIBLE);
                connectionLostLayout.setVisibility(View.VISIBLE);
            }
        }else{
            isFromOnResume = false;
        }

        /*        //Debug Code
        String result = "{'UserId':1,'Requests':[{'RequestId':1,'RequesteUserId':'2', 'RequestUserName':'Umang', 'RequestMessage':'Help me, baby!', 'RequestUserProfession':'Software Engineer', 'RequestUserProfilePhotoServerPath':'C:/profile.png', 'RequestedTime':'12/13/14 9.48 a.m.'},{'RequestId':2,'RequesteUserId':3, 'RequestUserName':'Anuj', 'RequestMessage':'Get me out of here', 'RequestUserProfession':'Priest', 'RequestUserProfilePhotoServerPath':'C:/profile.png', 'RequestedTime':'12/14/14 8.48 a.m.'}]}";
        List<Requests> list = GetObjectsFromResponse(result);
        if(list != null){
            populateListView(list);
            progressDialog.dismiss();
        }*/

        retryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new RequestsDisplayActivity())
                        .commit();
            }
        });

        return layout;

    }

    @Override
    public void onResume(){
        super.onResume();
        isFromOnResume = true;
        populateListView(resumeList);
        listViewRequests.setSelectionFromTop(index, 0);
    }

    @Override
    public void onPause(){
        super.onPause();
        index = listViewRequests.getFirstVisiblePosition();
    }

    private void populateListView(List<Requests> list) {

        final Requests[] requestsArray = new Requests[list.size()];
        for(int i = 0; i < list.size(); i++){
            requestsArray[i] = list.get(i);
        }
        listAdapter = new RequestsArrayAdapter(getActivity(), requestsArray);
        listViewRequests.setAdapter(listAdapter);

        listViewRequests.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View
                    view, int position, long id) {

                try {
                    Bundle i = new Bundle();
                    i.putString("RequestID", requestsArray[position].RequestID);
                    i.putString("RequesteUserId", requestsArray[position].RequesteUserId);
                    i.putString("RequestUserName", requestsArray[position].RequestUserName);
                    i.putString("RequestString", requestsArray[position].RequestString);
                    i.putString("RequestUserProfession", requestsArray[position].RequestUserProfession);
                    i.putString("RequestUserProfilePhotoServerPath", requestsArray[position].RequestUserProfilePhotoServerPath);
                    i.putString("RequestTime", requestsArray[position].RequestTime);

                    Fragment individualRequestDisplayFragment = new RequestDisplayActivity();
                    individualRequestDisplayFragment.setArguments(i);

                    Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.container);

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    TempDataClass.fragmentStack.lastElement().onPause();
                    TempDataClass.fragmentStack.push(currentFragment);
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, individualRequestDisplayFragment)
                            .commit();
                }
                catch(Exception ex){
                    //.makeText(getActivity().getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
                    Log.e("Request", ex.getMessage());
                }

            }

        });

    }

    private List<Requests> GetObjectsFromResponse(String response) {
        try {

            //JSONObject employee =(new JSONObject(response)).getJSONObject("Requests");
            JSONArray contacts = (new JSONObject(response)).getJSONArray("Requests");

            List<Requests> list = new ArrayList<Requests>();


            for(int i = 0; i < contacts.length(); i++){
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
            //Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("Request", e.getMessage());
            return null;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(FragmentTitles.REQUESTS);
        //TODO
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_requests_display, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_request) {
            GenerateNewRequest();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void GenerateNewRequest() {
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.alert_prompt_new_request, null);

        ArrayList<String> array = new ArrayList<String>();
        array.add("Registered Locations");
        array.add("Current Locations");
        final Spinner spinner1;
        ArrayAdapter<String> mAdapter;
        //spinner1= (Spinner) promptsView.findViewById(R.id.spinnerLocationSelector);
        final EditText requestEditText = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
        //mAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.spinner_item, array);
        //mAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        //spinner1.setAdapter(mAdapter);

        final Switch locationSwicth = (Switch) promptsView.findViewById(R.id.switch1);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setMessage("Generate New Request!");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if(requestEditText.getText().toString().equals("") || requestEditText.getText() == null){
                            Toast.makeText(getActivity(), "Please enter a Request Message!", Toast.LENGTH_LONG).show();
                        }
                        else{
                            newRequestString = requestEditText.getText().toString();
                            //if(spinner1.getSelectedItem().toString().equals("Registered Locations")){
                            if(locationSwicth.isChecked()){
                                isCurrentLocation = false;
                            }
                            else{
                                isCurrentLocation = true;
                            }
                            progressDialog.setMessage("Generating Request...");
                            progressDialog.show();
                            HttpAsyncTaskPOST newPost = new HttpAsyncTaskPOST();
                            newPost.execute("http://teach-mate.azurewebsites.net/Request/SendRequestNotification");
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
            jsonObject.put("UserId", TempDataClass.serverUserId);
            newRequest.RequesteUserId = TempDataClass.serverUserId;
            //TODO
            jsonObject.put("RequestMessage", newRequestString);
            newRequest.RequestString = newRequestString;
            if(isCurrentLocation){
                jsonObject.put("IsCurrent", "true");
                jsonObject.put("Latitude", TempDataClass.currentLattitude);
                jsonObject.put("Longitude", TempDataClass.currentLongitude);
            }
            else {
                jsonObject.put("IsCurrent", "false");
                jsonObject.put("Longitude", 0);
                jsonObject.put("Longitude", 0);
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String currentDateandTime = sdf.format(new Date());
            newRequest.RequestTime = currentDateandTime;
            jsonObject.put("TimeOfRequest", currentDateandTime);



            //Code to get current date and month
            Calendar calendar = Calendar.getInstance();
            int cYear = calendar.get(Calendar.YEAR);
            int cDayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

            newRequest.requestYear = cYear;
            newRequest.requestDayOfTheYear = cDayOfYear;
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

    private class HttpAsyncTaskPOST extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return POST(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            newRequest.RequestID = result;
            newRequest.RequesteUserId = TempDataClass.serverUserId;
            newRequest.RequestUserName = TempDataClass.userName;
            newRequest.RequestUserProfession = TempDataClass.userProfession;
            RequestsDBHandler.InsertRequests(getActivity().getApplicationContext(), newRequest);
            Toast.makeText(getActivity().getApplicationContext(), "Request Generated Successfully!", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
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
            if(result != null && !result.isEmpty()) {
                if (!result.equals("Empty")) {
                    List<Requests> list = GetObjectsFromResponse(result);
                    resumeList = list;
                    if (list != null) {
                        populateListView(list);
                    }
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);
                    builder1.setTitle("Alert!");
                    builder1.setMessage("No New Requests found in Server!");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
            }else{
                progressDialog.dismiss();
                AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);
                builder1.setTitle("Alert!");
                builder1.setMessage("No New Requests found in Server!");
                builder1.setCancelable(true);
                builder1.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        }
    }

    public class loadmorelistview extends AsyncTask<String ,Void,String>{
        int current_position = listViewRequests.getFirstVisiblePosition();
        int lastpostiton = listViewRequests.getCount()-1;


        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            StringBuilder builder = new StringBuilder();
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(params[0]);
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
        protected void onPostExecute(String result) {
            if(result != null && !result.isEmpty()) {
                if (!result.equals("Empty")) {
                    List<Requests> list = GetObjectsFromResponse(result);
                    if (list != null) {
                        AddToListView(list);
                    }
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);
                    builder1.setTitle("Alert!");
                    builder1.setMessage("No more Requests found in Server!");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
            }else{
                progressDialog.dismiss();
                AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);
                builder1.setTitle("Alert!");
                builder1.setMessage("No more Requests found in Server!");
                builder1.setCancelable(true);
                builder1.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        }
    }

    private void AddToListView(List<Requests> list) {
        for(int i = 0; i < list.size(); i++){
            resumeList.add(list.get(i));
        }
        final Requests[] requestsArray = new Requests[resumeList.size()];
        for(int i = 0; i < resumeList.size(); i++){
            requestsArray[i] = resumeList.get(i);
        }
        listAdapter = new RequestsArrayAdapter(getActivity(), requestsArray);
        listViewRequests.setAdapter(listAdapter);

        listViewRequests.setSelectionFromTop(listCurrentPosition, 0);

        listViewRequests.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View
                    view, int position, long id) {

                try {
                    Bundle i = new Bundle();
                    i.putString("RequestID", requestsArray[position].RequestID);
                    i.putString("RequesteUserId", requestsArray[position].RequesteUserId);
                    i.putString("RequestUserName", requestsArray[position].RequestUserName);
                    i.putString("RequestString", requestsArray[position].RequestString);
                    i.putString("RequestUserProfession", requestsArray[position].RequestUserProfession);
                    i.putString("RequestUserProfilePhotoServerPath", requestsArray[position].RequestUserProfilePhotoServerPath);
                    i.putString("RequestTime", requestsArray[position].RequestTime);

                    Fragment individualRequestDisplayFragment = new RequestDisplayActivity();
                    individualRequestDisplayFragment.setArguments(i);

                    Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.container);

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    TempDataClass.fragmentStack.lastElement().onPause();
                    TempDataClass.fragmentStack.push(currentFragment);
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, individualRequestDisplayFragment)
                            .commit();
                }
                catch(Exception ex){
                    //Toast.makeText(getActivity().getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
                    Log.e("Request", ex.getMessage());
                }

            }

        });
    }

}
