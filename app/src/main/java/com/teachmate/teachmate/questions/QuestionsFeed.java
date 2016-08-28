package com.teachmate.teachmate.questions;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.teachmate.teachmate.FragmentTitles;
import com.teachmate.teachmate.MainActivity;
import com.teachmate.teachmate.R;
import com.teachmate.teachmate.TempDataClass;
import com.teachmate.teachmate.clicked;
import com.teachmate.teachmate.models.Question_Model;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by archon on 11-01-2015.
 */
public class QuestionsFeed extends Fragment  {
//will need to implement SwipeRefreshLayout.OnRefreshListener if swipe view is necessary
    public ListView list;
    public String imageurl;
    EditText etsearch;
    ImageView imageView;
    Question_Adapter question_adapter;
    public int REFRESH_TIME_IN_SECONDS = 5;
    public int lastviewposition;
    //SwipeRefreshLayout swipeRefreshLayout;
    public ArrayList<Question_Model> questionlist;
    TextView tvquestionid;
    Boolean flag = true;


    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(FragmentTitles.QUESTIONS_FEED);


    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.questions_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_question) {
            Fragment nextFragment = new Ask_question();

            Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.container);

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            TempDataClass.fragmentStack.lastElement().onPause();
            TempDataClass.fragmentStack.push(currentFragment);
            fragmentManager.beginTransaction()
                    .replace(R.id.container, nextFragment)
                    .commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        FragmentActivity faActivity  = (FragmentActivity) super.getActivity();


        RelativeLayout relativeLayout=(RelativeLayout)inflater.inflate(R.layout.mainlistview,container,false);
      //  swipeRefreshLayout = (SwipeRefreshLayout)relativeLayout.findViewById(R.id.lySwipeRefresh);

        setHasOptionsMenu(true);

        imageView = (ImageView)relativeLayout.findViewById(R.id.imageView);
        list = (ListView)relativeLayout.findViewById(R.id.listView);
        etsearch = (EditText)relativeLayout.findViewById(R.id.etsearch);

        Button loadmore=new Button(getActivity());

        loadmore.setText(faActivity.getString(R.string.Load_More_Items));
        loadmore.setBackgroundColor(Color.WHITE);
        loadmore.setTextColor(Color.BLACK);
        list.addFooterView(loadmore);


        loadmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current_position=list.getFirstVisiblePosition();
                lastviewposition =list.getCount()-1;
                String lastquestionid;
                lastquestionid = questionlist.get(list.getCount()-2).getQuestion_id();

                new loadmorelistview().execute("http://teach-mate.azurewebsites.net/QuestionForum/RetrieveQuestions?lastQuestionId="+lastquestionid);



               // Toast.makeText(getActivity().getApplicationContext(), "" + list.getCount(), Toast.LENGTH_LONG).show();
            }
        });


       /* swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorScheme(android.R.color.holo_blue_light,
                android.R.color.white, android.R.color.holo_blue_light,
                android.R.color.white);
*/
        etsearch.setVisibility(View.GONE);
        questionlist = new ArrayList<Question_Model>();
        list.invalidateViews();
        etsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int textlength = s.length();
                ArrayList<Question_Model> tempArrayList = new ArrayList<Question_Model>();
                for(Question_Model c: questionlist){
                    if (textlength <= (c.getQuestion().length())) {
                        if((c.getQuestion().toLowerCase().contains(s.toString().toLowerCase()))||(c.getUsername().toLowerCase().contains(s.toString().toLowerCase()))) {
                            tempArrayList.add(c);
                        }
                    }
                }
              Question_Adapter mAdapter = new Question_Adapter(getActivity().getApplicationContext(),R.layout.singlerow, tempArrayList);

                list.setAdapter(mAdapter);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        try {
            new questionfeed().execute("http://teach-mate.azurewebsites.net/QuestionForum/RetrieveQuestions?lastQuestionId=0");
        } catch (Exception e) {
            e.printStackTrace();
        }


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String user=questionlist.get(position).getUsername();
                String qid=questionlist.get(position).getQuestion_id();
                String url=questionlist.get(position).getImage();
                String quest=questionlist.get(position).getQuestion();
                String category=questionlist.get(position).getCategory();
                String asked=questionlist.get(position).getAsked_time();
                String askedagain=questionlist.get(position).getAsked_time();





                Bundle data=new Bundle();
                data.putString("username", user);
                data.putString("category",category);
                data.putString("question", quest);
                data.putString("asked_time", asked);
                data.putString("question_id", qid);
                data.putString("image", url);

                Fragment clickedfragment = new clicked();
                clickedfragment.setArguments(data);

                Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.container);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                TempDataClass.fragmentStack.lastElement().onPause();
                TempDataClass.fragmentStack.push(currentFragment);
                fragmentManager.beginTransaction()
                        .replace(R.id.container, clickedfragment)
                        .commit();


            }
        });

        return relativeLayout;
    }

    /*@Override
    public void onRefresh() {
        questionlist.clear();
        new questionfeed().execute("http://teach-mate.azurewebsites.net/QuestionForum/RetrieveQuestions?lastQuestionId=0");

    }
    private void stopSwipeRefresh() {
        swipeRefreshLayout.setRefreshing(false);
    }
*/




    public class questionfeed extends AsyncTask<String, Void, Boolean> {

        // ImageView imageView = (ImageView)relativeLayout.findViewById(R.id.imageView);
        ProgressDialog dialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            //
            super.onPreExecute();

            dialog.setProgressStyle(2);
            dialog.setMessage(getString(R.string.Loading));
            dialog.show();

        }

        @Override
        protected Boolean doInBackground(String... params) {

            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(params[0]);
                HttpResponse response = client.execute(httpGet);
                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);

                    JSONObject jobj = new JSONObject(data);
                    JSONArray jarray = jobj.getJSONArray("Questions");
                    for (int i = 0; i < jarray.length(); i++) {
                        Question_Model question = new Question_Model();
                        JSONObject real = jarray.getJSONObject(i);

                        question.setQuestion_id(real.getString("QuestionId"));//this would be the question id
                        if(real.getString("UserProfilePhotoServerPath").length()==0){
                            question.setImage("");
                        }
                        question.setImage(real.getString("UserProfilePhotoServerPath"));

                     //   question.setImage("http://eraser2.heidi.ie/wp-content/plugins/all-in-one-seo-pack-pro/images/default-user-image.png");
                        question.setUsername(real.getString("AskedBy"));//this is the username of the person who asked the question
                        question.setQuestion(real.getString("QuestionMessage"));//jobj.getString("validate")//this would be the question itself
                        question.setQuestion_id(real.getString("QuestionId"));
                        String categorycheck=real.getString("Category");
                        if(categorycheck.length()==0){
                            question.setCategory("Category : General Question");
                        }
                        question.setCategory(real.getString("Category"));
                      //  question.setCategory("category");
                        question.setAsked_time(real.getString("AskedTime"));//this is the time the question was asked

                        questionlist.add(question);

                    }

                    return true;
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            dialog.dismiss();

            if (aBoolean == false) {
                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.No_Questions_Found), Toast.LENGTH_LONG).show();

            } else {
                final   Question_Adapter question_adapter = new Question_Adapter(getActivity(),R.layout.singlerow, questionlist);

                Parcelable state = list.onSaveInstanceState();


                list.setAdapter(question_adapter);
                list.setTextFilterEnabled(true);
             //   Toast.makeText(getActivity().getApplicationContext(), "" + list.getCount(), Toast.LENGTH_LONG).show();
                //stopSwipeRefresh();
                list.onRestoreInstanceState(state);

                //question_adapter.notifyDataSetChanged();
            }
        }
    }


    public class loadmorelistview extends AsyncTask<String ,Void,Boolean>{
        int current_position=list.getFirstVisiblePosition();
        int lastpostiton=list.getCount()-1;
        ProgressDialog dialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            //
            super.onPreExecute();

            dialog.setProgressStyle(2);
            dialog.setMessage(getString(R.string.Loading_More_Questions));
            dialog.show();

        }


        @Override
        protected Boolean doInBackground(String... params) {
            try {
                HttpClient client = new DefaultHttpClient();
               // HttpPost httpPost=new HttpPost(params[0]);
                HttpGet httpGet = new HttpGet(params[0]);
                HttpResponse response = client.execute(httpGet);
                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);

                    JSONObject jobj = new JSONObject(data);
                    JSONArray jarray = jobj.getJSONArray("Questions");
                    for (int i = 0; i < jarray.length(); i++) {
                        Question_Model question1 = new Question_Model();
                        JSONObject real = jarray.getJSONObject(i);

                        question1.setQuestion_id(real.getString("QuestionId"));//this would be the question id
                        if(real.getString("UserProfilePhotoServerPath").length()==0){
                            question1.setImage("");

                        }else{
                            question1.setImage(real.getString("UserProfilePhotoServerPath"));}
                        question1.setUsername(real.getString("AskedBy"));
                        question1.setQuestion(real.getString("QuestionMessage"));
                        question1.setQuestion_id(real.getString("QuestionId"));
                        question1.setCategory(real.getString("Category"));
                        question1.setAsked_time(real.getString("AskedTime"));//this is the time the question was asked

                        questionlist.add(question1);

                    }

                    return true;
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }




            return null;
        }
        protected void onPostExecute(Boolean aboolean) {
            super.onPostExecute(aboolean);
            dialog.dismiss();
            Question_Adapter question_adapter = new Question_Adapter(getActivity().getApplicationContext(), R.layout.singlerow,questionlist);

            list.setAdapter(question_adapter);
            question_adapter.notifyDataSetChanged();
            list.setSelectionFromTop(lastviewposition + 1, 0);
          //  Toast.makeText(getActivity().getApplicationContext(), "" + list.getCount(), Toast.LENGTH_LONG).show();
        }
    }
}
