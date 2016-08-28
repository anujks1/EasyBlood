package com.teachmate.teachmate.SignUp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.EditText;
import android.widget.Toast;

import com.teachmate.teachmate.MainActivity;
import com.teachmate.teachmate.R;
import com.teachmate.teachmate.TempDataClass;
import com.teachmate.teachmate.models.UserModel;

public class NewSignUpActicity extends ActionBarActivity {

    public static UserModel userModel = new UserModel();

    onNextPressed my_interface;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_acticity);


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.containerFrame, new CredentialDetailsFragment(), "CredentialsDisplayFragment")
                    .addToBackStack(null)
                    .commit();
        }

        progressDialog = new ProgressDialog(NewSignUpActicity.this);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading..");
    }

    public void setInterface(onNextPressed my_interface){
        this.my_interface = my_interface;
    }

    public void showProgressDialog(){
        progressDialog.show();
    }

    public void dismissProgressDialog(){
        progressDialog.dismiss();
    }

    public void startMainActivity(){
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);

        finish();
    }

    public void setProgressDialogMessage(String msg){
        progressDialog.setMessage(msg);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up_acticity, menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class UserDetailsFragment extends Fragment implements onNextPressed {


        EditText editTextFirstName;
        EditText editTextLastName;
        EditText editTextPhoneNumber;
        EditText editTextProfession;

        String _editTextFirstName = "";
        String _editTextLastName = "";
        String _editTextPhoneNumber = "";
        String _editTextProfession = "";

        FragmentActivity activity;

        public UserDetailsFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            ((NewSignUpActicity) getActivity()).setInterface(this);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_sign_up_acticity, container, false);

            activity = (NewSignUpActicity) super.getActivity();

            editTextFirstName = (EditText) rootView.findViewById(R.id.editTextNewFirstName);
            editTextLastName = (EditText) rootView.findViewById(R.id.editTextNewLastName);
            editTextPhoneNumber = (EditText) rootView.findViewById(R.id.editTextNewMobileNumber);
            editTextProfession = (EditText) rootView.findViewById(R.id.editTextNewProfession);

            return rootView;
        }

        @Override
        public void saveCurrentData() {
            ((NewSignUpActicity) getActivity()).showProgressDialog();

            _editTextFirstName = editTextFirstName.getText().toString();
            if(_editTextFirstName.isEmpty()){
                ((NewSignUpActicity)getActivity()).dismissProgressDialog();
                editTextFirstName.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                Toast.makeText(activity.getApplicationContext(), "Please enter " + editTextFirstName.getHint().toString(), Toast.LENGTH_SHORT).show();
                return;
            }

            _editTextLastName = editTextLastName.getText().toString();
            if(_editTextLastName.isEmpty()){
                ((NewSignUpActicity)getActivity()).dismissProgressDialog();
                editTextLastName.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                Toast.makeText(activity.getApplicationContext(), "Please enter " + editTextLastName.getHint().toString(), Toast.LENGTH_SHORT).show();
                return;
            }

            _editTextPhoneNumber = editTextPhoneNumber.getText().toString();
            if(_editTextPhoneNumber.isEmpty()){
                ((NewSignUpActicity)getActivity()).dismissProgressDialog();
                editTextPhoneNumber.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                Toast.makeText(activity.getApplicationContext(), "Please enter " + editTextPhoneNumber.getHint().toString(), Toast.LENGTH_SHORT).show();
                return;
            }


            _editTextProfession = editTextProfession.getText().toString();
            if(_editTextProfession.isEmpty()){
                ((NewSignUpActicity)getActivity()).dismissProgressDialog();
                editTextProfession.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                Toast.makeText(activity.getApplicationContext(), "Please enter " + editTextProfession.getHint().toString(), Toast.LENGTH_SHORT).show();
                return;
            }

            NewSignUpActicity.userModel.FirstName = _editTextFirstName;
            NewSignUpActicity.userModel.LastName = _editTextLastName;
            NewSignUpActicity.userModel.PhoneNumber = _editTextPhoneNumber;
            NewSignUpActicity.userModel.Profession = _editTextProfession;

            final Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.containerFrame);

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    ((NewSignUpActicity)getActivity()).dismissProgressDialog();

                    currentFragment.onPause();
                    TempDataClass.signUpStack.push(currentFragment);

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.containerFrame, new ProfilePhotoPage(), "ProfilePhotoPage")
                            .addToBackStack("UserDetailsPage")
                            .commit();
                }
            }, 1000);
        }
    }

    public void Next(View v){
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.containerFrame);

        switch(currentFragment.getTag()){
            case "CredentialsDisplayFragment":
                my_interface.saveCurrentData();
                break;
            case "UserDetailsFragment":
                my_interface.saveCurrentData();
                break;
            case "ProfilePhotoPage":
                my_interface.saveCurrentData();
                break;
            case "LocationDetailsFragment":
                my_interface.saveCurrentData();
                break;
            default:
                break;
        }
    }

    public void BackPressed(View v){
        BackPressed();
    }

    public void BackPressed(){
        if(!(TempDataClass.signUpStack.size() == 0)) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.containerFrame, TempDataClass.signUpStack.lastElement());
            TempDataClass.signUpStack.lastElement().onResume();
            TempDataClass.signUpStack.pop();
            ft.commit();
        }
        else{
            finish();
        }
    }

    @Override
    public void onBackPressed(){
        BackPressed();
    }

}

