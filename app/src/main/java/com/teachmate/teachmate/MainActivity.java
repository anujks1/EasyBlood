package com.teachmate.teachmate;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.teachmate.teachmate.Chat.PreviousChatFragment;
import com.teachmate.teachmate.DBHandlers.DeviceInfoDBHandler;
import com.teachmate.teachmate.DBHandlers.UserModelDBHandler;
import com.teachmate.teachmate.Requests.MyRequests;
import com.teachmate.teachmate.Requests.RequestDisplayActivity;
import com.teachmate.teachmate.Requests.RequestsDisplayActivity;
import com.teachmate.teachmate.Responses.ResponseDisplayActivity;
import com.teachmate.teachmate.models.DeviceInfoKeys;
import com.teachmate.teachmate.models.UserModel;
import com.teachmate.teachmate.questions.MyQuestions;
import com.teachmate.teachmate.questions.QuestionsFeed;
import com.teachmate.teachmate.questions.SavedForOfflineReading;

import java.util.Stack;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,PreviousChatFragment.OnFragmentInteractionListener {

    Fragment initialFragment;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;



    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private boolean isThroughNotification = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String type = "";
        try {
            type = getIntent().getStringExtra("type");
            isThroughNotification = true;
        }catch(Exception ex){
            type = "";
            isThroughNotification = false;
        }
        if(type == null) {
            type = "";
            isThroughNotification = false;
        }
        UserModel user = UserModelDBHandler.ReturnValue(getApplicationContext());
        TempDataClass.userName = user.FirstName + " " + user.LastName;
        TempDataClass.serverUserId = user.ServerUserId;
        TempDataClass.userProfession = user.Profession;
        TempDataClass.emailId = user.EmailId;

        setContentView(R.layout.activity_main);

        String profilePhotoPath = DeviceInfoDBHandler.GetValueForKey(getApplicationContext(), DeviceInfoKeys.PROFILE_PHOTO_LOCAL_PATH);
        if(profilePhotoPath != null && !profilePhotoPath.isEmpty()) {
            TempDataClass.profilePhotoLocalPath = profilePhotoPath;
            TempDataClass.profilePhotoServerPath = "http://teach-mate.azurewebsites.net/MyImages/" + TempDataClass.serverUserId + ".jpg";
        }
        else{
            TempDataClass.profilePhotoLocalPath = "";
        }

        profilePhotoPath = DeviceInfoDBHandler.GetValueForKey(getApplicationContext(), DeviceInfoKeys.PROFILE_PHOTO_SERVER_PATH);
        if(profilePhotoPath != null && !profilePhotoPath.isEmpty()) {
            TempDataClass.profilePhotoServerPath = profilePhotoPath;
        }else{
            TempDataClass.profilePhotoServerPath = "http://teach-mate.azurewebsites.net/MyImages/default.jpg";
        }

        Bundle extras = new Bundle();

        switch (type){
            case "Replies":
                extras.putString("type",getIntent().getStringExtra("type"));
                extras.putString("Category",getIntent().getStringExtra("Category"));
                extras.putString("askedby",getIntent().getStringExtra("askedby"));
                extras.putString("userid_questions",getIntent().getStringExtra("userid_questions"));
                extras.putString("questionmessage",getIntent().getStringExtra("questionmessage"));
                extras.putString("asked_time_questions",getIntent().getStringExtra("asked_time_questions"));
                extras.putString("imagepath",getIntent().getStringExtra("imagepath"));
                extras.putString("questionid",getIntent().getStringExtra("questionid"));
                extras.putString("userprofession_questions",getIntent().getStringExtra("userprofession_questions"));
                initialFragment=new clicked();
                initialFragment.setArguments(extras);
                break;
            case "request":
                extras.putString("NotificationRequestId", getIntent().getStringExtra("NotificationRequestId"));
                initialFragment = new RequestDisplayActivity();
                initialFragment.setArguments(extras);
                break;
            case "response":
                extras.putString("NotificationResponseId", getIntent().getStringExtra("NotificationResponseId"));
                extras.putString("NotificationRequestId", getIntent().getStringExtra("NotificationRequestId"));
                extras.putString("NotificationResponseUserId", getIntent().getStringExtra("NotificationResponseUserId"));
                extras.putString("NotificationResponseUserName", getIntent().getStringExtra("NotificationResponseUserName"));
                extras.putString("NotificationResponseMessage", getIntent().getStringExtra("NotificationResponseMessage"));
                extras.putString("NotificationResponseUserProfession", getIntent().getStringExtra("NotificationResponseUserProfession"));
                extras.putString("NotificationResponseUserProfilePhotoServerPath", getIntent().getStringExtra("NotificationResponseUserProfilePhotoServerPath"));
                initialFragment = new ResponseDisplayActivity();
                initialFragment.setArguments(extras);
                break;
            default:
                initialFragment = new HomeFragment();
                break;
        }

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        if (isThroughNotification) {
            replaceFragment();
            isThroughNotification = false;
        }
    }

    @Override
    public void onBackPressed(){
        if(!(TempDataClass.fragmentStack.size() == 0)) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.container, TempDataClass.fragmentStack.lastElement());
            TempDataClass.fragmentStack.pop();
            ft.commit();
        }
        else{
            finish();
        }
    }

    private void replaceFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment temp = initialFragment;
        if(!TempDataClass.fragmentStack.lastElement().equals(new HomeFragment()) && TempDataClass.fragmentStack.size() != 1) {
            TempDataClass.fragmentStack.push(temp);
        }
        fragmentManager.beginTransaction()
                .replace(R.id.container, initialFragment)
                .commit();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        TempDataClass.fragmentStack = new Stack<Fragment>();
        TempDataClass.fragmentStack.push(new HomeFragment());
        switch(position) {
            case 0:
                initialFragment = new HomeFragment();
                break;
            case 1:
                initialFragment = new RequestsDisplayActivity();
                break;
            case 2:
                initialFragment = new MyRequests();
                break;
            case 5:
                initialFragment = new PreviousChatFragment();
                break;
            case 3:
                initialFragment = new QuestionsFeed();
                break;
            case 4:
                initialFragment = new MyQuestions();
                break;
            case 7:
                initialFragment = new SavedForOfflineReading();
                break;
            case 6:
                initialFragment = new AboutFragment();
                break;
            default:
                initialFragment = new HomeFragment();
                break;
        }

        if(!isThroughNotification) {
            replaceFragment();
        }

    }

    public void onSectionAttached(String title) {
        switch (title) {
            case FragmentTitles.REQUESTS:
                mTitle = FragmentTitles.REQUESTS;
                break;
            case FragmentTitles.MY_REQUESTS:
                mTitle = FragmentTitles.MY_REQUESTS;
                break;
            case FragmentTitles.HOME:
                mTitle = FragmentTitles.HOME;
                break;
            case FragmentTitles.CHAT:
                mTitle = FragmentTitles.CHAT;
                break;
            case FragmentTitles.TEACH_MATE:
                mTitle = FragmentTitles.TEACH_MATE;
                break;
            case FragmentTitles.QUESTIONS_FEED:
                mTitle = FragmentTitles.QUESTIONS_FEED;
                break;
            case FragmentTitles.MY_QUESTIONS:
                mTitle = FragmentTitles.MY_QUESTIONS;
                break;
            case FragmentTitles.ABOUT:
                mTitle = FragmentTitles.ABOUT;
                break;
            case FragmentTitles.RESPONSES:
                mTitle = FragmentTitles.RESPONSES;
                break;
            case FragmentTitles.REQUEST:
                mTitle = FragmentTitles.REQUEST;
                break;
            case FragmentTitles.CHANGE_PASSWORD:
                mTitle = FragmentTitles.CHANGE_PASSWORD;
                break;
            case FragmentTitles.UPDATE_PROFILE:
                mTitle = FragmentTitles.UPDATE_PROFILE;
                break;
            case FragmentTitles.NEW_QUESTION:
                mTitle = FragmentTitles.NEW_QUESTION;
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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

    @Override
    public void onFragmentInteraction(String id) {

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
        }
    }

    public void UpdateFragment(View v){
        TempDataClass.fragmentStack = new Stack<Fragment>();
        TempDataClass.fragmentStack.push(new HomeFragment());

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment temp = new UpdateUserFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.container, temp)
                .commit();

        mNavigationDrawerFragment.CloseDrawer();
    }

    public void NavigatetoOfflineQuestions(View v){
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment temp = new SavedForOfflineReading();
        TempDataClass.fragmentStack.lastElement().onPause();
        TempDataClass.fragmentStack.push(currentFragment);
        fragmentManager.beginTransaction()
                .replace(R.id.container, temp)
                .commit();

        mNavigationDrawerFragment.CloseDrawer();
    }

    public void ChangePassword(View v){

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment temp = new ChangePasswordFragment();
        TempDataClass.fragmentStack.lastElement().onPause();
        TempDataClass.fragmentStack.push(currentFragment);
        fragmentManager.beginTransaction()
                .replace(R.id.container, temp)
                .commit();

        mNavigationDrawerFragment.CloseDrawer();
    }


}
