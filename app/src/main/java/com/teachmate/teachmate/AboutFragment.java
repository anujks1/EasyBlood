package com.teachmate.teachmate;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.regex.Pattern;


public class AboutFragment extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.fragment_about, container, false);

        /*TextView link = (TextView) layout.findViewById(R.id.textViewFAQ);
        String linkText = "See the <a href='http://teach-mate.azurewebsites.net/' style='color: rgb(0,255,0)'><font color='#FFFFFF'>Frequently Asked Questions</font>.</a>";
        link.setText(Html.fromHtml(linkText));
        link.setLinkTextColor(Color.WHITE);
        link.setMovementMethod(LinkMovementMethod.getInstance());*/

        String text = "here.";
        TextView label = (TextView) layout.findViewById(R.id.textViewLink);
        label.setText(text);
        Pattern pattern = Pattern.compile("");
        label.setPaintFlags(label.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        Linkify.addLinks(label, pattern, "http://teach-mate.azurewebsites.net/");
        label.setLinkTextColor(Color.WHITE);

        return layout;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(FragmentTitles.ABOUT);
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

}
