package nyp.fypj.irarphotodiary.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;

import nyp.fypj.irarphotodiary.R;
import nyp.fypj.irarphotodiary.twitter.AlertDialogManager;
import nyp.fypj.irarphotodiary.twitter.ConnectionDetector;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/*
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
*/




public class LoginActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener,OnClickListener{

    public static final int RC_SIGN_IN = 0;
    private static final int PROFILE_PIC_SIZE = 400;
    public GoogleApiClient mGoogleApiClient;
    public boolean mIntentInProgress;
    public boolean mSignInClicked;
    public ConnectionResult mConnectionResult;
    private ImageButton btnSignIn;
    public Button btnSignOut, btnRevokeAccess;
    private ImageView imgProfilePic;
    private TextView profileName, profileEmail;
    private LinearLayout llProfileLayout;

    private Button btnProceed;
    private ImageView logo;
    private TextView title;
    private TextView header;
    private Button loginNormal;
    private LoginButton authButton;


    //TWITTER
    static String TWITTER_CONSUMER_KEY = "qDKgwJgHFdbDLsstDeGyA";
    static String TWITTER_CONSUMER_SECRET = "lhw8ZWZLFvm4K4e3zUiTz0OR86wD6ZUgZR45eju3JcY";

    // Preference Constants
    static String PREFERENCE_NAME = "twitter_oauth";
    static final String PREF_KEY_ACCESS_TOKEN = "access_token";
    static final String PREF_KEY_ACCESS_SECRET = "access_token_secret";
    public static final String PREF_KEY_USER_NAME = "user_name";
    public static final String  PREF_KEY_USER_PROFILE = "user_profile";

    static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";

    static final String TWITTER_CALLBACK_URL = "oauth://t4jsample";

    // Twitter oauth urls
    static final String URL_TWITTER_AUTH = "auth_url";
    static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";

    // Login button
    ImageButton btnLoginTwitter;
    //  private TwitterLoginButton loginTwitter;

    private static Twitter twitter;
    private static RequestToken requestToken;

    // Shared Preferences
    public static SharedPreferences mSharedPreferences;

