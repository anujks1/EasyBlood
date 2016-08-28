package com.teachmate.teachmate.Answers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.teachmate.teachmate.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by archon on 11-01-2015.
 */
public class answer_question extends Fragment{
    EditText etanswer;
    Button postanswerbutton;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentActivity fragmentActivity=(FragmentActivity)super.getActivity();
        RelativeLayout relativeLayout=(RelativeLayout)inflater.inflate(R.layout.activity_answer_question,container,false);
        etanswer=(EditText)relativeLayout.findViewById(R.id.etanswer);
        //String questionid=getIntent().getExtras().getString("question_id");
        postanswerbutton=(Button)relativeLayout.findViewById(R.id.postanswerbutton);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /* @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_answer_question);
         etanswer=(EditText)findViewById(R.id.etanswer);
         //String questionid=getIntent().getExtras().getString("question_id");
         postanswerbutton=(Button)findViewById(R.id.postanswerbutton);


     }*/
    public void postthis(View v){
        answer_async_task tell=new answer_async_task();
        String questionid=getActivity().getIntent().getExtras().getString("question_id");
        tell.execute("http://10.163.179.199:8222/MvcApplication1/Enigma/PostQuestion");//My server url to hit
        Toast.makeText(getActivity().getApplicationContext(), "successfully posted", Toast.LENGTH_LONG).show();
        getActivity().finish();
        if(TextUtils.isEmpty(etanswer.getText().toString())) {
            Toast.makeText(getActivity().getApplicationContext(),"please set a value so that you can proceed dumbass!",Toast.LENGTH_LONG).show();
        }
        else{
            prompt newprompt= new prompt();
            newprompt.show(getActivity().getFragmentManager(),"post");}
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
    public class prompt extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
            builder.setMessage("Are you sure you want to post this as an answer?")
                    .setPositiveButton("Yeah Baby Post me", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Create a new async task here....depends on jere
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();

        }
    }
    public class answer_async_task extends AsyncTask<String,Void,Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            HttpClient httpClient=new DefaultHttpClient();
            String questionid=getActivity().getIntent().getExtras().getString("question_id");
            HttpPost post= new HttpPost(params[0]);
            String json="";
            try {
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("QuestionId",questionid);
                jsonObject.put("AnswerMessage",etanswer.getText().toString());
                jsonObject.put("QuestionBoardId",1);
                //jsonObject.put("Date",created_time);
                json=jsonObject.toString();
                StringEntity se=new StringEntity(json);
                //se.setContentType("application/json");
                post.setEntity(se);
                HttpResponse response=httpClient.execute(post);
                int statuscode=response.getStatusLine().getStatusCode();
                HttpEntity response_entity=response.getEntity();
                String rquestion_id= EntityUtils.toString(response_entity);
                /*JSONObject question_id=new JSONObject(rquestion_id);
                //myquestionids.add(question_id.get("question_id").toString());*/




            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
