package nyp.fypj.irarphotodiary.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import nyp.fypj.irarphotodiary.R;


public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
    }
}
