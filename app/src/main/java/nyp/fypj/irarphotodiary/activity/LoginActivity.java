package nyp.fypj.irarphotodiary.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
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
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.io.InputStream;
import java.util.Arrays;

import nyp.fypj.irarphotodiary.R;


public class LoginActivity extends Activity implements OnClickListener, ConnectionCallbacks, OnConnectionFailedListener{

    private static final int RC_SIGN_IN = 0;
    private static final int PROFILE_PIC_SIZE = 400;
    private GoogleApiClient mGoogleApiClient;
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;
    private ImageButton btnSignIn;
    private Button btnSignOut, btnRevokeAccess;
    private ImageView imgProfilePic;
    private TextView profileName, profileEmail;
    private LinearLayout llProfileLayout;

    private Button btnProceed;
    private ImageView logo;
    private TextView title;
    private TextView header;
    private Button loginNormal;
    private LoginButton authButton;



    private TwitterLoginButton loginTwitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        loginNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, NavigationActivity.class);
                //    Intent i = new Intent(LoginActivity.this, GoogleMapActivity.class);
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


        //For Twitter Login
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
        //For facebook Login
        authButton = (LoginButton) findViewById(R.id.loginFacebook);
        authButton.setBackgroundResource(R.drawable.ic_facebook);
        authButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        authButton.setReadPermissions(Arrays.asList("user_likes", "user_status", "email", "user_about_me", "user_location"));

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
                // Signin button clicked
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



    public void onActivityResult(int resultCode, Intent data, int requestCode, int responseCode, Intent intent) {

        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
        loginTwitter.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result to the login button.
        // loginButton.onActivityResult(requestCode, resultCode,data);
    }
    /*  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result to the fragment, which will
        // then pass the result to the login button.
        Fragment fragment = getFragmentManager().findFragmentById(R.id.ProfileFragment);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }*/

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

    private void updateUI(boolean isSignedIn) {
        if (isSignedIn) {
            logo.setVisibility(View.GONE);
            header.setVisibility(View.GONE);
            title.setVisibility(View.GONE);
            loginTwitter.setVisibility(View.GONE);
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
            loginTwitter.setVisibility(View.VISIBLE);
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

    private void getProfileInformation() {
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

    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
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

    private void signOutFromGplus() {
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
