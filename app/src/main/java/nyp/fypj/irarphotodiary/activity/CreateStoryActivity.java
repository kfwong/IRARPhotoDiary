package nyp.fypj.irarphotodiary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.flaviofaria.kenburnsview.KenBurnsView;

import java.util.HashMap;

import nyp.fypj.irarphotodiary.R;

public class CreateStoryActivity extends FragmentActivity {
    private int position; // keeping track current entry in the parent list view position
    private TextView createStoryTitle;
    private TextView createStoryDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_story);

        getActionBar().setBackgroundDrawable(null);

        KenBurnsView createStoryImageView = (KenBurnsView) findViewById(R.id.createStoryImageView);
        createStoryImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.e("TADAH", "TADAHHHHH");
                return true;
            }
        });


        ///
//        Intent intent = getIntent();
//
//        position = intent.getIntExtra("position", -1);
//        HashMap<String, String> datum = (HashMap<String, String>)intent.getSerializableExtra("datum");
//
//        createStoryTitle = (TextView)findViewById(R.id.createStoryContentTitle);
//        createStoryTitle.setText(datum.get("title"));
//
//        createStoryDescription = (TextView) findViewById(R.id.createStoryDescription);
//        createStoryDescription.setText(datum.get("description"));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_story, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
            case R.id.createStorySave:
                HashMap<String, String> datum = new HashMap<String, String>();
                datum.put("title", createStoryTitle.getText().toString());
                datum.put("description", createStoryDescription.getText().toString());

                Intent intent = new Intent();
                intent.putExtra("position", position);
                intent.putExtra("datum", datum);

                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.createStoryCancel:
                setResult(RESULT_CANCELED);
                finish();
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
