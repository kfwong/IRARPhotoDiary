package sg.edu.nyp.irarphotodiary.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.etsy.android.grid.StaggeredGridView;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import sg.edu.nyp.irarphotodiary.R;
import sg.edu.nyp.irarphotodiary.activity.ViewStoryActivity;
import sg.edu.nyp.irarphotodiary.application.BootstrapApplication;
import sg.edu.nyp.irarphotodiary.dto.ImageProfile;

public class DashboardHomeFragment extends Fragment {
    private StaggeredGridView staggeredGridView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DashboardHomeFragmentAdapter dashboardHomeFragmentAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard_home, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        staggeredGridView = (StaggeredGridView) getView().findViewById(R.id.dashboardHomeFragmentStaggeredGridView);
        staggeredGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ImageProfile selectedImage = (ImageProfile) dashboardHomeFragmentAdapter.getItem(i);

                //TODO: to reuse the ViewStoryActivity, we need to put selectedImage into a list form, even if there's only single image...sort of design flaw haha
                ArrayList<ImageProfile> imageProfiles = new ArrayList<ImageProfile>();
                imageProfiles.add(selectedImage);

                Intent intent = new Intent(DashboardHomeFragment.this.getActivity().getApplicationContext(), ViewStoryActivity.class);
                intent.putParcelableArrayListExtra("imageProfiles", imageProfiles);
                startActivity(intent);
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.dashboardHomeFragmentSwipeRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.ICS_BLUE, R.color.grey, R.color.ICS_BLUE, R.color.grey);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }//onRefresh
        });//onRefreshListener

        //initial refresh
        refresh();
    }

    private void refresh() {
        ////
        swipeRefreshLayout.post(new Runnable() {
            @Override public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        Ion.with(this)
                .load("https://app-irarphotodiary.rhcloud.com/albums/images/")
                .as(new TypeToken<ArrayList<ImageProfile>>() {
                })
                .setCallback(new FutureCallback<ArrayList<ImageProfile>>() {
                    @Override
                    public void onCompleted(Exception e, ArrayList<ImageProfile> imageProfiles) {
                        dashboardHomeFragmentAdapter = new DashboardHomeFragmentAdapter(staggeredGridView.getContext(), imageProfiles);
                        staggeredGridView.setAdapter(dashboardHomeFragmentAdapter);

                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
        ////
    }

    private class DashboardHomeFragmentAdapter extends BaseAdapter {
        private LayoutInflater layoutInflater;
        private ArrayList<ImageProfile> data;

        public DashboardHomeFragmentAdapter(Context context, ArrayList<ImageProfile> data) {
            this.layoutInflater = LayoutInflater.from(context);
            this.data = data;
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
                view = layoutInflater.inflate(R.layout.adapter_fragment_dashboard_home_list_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.dashboardHomeItemImage = (ImageView) view.findViewById(R.id.dashboardHomeItemImage);
                viewHolder.dashboardHomeItemTitle = (TextView) view.findViewById(R.id.dashboardHomeItemTitle);
                viewHolder.dashboardHomeItemDescription = (TextView) view.findViewById(R.id.dashboardHomeItemDescription);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }

            ImageProfile datum = data.get(i);
            ImageLoader.getInstance().displayImage("http://res.cloudinary.com/" + BootstrapApplication.CLOUDINARY_CLOUD_NAME + "/image/upload/w_150,h_150/" + datum.getFilename() + "." + datum.getExtension(), viewHolder.dashboardHomeItemImage);
            viewHolder.dashboardHomeItemTitle.setText(datum.getTitle());
            viewHolder.dashboardHomeItemDescription.setText(datum.getDescription());
            return view;
        }

        private class ViewHolder {
            public ImageView dashboardHomeItemImage;
            public TextView dashboardHomeItemTitle;
            public TextView dashboardHomeItemDescription;
        }
    }


}