package nyp.fypj.irarphotodiary.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.app.Activity;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import nyp.fypj.irarphotodiary.R;


public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);

        // Remove divider under actionbar
        getActionBar().setBackgroundDrawable(null);

        Button loginNormal = (Button) findViewById(R.id.loginNormal);
        loginNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, NavigationActivity.class);
                //    Intent i = new Intent(LoginActivity.this, GoogleMapActivity.class);
                startActivity(i);

                finish();
            }
        });

        LoginButton authButton = (LoginButton) findViewById(R.id.loginFacebook);
        authButton.setBackgroundResource(R.drawable.ic_launcher);
        authButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        authButton.setReadPermissions(Arrays.asList("user_likes", "user_status", "email"));
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Intent i = new Intent(LoginActivity.this, NavigationActivity.class);
            //    Intent i = new Intent(LoginActivity.this, GoogleMapActivity.class);
            startActivity(i);

            finish();
        } else if (state.isClosed()) {
            Toast.makeText(this, "Login failed! Please try again.", Toast.LENGTH_SHORT).show();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
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
}
