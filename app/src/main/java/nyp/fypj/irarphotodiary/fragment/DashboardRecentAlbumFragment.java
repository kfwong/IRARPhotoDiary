package nyp.fypj.irarphotodiary.fragment;



import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import nyp.fypj.irarphotodiary.R;
import nyp.fypj.irarphotodiary.activity.CreateStoryListActivity;
import nyp.fypj.irarphotodiary.activity.ViewStoryActivity;
import nyp.fypj.irarphotodiary.application.BootstrapApplication;
import nyp.fypj.irarphotodiary.dto.Album;
import nyp.fypj.irarphotodiary.dto.ImageProfile;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 *
 */
public class DashboardRecentAlbumFragment extends Fragment {


    public DashboardRecentAlbumFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard_recent_album, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ProgressBar progressBar = (ProgressBar) getView().findViewById(R.id.dashboardRecentAlbumFragmentProgressBar);

        final StaggeredGridView staggeredGridView = (StaggeredGridView) getView().findViewById(R.id.dashboardRecentAlbumFragmentStaggeredGridView);
        staggeredGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Album selectedAlbum = (Album)adapterView.getItemAtPosition(i);

                Intent intent = new Intent(DashboardRecentAlbumFragment.this.getActivity().getApplicationContext(), ViewStoryActivity.class);
                intent.putParcelableArrayListExtra("imageProfiles", selectedAlbum.getImageProfiles());
                startActivity(intent);
            }
        });

        AsyncTask<Void,Void,Void> task = new AsyncTask<Void,Void,Void>() {

            private ArrayList<Album> albums;

            @Override
            protected Void doInBackground(Void... voids) {
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response;
                String responseString = null;
                try {
                    response = httpclient.execute(new HttpGet("https://fypj-124465r.rhcloud.com/albums/"));
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
                albums = gson.fromJson(responseString, new TypeToken<ArrayList<Album>>(){}.getType());

                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressBar.setVisibility(ProgressBar.VISIBLE);
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                dashboardRecentAlbumFragmentAdapter dashboardRecentAlbumFragmentAdapter = new dashboardRecentAlbumFragmentAdapter(staggeredGridView.getContext(), albums);
                staggeredGridView.setAdapter(dashboardRecentAlbumFragmentAdapter);

                progressBar.setVisibility(ProgressBar.INVISIBLE);
            }
        }; // AsyncTask

        task.execute();
    }

    private class dashboardRecentAlbumFragmentAdapter extends BaseAdapter {
        private LayoutInflater layoutInflater;
        private ArrayList<Album> albums;

        public dashboardRecentAlbumFragmentAdapter(Context context, ArrayList<Album> albums){
            this.layoutInflater = LayoutInflater.from(context);
            this.albums = albums;
        }

        @Override
        public int getCount() {
            return albums.size();
        }

        @Override
        public Object getItem(int i) {
            return albums.get(i);
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
                view = layoutInflater.inflate(R.layout.adapter_fragment_dashboard_recent_album_list_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.dashboardRecentAlbumItemImage = (ImageView) view.findViewById(R.id.dashboardRecentAlbumItemImage);
                viewHolder.dashboardRecentAlbumItemTitle = (TextView) view.findViewById(R.id.dashboardRecentAlbumItemTitle);
                viewHolder.dashboardRecentAlbumItemDescription = (TextView) view.findViewById(R.id.dashboardRecentAlbumItemDescription);
                view.setTag(viewHolder);
            }else{
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }

            Album album = albums.get(i);
            ImageProfile coverImage = albums.get(i).getImageProfiles().get(0);

            viewHolder.dashboardRecentAlbumItemTitle.setText(album.getTitle());
            viewHolder.dashboardRecentAlbumItemDescription.setText(album.getDescription());
            ImageLoader.getInstance().displayImage("http://res.cloudinary.com/"+ BootstrapApplication.CLOUDINARY_CLOUD_NAME+"/image/upload/w_0.1/"+coverImage.getFilename()+"."+coverImage.getExtension(), viewHolder.dashboardRecentAlbumItemImage);
            return view;
        }
        private class ViewHolder {
            public ImageView dashboardRecentAlbumItemImage;
            public TextView dashboardRecentAlbumItemTitle;
            public TextView dashboardRecentAlbumItemDescription;
        }
    }
}
