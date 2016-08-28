package com.teachmate.teachmate.SignUp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.teachmate.teachmate.LoginActivity;
import com.teachmate.teachmate.R;
import com.teachmate.teachmate.TempDataClass;


public class CredentialDetailsFragment extends Fragment implements onNextPressed {

    Activity activity;

    EditText editTextEmailId;
    EditText editTextPassword;
    EditText editTextReEnteredPassword;

    String _editTextEmailId = "";
    String _editTextPassword = "";
    String _editTextReEnteredPassword = "";

    public CredentialDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((NewSignUpActicity) getActivity()).setInterface(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_credential_details, container, false);

        activity = (NewSignUpActicity) super.getActivity();

        editTextEmailId = (EditText) layout.findViewById(R.id.editTextNewEmail);
        editTextPassword = (EditText) layout.findViewById(R.id.editTextNewPassword);
        editTextReEnteredPassword = (EditText) layout.findViewById(R.id.editTextNewReEnteredPassword);


        return layout;
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();
        ((NewSignUpActicity) getActivity()).setInterface(this);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void saveCurrentData() {
        ((NewSignUpActicity)getActivity()).showProgressDialog();

        _editTextEmailId = editTextEmailId.getText().toString();
        if(_editTextEmailId.isEmpty()){
            ((NewSignUpActicity)getActivity()).dismissProgressDialog();
            editTextEmailId.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            Toast.makeText(activity.getApplicationContext(), "Please enter " + editTextEmailId.getHint().toString(), Toast.LENGTH_SHORT).show();
            return;
        }

        _editTextPassword = editTextPassword.getText().toString();
        if(_editTextPassword.isEmpty()){
            ((NewSignUpActicity)getActivity()).dismissProgressDialog();
            editTextPassword.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            Toast.makeText(activity.getApplicationContext(), "Please enter " + editTextPassword.getHint().toString(), Toast.LENGTH_SHORT).show();
            return;
        }

        _editTextReEnteredPassword = editTextReEnteredPassword.getText().toString();
        if(_editTextReEnteredPassword.isEmpty()){
            ((NewSignUpActicity)getActivity()).dismissProgressDialog();
            editTextReEnteredPassword.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            Toast.makeText(activity.getApplicationContext(), "Please " + editTextReEnteredPassword.getHint().toString(), Toast.LENGTH_SHORT).show();
            return;
        }

        if(!_editTextPassword.equals(_editTextReEnteredPassword)){
            ((NewSignUpActicity)getActivity()).dismissProgressDialog();
            Toast.makeText(activity.getApplicationContext(), "Passwords do not match. Please Re-Enter Passwords", Toast.LENGTH_SHORT).show();
            return;
        }

        NewSignUpActicity.userModel.EmailId = _editTextEmailId;
        NewSignUpActicity.userModel.password = _editTextPassword;

        Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.containerFrame);


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                ((NewSignUpActicity)getActivity()).dismissProgressDialog();
            }
        }, 1000);

        currentFragment.onPause();
        TempDataClass.signUpStack.push(currentFragment);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerFrame, new NewSignUpActicity.UserDetailsFragment(), "UserDetailsFragment")
                .commit();

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     *
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
