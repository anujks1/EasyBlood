package com.teachmate.teachmate;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.teachmate.teachmate.Answers.Answer_Adapter;
import com.teachmate.teachmate.DBHandlers.AnswerModelDBHandler;
import com.teachmate.teachmate.DBHandlers.NotifsTableDbHandler;
import com.teachmate.teachmate.DBHandlers.QuestionModelDBHandler;
import com.teachmate.teachmate.models.Answer_Model;
import com.teachmate.teachmate.models.Question_Model;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
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
import java.util.ArrayList;
import java.util.Calendar;


public class clicked_activity extends ActionBarActivity {

    ListView l;
    QuestionModelDBHandler addtodb=new QuestionModelDBHandler();

    Answer_Adapter answer_adapter;
    public String question,value,qid1,imageurl,asked_time,username,category;
    ArrayList<Answer_Model> answerlist;
     AnswerModelDBHandler addanswertodb=new AnswerModelDBHandler();
    String latestanswerid;

    Question_Model dbadd=new Question_Model();
    Answer_Model answer=new Answer_Model();
    TextView tvusername,tvquestion,tvaskedtime,tvquestion_id1;
    ImageView imageView;
    Button addtodbbtn;
    public String created_time;
    public String hour;
    public String minutes;
    public String month;
    public String day;
    public String seconds;
    public String year;
    public Calendar c=Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clicked_activity);
        tvusername=(TextView)findViewById(R.id.tvusername);
        addtodbbtn=(Button)findViewById(R.id.readlater);
        tvquestion=(TextView)findViewById(R.id.tvquestion);
        tvaskedtime=(TextView)findViewById(R.id.tvaskedtime);
        tvquestion_id1=(TextView)findViewById(R.id.tvquestion_id1);

        imageView=(ImageView)findViewById(R.id.imageView);
        l = (ListView) findViewById(R.id.listView2);
        answerlist = new ArrayList<>();

        username=getIntent().getExtras().getString("username");
        question=getIntent().getExtras().getString("question");
        category=getIntent().getExtras().getString("category");
        asked_time=getIntent().getExtras().getString("asked_time");
        qid1=getIntent().getExtras().getString("question_id");
        imageurl=getIntent().getExtras().getString("image");
        Picasso.with(this).load(imageurl).into(imageView);
        new answerfeed().execute("http://teach-mate.azurewebsites.net/QuestionForum/RetrieveRepliesForAQuestion?id="+qid1);



        tvusername.setText(username);
        tvquestion.setText(question);
        tvquestion_id1.setText(qid1);
        tvaskedtime.setText(asked_time);





        dbadd.setUsername(username);
        dbadd.setQuestion(question);
        dbadd.setAsked_time(asked_time);
        dbadd.setQuestion_id(qid1);
        dbadd.setImage(imageurl);
        dbadd.setCategory(category);






    }
    public void addtodb(View v){

        addtodb.InsertQuestionModel(getApplicationContext(),dbadd);
        addanswertodb.InsertAnswerList(getApplicationContext(),answerlist);


        Toast.makeText(getApplicationContext(),"added to db",Toast.LENGTH_SHORT).show();

    }
    public void answer_this_question(View v){
        AlertDialog.Builder alert=new AlertDialog.Builder(this);
        alert.setTitle("Your Answer");
        alert.setMessage("Question"+question);


        final EditText input=new EditText(this);

        alert.setView(input);

        alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              value=input.getText().toString();
                if(answerlist.isEmpty()){
                    latestanswerid="1";
                }
                else{
                    latestanswerid=answerlist.get(l.getFirstVisiblePosition()).getAnswer_id();
                }
                minutes=Integer.toString(c.get(Calendar.MINUTE));
                seconds=Integer.toString(c.get(Calendar.SECOND));
                month=Integer.toString(c.get(Calendar.MONTH)+1);
                hour=Integer.toString(c.get(Calendar.HOUR_OF_DAY));
                day=Integer.toString(c.get(Calendar.DAY_OF_MONTH));
                year=Integer.toString(c.get(Calendar.YEAR));
                created_time=day+"/"+month+"/"+year+" "+hour+":"+minutes+":"+seconds;
                //do something with value
                new AddReply().execute("http://teach-mate.azurewebsites.net/QuestionForum/AddReply");

            }
        });

        alert.setNegativeButton("cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //canceled
            }
        });

        alert.show();

    }



    public class answerfeed extends AsyncTask<String, Void, Boolean> {


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
                    if(data.length()==0){
                        return false;
                    }

                    JSONObject jobj = new JSONObject(data);
                    JSONArray jarray = jobj.getJSONArray("Replies");
                    for (int i = 0; i < jarray.length(); i++) {
                        JSONObject real=jarray.getJSONObject(i);

                        Answer_Model answer = new Answer_Model();

                        answer.setActualanswer(real.getString("ReplyMessage"));
                       answer.setAnsweredby(real.getString("RepliedBy"));//jobj.getString("validate")
                        answer.setAnswer_id(real.getString("ReplyId"));
                       answer.setAnsweredtime(real.getString("RepliedTime"));
                        answer.setQuestion_id(qid1);
                       /* answer.setAnsweredby("me");
                        answer.setAnsweredtime("now");
                        answer.setAnswer_id("1");
                        answer.setQuestion_id(qid1);*/

                        answerlist.add(answer);


                    }
                    return true;
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);


            if (aBoolean == false) {
                Toast.makeText(getApplicationContext(), "No answers for this question", Toast.LENGTH_LONG).show();
            } else {
                Answer_Adapter answer_adapter1 = new Answer_Adapter(getApplicationContext(), R.layout.answer_single, answerlist);
                l.setAdapter(answer_adapter1);



            }
        }

    }
    public class AddReply extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... params) {
            InputStream inputStream = null;
            String result = "";
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost((params[0]));

                JSONObject replyobj = new JSONObject();
                replyobj.put("ReplyMessage", value);
                replyobj.put("TimeOfReply",created_time);
                replyobj.put("UserID","19");
                replyobj.put("QuestionID",qid1);
                String replyjson="";
                replyjson=replyobj.toString();

                StringEntity se=new StringEntity(replyjson);
                httpPost.setEntity(se);
                HttpResponse httpResponse = httpClient.execute(httpPost);
                inputStream = httpResponse.getEntity().getContent();
                if(inputStream != null)
                    result = convertInputStreamToString(inputStream);
                else
                    result = "Did not work!";


            }catch (Exception e){
                e.printStackTrace();
            }
            return result;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            NotifsTableDbHandler notifsTableDbHandler=new NotifsTableDbHandler();
            notifsTableDbHandler.addtonotifsdb(getApplicationContext(),qid1,latestanswerid);
            Toast.makeText(getApplicationContext(),"Added to notifs db "+latestanswerid,Toast.LENGTH_SHORT).show();
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
}