    // Internet Connection detector
    private ConnectionDetector cd;
    AlertDialogManager alert = new AlertDialogManager();



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Font path
        String fontPath = "font/sigward.ttf";
        // text view label
        TextView txtGhost = (TextView) findViewById(R.id.textView8);
        // Loading Font Face
        Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
        // Applying font
        txtGhost.setTypeface(tf);
        txtGhost.setTextSize(30);

        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);

        // Remove divider under actionbar
        getActionBar().setBackgroundDrawable(null);

        loginNormal = (Button) findViewById(R.id.loginNormal);
        loginNormal.setBackgroundColor(Color.TRANSPARENT);
        loginNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, NavigationActivity.class);
                startActivity(i);

                finish();
            }
        });

        btnSignIn = (ImageButton) findViewById(R.id.loginGPlus);
        btnSignOut = (Button) findViewById(R.id.btn_sign_out);
        btnRevokeAccess = (Button) findViewById(R.id.btn_revoke_access);
        imgProfilePic = (ImageView) findViewById(R.id.imgProfilePic);
        profileName = (TextView) findViewById(R.id.profileName);
        profileEmail = (TextView) findViewById(R.id.profileEmail);
        llProfileLayout = (LinearLayout) findViewById(R.id.llProfileLayout);

        btnProceed = (Button) findViewById(R.id.btn_proceed);
        logo = (ImageView) findViewById(R.id.loginLogo);
        header = (TextView) findViewById(R.id.textView8);
        title = (TextView) findViewById(R.id.loginTitle);


        // Button click listeners
        btnSignIn.setOnClickListener(this);
        btnSignOut.setOnClickListener(this);
        btnRevokeAccess.setOnClickListener(this);
        btnProceed.setOnClickListener(this);



        // Initializing google plus api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();




        //TWITTER
        if (TWITTER_CONSUMER_KEY.trim().length() == 0 || TWITTER_CONSUMER_SECRET.trim().length() == 0) {
            // Internet Connection is not present
            alert.showAlertDialog(LoginActivity.this, "Twitter oAuth tokens",
                    "Please set your twitter oauth tokens first!", false);
            // stop executing code by return
            return;
        }

        // All UI elements
        btnLoginTwitter = (ImageButton) findViewById(R.id.btnLoginTwitter);
        btnLoginTwitter.setBackgroundResource(R.drawable.ic_twitter);
        mSharedPreferences = getApplicationContext().getSharedPreferences("MyPref", 0);

        /**
         * Twitter login button click event will call loginToTwitter() function
         * */
        btnLoginTwitter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Call login twitter function
                new TwitterLoginTask().execute();
            }
        });


        if (!isTwitterLoggedInAlready()) {
            Uri uri = getIntent().getData();
            if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
                // oAuth verifier
                String verifier = uri.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);

                new TwitterGetAccessTokenTask().execute(verifier);
            }
        } else {
            String userName = mSharedPreferences.getString(PREF_KEY_USER_NAME, "");
            //
            // enableTwitterStatus(userName);
        }


        //For Twitter Login
        /*
        loginTwitter = (TwitterLoginButton) findViewById(R.id.loginTwitter);
        loginTwitter.setText(null);
        loginTwitter.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        loginTwitter.setBackgroundResource(R.drawable.ic_twitter);
        loginTwitter.setCallback(new Callback<TwitterSession>() {
            //  TwitterSession session =
            //      Twitter.getSessionManager().getActiveSession();
            //   TwitterAuthToken authToken = session.getAuthToken();
            //  String token = authToken.token;
            //  String secret = authToken.secret;
            @Override
            public void success(Result<TwitterSession> result) {
                TwitterSession session =
                        Twitter.getSessionManager().getActiveSession();
                TwitterAuthToken authToken = session.getAuthToken();
                String token = authToken.token;
                String secret = authToken.secret;
                long userid = session.getUserId();
                // Do something with result, which provides a TwitterSession for making API calls

                // Do something with the result, which provides
                // the email address

                //loginTwitter.setReadPermissions(Arrays.asList("user_likes", "user_status", "email", "user_about_me", "user_location"));
                Toast.makeText(getApplicationContext(),"Welcome! "+session.getUserName() ,Toast.LENGTH_LONG).show();
                Intent i = new Intent(LoginActivity.this, NavigationActivity.class);
                startActivity(i);
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
                Toast.makeText(getApplicationContext(),"Login Failed",Toast.LENGTH_LONG).show();
            }
        });
        */

        //For facebook Login
        authButton = (LoginButton) findViewById(R.id.loginFacebook);
        authButton.setBackgroundResource(R.drawable.ic_facebook);
        authButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        authButton.setReadPermissions(Arrays.asList("user_likes", "user_status", "email", "user_about_me", "user_location"));

    }


    /**
     * Login with Twitter.
     */
    private class TwitterLoginTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {
            // Check if already logged in
            if (!isTwitterLoggedInAlready()) {
                ConfigurationBuilder builder = new ConfigurationBuilder();
                builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
                builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
                Configuration configuration = builder.build();

                TwitterFactory factory = new TwitterFactory(configuration);
                twitter = factory.getInstance();

                try {
                    requestToken = twitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);

                    LoginActivity.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL())));

                } catch (TwitterException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog and show
         * the data in UI
         */
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (isTwitterLoggedInAlready()) {
                // User already logged into twitter


                Toast.makeText(LoginActivity.this, "Already Logged into Twitter", Toast.LENGTH_LONG).show();

            }
        }

    }
    /**
     * Get Twitter access token.
     */
    public class TwitterGetAccessTokenTask extends AsyncTask<String, Void, User> {

        @Override
        protected User doInBackground(String... params) {

            String verifier = params[0];

            // Get the access token
            AccessToken accessToken = null;
            User user = null;
            try {
                accessToken = twitter.getOAuthAccessToken(requestToken, verifier);

                // Shared Preferences
                SharedPreferences.Editor e = mSharedPreferences.edit();

                // After getting access token, access token secret
                // store them in application preferences
                e.putString(PREF_KEY_ACCESS_TOKEN, accessToken.getToken());
                e.putString(PREF_KEY_ACCESS_SECRET, accessToken.getTokenSecret());
                // Store login status - true
                e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);


                Log.d(TAG, "Twitter OAuth Token: " + accessToken.getToken());

                // Getting user details from twitter
                // For now i am getting his name only
                long userID = accessToken.getUserId();
                user = twitter.showUser(userID);

                e.putString(PREF_KEY_USER_NAME, user.getName());
                e.putString(PREF_KEY_USER_PROFILE, user.getProfileBackgroundImageURL());
                e.commit(); // save changes

            } catch (TwitterException e) {
                Log.e(TAG, "Twitter Login Error: " + e.getMessage());
                e.printStackTrace();
            }

            return user;
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);

            if (user != null) {
                String userName = mSharedPreferences.getString(PREF_KEY_USER_NAME, "");
                Intent intent=new Intent(LoginActivity.this,NavigationActivity.class);
                intent.putExtra("TUsername",userName);
                startActivity(intent);
                Toast.makeText(LoginActivity.this, "Already Logged into Twitter"+userName, Toast.LENGTH_LONG).show();
                //  enableTwitterStatus(user.getName());
            }
        }
    }

    public void logoutFromTwitter() {
        // Clear the shared preferences
        SharedPreferences.Editor e = mSharedPreferences.edit();
        e.remove(PREF_KEY_ACCESS_TOKEN);
        e.remove(PREF_KEY_ACCESS_SECRET);
        e.remove(PREF_KEY_TWITTER_LOGIN);
        e.commit();

        // After this take the appropriate action
        // I am showing the hiding/showing buttons again
        // You might not needed this code



    }

    /**
     * Check user already logged in your application using twitter Login flag is
     * fetched from Shared Preferences
     * */
    private boolean isTwitterLoggedInAlready() {
        // return twitter login status from Shared Preferences
        return mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(PREF_KEY_USER_NAME, mSharedPreferences.getString(PREF_KEY_USER_NAME, null));

        // user email id
        user.put(PREF_KEY_USER_PROFILE, mSharedPreferences.getString(PREF_KEY_USER_PROFILE, null));

        // return user
        return user;
    }


    //GOOGLE PLUS
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginGPlus:
                signInWithGplus();
                break;

            case R.id.btn_sign_out:
                // Signout button clicked
                signOutFromGplus();
                break;

            case R.id.btn_revoke_access:
                // Revoke access button clicked
                revokeGplusAccess();
                break;

            case R.id.btn_proceed:
                proceedNav();
                break;
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // TODO Auto-generated method stub
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                    0).show();
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }

    }


    //facebook
    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            //Intent i = new Intent(LoginActivity.this, NavigationActivity.class);
            //    Intent i = new Intent(LoginActivity.this, GoogleMapActivity.class);
            //startActivity(i);
            finish();
        } else if (state.isClosed()) {
            //Toast.makeText(this, "Login failed! Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private UiLifecycleHelper uiHelper;

    @Override
    public void onResume() {
        super.onResume();

        /*Session session = Session.getActiveSession();
        if (session != null && (session.isOpened() || session.isClosed()) ) {
            onSessionStateChange(session, session.getState(), null);
        }*/
        uiHelper.onResume();
    }


    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);


    }
    private static final String TAG = "MainFragment";

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };


    @Override
    public void onConnected(Bundle connectionHint) {
        // TODO Auto-generated method stub
        mSignInClicked = false;
        //Toast.makeText(this, "Connected!", Toast.LENGTH_LONG).show();


        // Get user's information
        getProfileInformation();

        // Update the UI after signin
        updateUI(true);

    }

    @Override
    public void onConnectionSuspended(int cause) {
        // TODO Auto-generated method stub
        mGoogleApiClient.connect();
        updateUI(false);
    }

    public void updateUI(boolean isSignedIn) {
        if (isSignedIn) {

            getActionBar().setTitle("My Profile");

            logo.setVisibility(View.GONE);
            header.setVisibility(View.GONE);
            title.setVisibility(View.GONE);
            //loginTwitter.setVisibility(View.GONE);
            btnLoginTwitter.setVisibility(View.GONE);
            btnSignIn.setVisibility(View.GONE);
            loginNormal.setVisibility(View.GONE);
            authButton.setVisibility(View.GONE);

            btnSignOut.setVisibility(View.GONE);
            btnRevokeAccess.setVisibility(View.VISIBLE);
            btnProceed.setVisibility(View.VISIBLE);
            llProfileLayout.setVisibility(View.VISIBLE);
        }

        else {
            logo.setVisibility(View.VISIBLE);
            header.setVisibility(View.VISIBLE);
            title.setVisibility(View.VISIBLE);
            //loginTwitter.setVisibility(View.VISIBLE);
            btnLoginTwitter.setVisibility(View.VISIBLE);
            btnSignIn.setVisibility(View.VISIBLE);
            loginNormal.setVisibility(View.VISIBLE);
            authButton.setVisibility(View.VISIBLE);

            btnSignOut.setVisibility(View.GONE);
            btnRevokeAccess.setVisibility(View.GONE);
            btnProceed.setVisibility(View.GONE);
            llProfileLayout.setVisibility(View.GONE);
        }
    }

    private void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }

    /**
     * Method to resolve any signin errors
     * */
    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    public void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String personGooglePlusProfile = currentPerson.getUrl();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

                Log.e(TAG, "Name: " + personName + ", plusProfile: "
                        + personGooglePlusProfile + ", email: " + email
                        + ", Image: " + personPhotoUrl);

                profileName.setText(personName);
                profileEmail.setText(email);

                // by default the profile url gives 50x50 px image only
                // we can replace the value with whatever dimension we want by
                // replacing sz=X
                personPhotoUrl = personPhotoUrl.substring(0,
                        personPhotoUrl.length() - 2)
                        + PROFILE_PIC_SIZE;

                new LoadProfileImage(imgProfilePic).execute(personPhotoUrl);

            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public LoadProfileImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    public void signOutFromGplus() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
            updateUI(false);
        }
    }


    public void revokeGplusAccess() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status arg0) {
                            Log.e(TAG, "User access revoked!");
                            mGoogleApiClient.connect();
                            updateUI(false);
                        }

                    });
        }
    }

    private void proceedNav(){
        Intent i = new Intent(LoginActivity.this, NavigationActivity.class);
        //    Intent i = new Intent(LoginActivity.this, GoogleMapActivity.class);
        startActivity(i);

        finish();
    }


    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit Application")
                .setMessage("Are you sure you want to exit this application?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with exit

                        int id= android.os.Process.myPid();
                        android.os.Process.killProcess(id);
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


}
