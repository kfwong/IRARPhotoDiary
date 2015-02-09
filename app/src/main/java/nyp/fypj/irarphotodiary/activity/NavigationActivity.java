package nyp.fypj.irarphotodiary.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.Session;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

import nyp.fypj.irarphotodiary.R;
import nyp.fypj.irarphotodiary.dto.ImageProfile;
import nyp.fypj.irarphotodiary.fragment.DashboardFragment;
import nyp.fypj.irarphotodiary.fragment.MyDiaryFragment;
import nyp.fypj.irarphotodiary.fragment.SearchFragment;
import nyp.fypj.irarphotodiary.fragment.TwitterProfileFragment;


public class NavigationActivity extends FragmentActivity {

    private ActionBarDrawerToggle navigationToggle;
    private ListView navigationList;
    private DrawerLayout navigationDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        // Remove divider under actionbar
        getActionBar().setBackgroundDrawable(null);

        List<String> data = new ArrayList<String>();
        data.add("Home");
        data.add("My Profile");
        data.add("Manage Diary");
        data.add("Search");
        data.add("AR Locations");
        data.add("Google Map");
        data.add("Logout");

        navigationDrawer = (DrawerLayout) findViewById(R.id.navigationDrawer);
        navigationToggle = new ActionBarDrawerToggle(
                this,
                navigationDrawer,
                R.drawable.ic_drawer,
                R.string.app_name,
                R.string.app_name);
        navigationDrawer.setDrawerListener(navigationToggle);

        this.getActionBar().setDisplayHomeAsUpEnabled(true);
        this.getActionBar().setHomeButtonEnabled(true);

        NavigationListAdapter navigationListAdapter = new NavigationListAdapter(this.getApplicationContext(), data);
        navigationList = (ListView) findViewById(R.id.navigationList);
        navigationList.setAdapter(navigationListAdapter);
        navigationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                displayFragment(position);
            }
        });

        if (savedInstanceState == null) {
            DashboardFragment dashboardFragment = new DashboardFragment();
            FragmentManager fragmentManager = this.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content, dashboardFragment).commit();
        }

    }

    private void displayFragment(int position) {

        Fragment fragment = null;
        Intent intent = null;

        switch (position) {
            case 0: //Home
                fragment = new DashboardFragment();
                break;
            case 1:
                fragment = new TwitterProfileFragment();

                break;
            case 2:
                fragment = new MyDiaryFragment();
                break;
            case 3:
                fragment = new SearchFragment();
                break;
            case 4:
                Ion.with(this)
                        .load("https://fypj-124465r.rhcloud.com/albums/images/")
                        .as(new TypeToken<ArrayList<ImageProfile>>() {
                        })
                        .setCallback(new FutureCallback<ArrayList<ImageProfile>>() {
                            @Override
                            public void onCompleted(Exception e, ArrayList<ImageProfile> imageProfiles){

                                Intent i = new Intent(NavigationActivity.this, ARActivity.class);
                                i.putParcelableArrayListExtra("imageProfiles", imageProfiles);
                                startActivity(i);
                            }
                        });
                break;
            case 5:
                Ion.with(this)
                        .load("https://fypj-124465r.rhcloud.com/albums/images/")
                        .as(new TypeToken<ArrayList<ImageProfile>>() {
                        })
                        .setCallback(new FutureCallback<ArrayList<ImageProfile>>() {
                            @Override
                            public void onCompleted(Exception e, ArrayList<ImageProfile> imageProfiles){

                                Intent i = new Intent(NavigationActivity.this, GoogleMapActivity.class);
                                i.putParcelableArrayListExtra("imageProfiles", imageProfiles);
                                startActivity(i);
                            }
                        });
                break;
            case 6:
                fragment = null;

                // find the active session which can only be facebook in my app
                Session session = Session.getActiveSession();
                // run the closeAndClearTokenInformation which does the following
                // DOCS : Closes the local in-memory Session object and clears any persistent
                // cache related to the Session.
                session.closeAndClearTokenInformation();
                // return the user to the login screen
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                // make sure the user can not access the page after he/she is logged out
                // clear the activity stack
                finish();
                break;
            default:
                fragment = null;
                break;
        }
        if (fragment != null) {
            FragmentManager fragmentManager = this.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content, fragment).commit();

            navigationList.setItemChecked(position, true);
            navigationList.setSelection(position);
            navigationDrawer.closeDrawer(navigationList);
        } else {
            Log.e(this.getClass().getName(), "Cannot load fragment or logout!");
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (navigationToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        navigationToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        navigationToggle.onConfigurationChanged(newConfig);
    }


    private class NavigationListAdapter extends BaseAdapter {
        private List<String> data;
        private LayoutInflater layoutInflater;

        public NavigationListAdapter(Context context, List<String> data) {
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
        public View getView(int i, View convertView, ViewGroup parent) {
            View view;
            ViewHolder viewHolder;


            if (convertView == null) {
                view = layoutInflater.inflate(R.layout.adapter_activity_navigation, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.navigationListItemTitle = (TextView) view.findViewById(R.id.navigationListItemTitle);
                viewHolder.navigationListItemIcon = (ImageView) view.findViewById(R.id.navigationListItemIcon1);





                view.setTag(viewHolder);

            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }

            // TODO:the first section of the list, wanted to make it look like a user profile thingy
            /*
            if(i == 0){
                view.setBackgroundColor(getResources().getColor(R.color.ICS_BLUE));
                viewHolder.navigationListItemIcon.setImageResource(R.drawable.ic_action_user);
                view.invalidate();
            }
            */

            String datum = data.get(i);

            if(datum=="Home"){

                ImageView image = (ImageView) view.findViewById(R.id.navigationListItemIcon1);
                image.setImageResource(R.drawable.ic_home);}

            else if (datum=="My Profile"){ImageView image = (ImageView) view.findViewById(R.id.navigationListItemIcon1);
                image.setImageResource(R.drawable.ic_user);
                }

            else if(datum=="Manage Diary"){ImageView image = (ImageView) view.findViewById(R.id.navigationListItemIcon1);
                image.setImageResource(R.drawable.ic_settings);}

            else if (datum=="Search"){ImageView image = (ImageView) view.findViewById(R.id.navigationListItemIcon1);
                image.setImageResource(R.drawable.ic_search);}

            else if (datum=="AR Locations"){ImageView image = (ImageView) view.findViewById(R.id.navigationListItemIcon1);
                image.setImageResource(R.drawable.ic_ar);}

            else if (datum=="Google Map"){ImageView image = (ImageView) view.findViewById(R.id.navigationListItemIcon1);
                image.setImageResource(R.drawable.ic_map);}

            else if (datum=="Logout"){ImageView image = (ImageView) view.findViewById(R.id.navigationListItemIcon1);
                image.setImageResource(R.drawable.ic_power);}

            viewHolder.navigationListItemTitle.setText(datum);
            // viewHolder.navigationListItemIcon.setText(datum);
            return view;
        }

        private class ViewHolder {
            public TextView navigationListItemTitle;
            public ImageView navigationListItemIcon;
        }
    }


    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        // find the active session which can only be facebook in my app
        Session session = Session.getActiveSession();
        // run the closeAndClearTokenInformation which does the following
        // DOCS : Closes the local in-memory Session object and clears any persistent
        // cache related to the Session.
        session.closeAndClearTokenInformation();
        // return the user to the login screen
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
       // Intent setIntent = new Intent(Intent.ACTION_MAIN);
       // setIntent.addCategory(Intent.CATEGORY_LAUNCHER);
       // setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
       //startActivity(setIntent);
    }
}
