package com.teachmate.teachmate.SignUp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.teachmate.teachmate.CommonMethods;
import com.teachmate.teachmate.R;
import com.teachmate.teachmate.TempDataClass;

import java.io.InputStream;

public class ProfilePhotoPage extends Fragment implements onNextPressed{

    ImageView image;
    InputStream inputStream;

    private static int RESULT_LOAD_IMAGE = 2;
    private final String FILEPATH = "FilePath";

    SharedPreferences prefs;

    private static String profilePath = "";

    public ProfilePhotoPage() {
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
        // Inflate the layout for this fragment
        FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.fragment_profile_photo_page, container, false);

        image = (ImageView) layout.findViewById(R.id.imageViewNewProfilePhoto);
        prefs = getActivity().getSharedPreferences("com.teachmate.teachmate", Context.MODE_PRIVATE);

        if(!NewSignUpActicity.userModel.profilePhotoLocalPath.isEmpty()){
            image.setImageBitmap(BitmapFactory.decodeFile(NewSignUpActicity.userModel.profilePhotoLocalPath));
        }

        image.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, 2);
            }
        });

        return layout;
    }

    @Override
    public void onResume(){
        super.onResume();
        if(!NewSignUpActicity.userModel.profilePhotoLocalPath.isEmpty()){
            image.setImageBitmap(BitmapFactory.decodeFile(NewSignUpActicity.userModel.profilePhotoLocalPath));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == -1 && null != data) {
            Uri SelectedImage = data.getData();
            String[] FilePathColumn = {MediaStore.Images.Media.DATA };

            Cursor SelectedCursor = getActivity().getContentResolver().query(SelectedImage, FilePathColumn, null, null, null);
            SelectedCursor.moveToFirst();

            int columnIndex = SelectedCursor.getColumnIndex(FilePathColumn[0]);
            String picturePath = SelectedCursor.getString(columnIndex);
            profilePath = picturePath;
            SelectedCursor.close();

            image.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            //CommonMethods.scaleImage(getActivity().getApplicationContext(), image, 100);
            //Toast.makeText(getApplicationContext(), picturePath, Toast.LENGTH_SHORT).show();

        }
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

        if(!profilePath.isEmpty()) {
            NewSignUpActicity.userModel.profilePhotoLocalPath = profilePath;
        }
        else {
            ((NewSignUpActicity) getActivity()).dismissProgressDialog();
            AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
            builder1.setTitle("Alert!");
            builder1.setMessage("Are you sure you do not want to select a profile photo?");
            builder1.setCancelable(true);
            builder1.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            builder1.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id){
                            dialog.dismiss();
                            TempDataClass.newSignUpProfilePhotoReturn = true;
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }

        if(TempDataClass.newSignUpProfilePhotoReturn){
            return;
        }

        Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.containerFrame);
        currentFragment.onPause();
        TempDataClass.signUpStack.push(currentFragment);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                ((NewSignUpActicity)getActivity()).dismissProgressDialog();

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.containerFrame, new LocationDetailsFragment(), "LocationDetailsFragment")
                        .commit();
            }
        }, 1000);


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

}
