package nyp.fypj.irarphotodiary.fragment;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import nyp.fypj.irarphotodiary.R;
import nyp.fypj.irarphotodiary.application.BootstrapApplication;
import nyp.fypj.irarphotodiary.dto.ImageProfile;
import nyp.fypj.irarphotodiary.dto.Tag;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {
    private ExpandableListView expandableListView;
    private ExpandableListViewAdapter expandableListViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    public int GetDipsFromPixel(float pixels)
    {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        expandableListView = (ExpandableListView) getView().findViewById(R.id.expandableListView);

        expandableListView.setIndicatorBoundsRelative(BootstrapApplication.DEVICE_WIDTH - GetDipsFromPixel(35), BootstrapApplication.DEVICE_WIDTH - GetDipsFromPixel(5));

        ///////////////
        AsyncTask<Void,Integer,Void> task = new AsyncTask<Void,Integer,Void>() {
            private String responseString;
            private ArrayList<ImageProfile> imageProfiles;

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    // Upload json to databasesss
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost("http://fypj-124465r.rhcloud.com/albums/images");
                    httpPost.setHeader("Content-Type", "application/json");
                    httpPost.setEntity(new StringEntity("{\"tags\":[{\"confidence\":43.10115825222821,\"tag\":\"flower\"},{\"confidence\":35.64136447136573,\"tag\":\"pink\"}]}"));
                    HttpResponse response = httpClient.execute(httpPost); //TODO: not used?

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

                    Gson gson = new Gson();
                    imageProfiles = gson.fromJson(responseString, new TypeToken<ArrayList<ImageProfile>>(){}.getType());

                }catch (UnsupportedEncodingException ex){
                    ex.printStackTrace();
                }catch (IOException ex){
                    ex.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                expandableListViewAdapter = new ExpandableListViewAdapter(SearchFragment.this.getActivity().getApplicationContext(), imageProfiles);
                expandableListView.setAdapter(expandableListViewAdapter);
            }
        };

        task.execute();
        ///////////////

    }

    private class ExpandableListViewAdapter extends BaseExpandableListAdapter{
        private final ArrayList<ImageProfile> imageProfiles;
        private final LayoutInflater inflater;

        public ExpandableListViewAdapter(Context context, ArrayList<ImageProfile> imageProfiles){
            this.inflater = LayoutInflater.from(context);
            this.imageProfiles = imageProfiles;

        }

        @Override
        public Tag getChild(int parentPosition, int childPosition) {
            return imageProfiles.get(parentPosition).getTags().get(childPosition);
        }

        @Override
        public int getGroupCount() {
            return imageProfiles.size();
        }

        @Override
        public int getChildrenCount(int parentPosition) {
            return imageProfiles.get(parentPosition).getTags().size();
        }

        @Override
        public ImageProfile getGroup(int parentPosition) {
            return imageProfiles.get(parentPosition);
        }

        @Override
        public long getGroupId(int parentPosition) {
            return parentPosition;
        }

        @Override
        public long getChildId(int parentPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int parentPosition, boolean isExpanded, View theConvertView, ViewGroup parent) {
            View view = theConvertView;
            ParentViewHolder parentViewHolder;

            if(view == null){
                view = inflater.inflate(R.layout.adapter_fragment_search_list_parent, null);
                parentViewHolder = new ParentViewHolder();
                parentViewHolder.title = (TextView) view.findViewById(R.id.textView);
                parentViewHolder.description = (TextView) view.findViewById(R.id.textView8);
                parentViewHolder.imageView = (ImageView) view.findViewById(R.id.imageView);
                parentViewHolder.averageConfidenceLevel = (TextView) view.findViewById(R.id.textView9);

                view.setTag(parentViewHolder);
            }else{
                parentViewHolder = (ParentViewHolder) view.getTag();
            }

            final ImageProfile imageProfile = getGroup(parentPosition);

            double averageConfidenceLevel = 0;
            int count = 0;
            for(Tag tag : imageProfile.getTags()){
                averageConfidenceLevel += tag.getConfidence();
                count++;
            }
            if(count >0) {
                averageConfidenceLevel = averageConfidenceLevel/count;
            }

            parentViewHolder.title.setText(imageProfile.getTitle());
            parentViewHolder.description.setText(imageProfile.getDescription());
            parentViewHolder.averageConfidenceLevel.setText("Average Confidence Level: "+ Math.round(averageConfidenceLevel)+"%");

            ImageLoader.getInstance().displayImage("http://res.cloudinary.com/"+ BootstrapApplication.CLOUDINARY_CLOUD_NAME+"/image/upload/w_92,h_92,c_thumb/"+imageProfile.getFilename()+"."+imageProfile.getExtension(), parentViewHolder.imageView);

            return view;
        }


        @Override
        public View getChildView(int parentPosition, int childPosition, boolean isExpandable, View theConvertView, ViewGroup parent) {
            View resultView = theConvertView;
            ChildViewHolder childViewHolder;

            if(resultView == null){
                resultView = inflater.inflate(R.layout.adapter_fragment_search_list_child, null);
                childViewHolder = new ChildViewHolder();
                childViewHolder.tag = (TextView) resultView.findViewById(R.id.textView3);
                childViewHolder.confidence = (TextView) resultView.findViewById(R.id.textView7);
                resultView.setTag(childViewHolder);
            }else{
                childViewHolder = (ChildViewHolder) resultView.getTag();
            }

            final Tag tag = getChild(parentPosition, childPosition);

            childViewHolder.tag.setText(tag.getTag());
            childViewHolder.confidence.setText(Double.toString(Math.round(tag.getConfidence())) + "%");

            return resultView;
        }

        @Override
        public boolean isChildSelectable(int parentPosition, int childPosition) {
            return true;
        }

        private final class ParentViewHolder {
            TextView title;
            TextView description;
            ImageView imageView;
            TextView averageConfidenceLevel;
        }

        private final class ChildViewHolder{
            TextView tag;
            TextView confidence;
        }
    }
}
