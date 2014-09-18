package nyp.fypj.irarphotodiary.activity;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;

import nyp.fypj.irarphotodiary.R;
import nyp.fypj.irarphotodiary.fragment.DashboardFragment;


public class NavigationActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        List<String> data = new ArrayList<String>();
        data.add("Home");
        data.add("School");
        data.add("Work");

        DrawerLayout navigationDrawer = (DrawerLayout) findViewById(R.id.navigationDrawer);
        this.getActionBar().setDisplayHomeAsUpEnabled(true);
        this.getActionBar().setHomeButtonEnabled(true);

        ListView navigationList = (ListView) findViewById(R.id.navigationList);
        NavigationListAdapter navigationListAdapter = new NavigationListAdapter(this.getApplicationContext(), data);
        navigationList.setAdapter(navigationListAdapter);

        DashboardFragment dashboardFragment = new DashboardFragment();
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, dashboardFragment).commit();

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class NavigationListAdapter extends BaseAdapter{
        private List<String> data;
        private LayoutInflater layoutInflater;

        public NavigationListAdapter(Context context, List<String> data){
            this.data = data;
            this.layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int i) {
            return data.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent){
            View view;
            ViewHolder viewHolder;

            if(convertView == null){
                view = layoutInflater.inflate(R.layout.adapter_activity_navigation, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.navigationListItemTitle = (TextView) view.findViewById(R.id.navigationListItemTitle);
                view.setTag(viewHolder);
            }else{
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }

            // the first section of the list, wanted to make it look like a user profile thingy
            if(i == 0){
                view.setBackgroundColor(getResources().getColor(R.color.ICS_BLUE));
                view.invalidate();
            }

            String datum = data.get(i);
            viewHolder.navigationListItemTitle.setText(datum);
            return view;
        }

        private class ViewHolder{
            public TextView navigationListItemTitle;
        }
    }
}
