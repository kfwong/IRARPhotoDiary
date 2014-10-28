package nyp.fypj.irarphotodiary.fragment;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.etsy.android.grid.StaggeredGridView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nyp.fypj.irarphotodiary.R;
import nyp.fypj.irarphotodiary.activity.ViewStoryActivity;
import nyp.fypj.irarphotodiary.application.BootstrapApplication;
import nyp.fypj.irarphotodiary.dto.ImageProfile;

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

    private void refresh(){
        AsyncTask<Void,Void,Void> task = new AsyncTask<Void,Void,Void>() {

            private ArrayList<ImageProfile> imageProfiles;

            @Override
            protected Void doInBackground(Void... voids) {
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response;
                String responseString = null;
                try {
                    response = httpclient.execute(new HttpGet("https://fypj-124465r.rhcloud.com/albums/images/"));
                    StatusLine statusLine = response.getStatusLine();
                    if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        response.getEntity().writeTo(out);
                        out.close();
                        responseString = out.toString();
                    } else{
                        //Closes the connection.
                        response.getEntity().getContent().close();
                        throw new IOException(statusLine.getReasonPhrase());
                    }
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Gson gson = new Gson();
                imageProfiles = gson.fromJson(responseString, new TypeToken<ArrayList<ImageProfile>>(){}.getType());

                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                dashboardHomeFragmentAdapter = new DashboardHomeFragmentAdapter(staggeredGridView.getContext(), imageProfiles);
                staggeredGridView.setAdapter(dashboardHomeFragmentAdapter);

                swipeRefreshLayout.setRefreshing(false);
            }
        }; // AsyncTask

        task.execute();
    }

    private class DashboardHomeFragmentAdapter extends BaseAdapter{
        private LayoutInflater layoutInflater;
        private ArrayList<ImageProfile> data;

        public DashboardHomeFragmentAdapter(Context context, ArrayList<ImageProfile> data){
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

            if(convertView == null){
                view = layoutInflater.inflate(R.layout.adapter_fragment_dashboard_home_list_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.dashboardHomeItemImage = (ImageView) view.findViewById(R.id.dashboardHomeItemImage);
                viewHolder.dashboardHomeItemTitle = (TextView) view.findViewById(R.id.dashboardHomeItemTitle);
                viewHolder.dashboardHomeItemDescription = (TextView) view.findViewById(R.id.dashboardHomeItemDescription);
                view.setTag(viewHolder);
            }else{
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }

            ImageProfile datum = data.get(i);
            ImageLoader.getInstance().displayImage("http://res.cloudinary.com/"+ BootstrapApplication.CLOUDINARY_CLOUD_NAME+"/image/upload/w_0.1/"+datum.getFilename()+"."+datum.getExtension(), viewHolder.dashboardHomeItemImage);
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