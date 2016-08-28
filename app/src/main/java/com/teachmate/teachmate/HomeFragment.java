package com.teachmate.teachmate;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.teachmate.teachmate.DBHandlers.QuestionModelDBHandler;
import com.teachmate.teachmate.DBHandlers.RequestsDBHandler;
import com.teachmate.teachmate.Requests.MyRequests;
import com.teachmate.teachmate.models.CategoryList;
import com.teachmate.teachmate.models.Question_Model;
import com.teachmate.teachmate.models.Requests;
import com.teachmate.teachmate.questions.Ask_question;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
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

public class HomeFragment extends Fragment {
    ArrayList<String> myquestionids = new ArrayList<String>();
    Question_Model myquestionsdb = new Question_Model();
    public String created_time;
    public String questionmessage;
    public String result;
    public String hour;
    public String minutes;
    public String month;
    public String day;
    public String seconds;
    public String year;
    public String category;
    public boolean isInCategory;
    public Calendar c=Calendar.getInstance();

    Button buttonNewRequest;
    Button buttonNewQuestion;

    String newRequestString;
    boolean isCurrentLocation;

    ProgressDialog progressDialog;

    ImageView profilePhoto;

    Requests newRequest;

    FragmentActivity activity;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.fragment_home, container, false);

        activity = (FragmentActivity) super.getActivity();

        TextView userName = (TextView) layout.findViewById(R.id.homeUserName);
        userName.setText(TempDataClass.userName);

        profilePhoto = (ImageView) layout.findViewById(R.id.imageViewProfilePhoto);

        if(TempDataClass.profilePhotoLocalPath.isEmpty()){
            Picasso.with(activity.getApplicationContext()).load(TempDataClass.profilePhotoServerPath).into(profilePhoto);
        }
        else{
            profilePhoto.setImageBitmap(BitmapFactory.decodeFile(TempDataClass.profilePhotoLocalPath));
        }

        buttonNewRequest = (Button) layout.findViewById(R.id.buttonNewRequest);
        buttonNewQuestion = (Button) layout.findViewById(R.id.buttonNewQuestion);
        buttonNewQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generatenewquestion();
            }
        });

        newRequest = new Requests();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Generating Request...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);

        buttonNewRequest.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                GenerateNewRequest();
            }
        });

        return layout;
    }

    public void generatenewquestion(){
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View promptview = factory.inflate(
                R.layout.ask_question_dialogue, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setView(promptview);
        alertDialog.setMessage("Ask a new Question!");
        final EditText etquestion=(EditText)promptview.findViewById(R.id.etquestion_ask_question);
        final AutoCompleteTextView etcategory=(AutoCompleteTextView)promptview.findViewById(R.id.etcategory_ask_question);
        final CategoryList categorylist1=new CategoryList();
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this.getActivity(),android.R.layout.simple_dropdown_item_1line,categorylist1.CATEGORIES);
        etcategory.setAdapter(adapter);
        promptview.findViewById(R.id.ask_button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //your business logic
                minutes=Integer.toString(c.get(Calendar.MINUTE));//getting time from calendar object
                hour=Integer.toString(c.get(Calendar.HOUR));
                seconds=Integer.toString(c.get(Calendar.SECOND));
                month=Integer.toString(c.get(Calendar.MONTH)+1);
                day=Integer.toString(c.get(Calendar.DAY_OF_MONTH));
                year=Integer.toString(c.get(Calendar.YEAR));
                created_time=day+"/"+month+"/"+year+" "+hour+":"+minutes+":"+seconds;
                questionmessage=etquestion.getText().toString();
                category=etcategory.getText().toString();
                isInCategory=categorylist1.checkinarray(categorylist1.CATEGORIES,category);
                if(isInCategory) {
                    if (TextUtils.isEmpty(category) || TextUtils.isEmpty(questionmessage)) {
                        Toast.makeText(getActivity(), "The above fields cannot be empty.Please fill both the fields and retry", Toast.LENGTH_LONG).show();
                    } else {
                        ask_question_async ask = new ask_question_async();
                        ask.execute("http://teach-mate.azurewebsites.net/QuestionForum/AddQuestion");
                        alertDialog.dismiss();

                    }
                }else{
                    Toast.makeText(getActivity(),"Please select an option from the defined categories",Toast.LENGTH_LONG).show();
                }




            }
        });
        promptview.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

            }
        });

        alertDialog.show();

    }

    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(FragmentTitles.HOME);
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
        //spinner1.setAdapter(mAdapter);

        final Switch locationSwitch = (Switch) promptsView.findViewById(R.id.switch1);


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
                            if(!locationSwitch.isChecked()){
                                isCurrentLocation = false;
                            }
                            else{
                                isCurrentLocation = true;
                            }
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

            Fragment nextFragment = new MyRequests();

            Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.container);

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            //TempDataClass.fragmentStack.lastElement().onPause();
            TempDataClass.fragmentStack.push(currentFragment);
            fragmentManager.beginTransaction()
                    .replace(R.id.container, nextFragment)
                    .commit();
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
    public class ask_question_async extends AsyncTask<String,Void,String> {
        ProgressDialog dialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            //
            super.onPreExecute();

            dialog.setProgressStyle(2);
            dialog.setMessage("Please wait while we post your Question.");
            dialog.show();

        }

        @Override
        protected String doInBackground(String... params) {
            InputStream inputStream = null;

            HttpClient httpClient=new DefaultHttpClient();
            HttpPost post= new HttpPost(params[0]);


            String json="";
            try {
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("UserId", TempDataClass.serverUserId);
                jsonObject.put("TimeOfQuestion",created_time);
                jsonObject.put("QuestionMessage",questionmessage);
                jsonObject.put("Category",category);
                jsonObject.put("QuestionBoardId","4");


                json=jsonObject.toString();
                StringEntity se=new StringEntity(json);
                post.setEntity(se);
                HttpResponse response=httpClient.execute(post);
                inputStream = response.getEntity().getContent();
                if(inputStream != null)

                    result = convertInputStreamToString(inputStream);
                else
                    result = "Did not work!";

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            myquestionsdb.setQuestion_id(s);
            myquestionsdb.setQuestion(questionmessage);
            myquestionsdb.setAsked_time(created_time);
            myquestionsdb.setUsername(TempDataClass.userName);
            myquestionsdb.setImage("image");
            myquestionsdb.setCategory(category);
            addtomyquestionsdb();

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.container, TempDataClass.fragmentStack.lastElement());
            TempDataClass.fragmentStack.pop();
            ft.commit();

        }

        private  String convertInputStreamToString(InputStream inputStream) throws IOException{
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
            String line = "";
            String result = "";
            while((line = bufferedReader.readLine()) != null)
                result += line;

            inputStream.close();
            return result;

        }

    }
    public void addtomyquestionsdb(){
        QuestionModelDBHandler addtodb=new QuestionModelDBHandler();
        // public static final AnswerModelDBHandler addanswertodb=new AnswerModelDBHandler();
        //  addtodb.InsertQuestionModel(getActivity(),dbadd);
        addtodb.Insertintomyquestionstable(getActivity(),myquestionsdb);
        //  addanswertodb.InsertAnswerList(getApplicationContext(),answerlist);


        Toast.makeText(getActivity(),"Question Posted Successfully",Toast.LENGTH_SHORT).show();

    }

}
