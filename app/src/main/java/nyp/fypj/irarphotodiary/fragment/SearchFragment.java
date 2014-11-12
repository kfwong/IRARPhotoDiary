package nyp.fypj.irarphotodiary.fragment;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.koushikdutta.ion.Ion;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

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
    private ImageView selectedImageView;
    private String takenPhotoTempUri;
    private ProgressBar searchProgressBar;
    private TextView searchStatus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    public int GetDipsFromPixel(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            takenPhotoTempUri = savedInstanceState.getString("takenPhotoTempUri");
        }

        selectedImageView = (ImageView) getView().findViewById(R.id.searchSelectedImage);

        expandableListView = (ExpandableListView) getView().findViewById(R.id.expandableListView);

        expandableListView.setIndicatorBoundsRelative(BootstrapApplication.DEVICE_WIDTH - GetDipsFromPixel(35), BootstrapApplication.DEVICE_WIDTH - GetDipsFromPixel(5));

        searchProgressBar = (ProgressBar) getView().findViewById(R.id.searchProgressBar);

        searchStatus = (TextView) getView().findViewById(R.id.searchStatus);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.searchChooseFromGallery:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);//TODO: LOOK AT THAT UGLY REQUEST CODE!!!
                break;
            case R.id.searchTakePhoto:
                try {
                    Intent takePhotoIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    File storage = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    File image = null;

                    image = File.createTempFile("IRAR_" + System.currentTimeMillis(), ".jpg", storage);

                    takenPhotoTempUri = Uri.fromFile(image).toString();
                    Log.e("onOptionsItemSelected", "image: " + takenPhotoTempUri);

                    if (image != null) {
                        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
                        startActivityForResult(takePhotoIntent, 2);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1: //TODO
                if (resultCode == getActivity().RESULT_OK) {
                    Uri cachedUri = data.getData();

                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getActivity().getContentResolver().query(cachedUri,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    final String actualUri = cursor.getString(columnIndex);
                    cursor.close();

                    ImageLoader.getInstance().loadImage("file://" + actualUri, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            super.onLoadingComplete(imageUri, view, loadedImage);


                            AsyncTask<Void, Integer, Void> task = new AsyncTask<Void, Integer, Void>() {
                                private ArrayList<ImageProfile> imageProfiles;

                                @Override
                                protected Void doInBackground(Void... voids) {

                                    try {

                                        publishProgress(1);
                                        String result = Ion.with(SearchFragment.this)
                                                .load("http://api.imagga.com/draft/tags")
                                                .setTimeout(60000)
                                                .setMultipartParameter("api_key", "acc_31de762e407a6a3")
                                                .setMultipartFile("file", new File(actualUri))
                                                .asString()
                                                .get();

                                        publishProgress(2);
                                        imageProfiles = Ion.with(SearchFragment.this)
                                                .load("http://fypj-124465r.rhcloud.com/albums/images")
                                                .setTimeout(60000)
                                                .addHeader("Content-Type", "application/json")
                                                .setStringBody(result)
                                                .as(new TypeToken<ArrayList<ImageProfile>>() {
                                                })
                                                .get();

                                        publishProgress(3);
                                    } catch (InterruptedException ex) {
                                        ex.printStackTrace();
                                    } catch (ExecutionException ex) {
                                        publishProgress(5);
                                        ex.printStackTrace();
                                    }


                                    return null;
                                }//doInBackground

                                @Override
                                protected void onProgressUpdate(Integer... values) {
                                    super.onProgressUpdate(values);

                                    switch (values[0]) {
                                        case 1:
                                            searchProgressBar.setVisibility(ProgressBar.VISIBLE);
                                            searchStatus.setText("Analyzing in progress...\nThis may take a while depending on the size of the image.");
                                            break;
                                        case 2:
                                            searchStatus.setText("Image Analyzed. Querying image database...");
                                            break;
                                        case 3:
                                            searchStatus.setText("Query completed. Formatting results for display...");
                                            break;
                                        case 4:
                                            searchProgressBar.setVisibility(ProgressBar.GONE);
                                            searchStatus.setText("Search completed on \n" + new Date());
                                            break;
                                        case 5:
                                            searchProgressBar.setVisibility(ProgressBar.GONE);
                                            searchStatus.setText("Query timeout: exceeded 1 minute.");
                                    }
                                }

                                @Override
                                protected void onPostExecute(Void aVoid) {
                                    super.onPostExecute(aVoid);

                                    expandableListViewAdapter = new ExpandableListViewAdapter(SearchFragment.this.getActivity().getApplicationContext(), imageProfiles);
                                    expandableListView.setAdapter(expandableListViewAdapter);

                                    publishProgress(4);
                                }
                            };

                            task.execute();

                            loadedImage = ThumbnailUtils.extractThumbnail(loadedImage, 92, 92);

                            selectedImageView.setImageBitmap(loadedImage);
                        }
                    });

                }
                break;
            case 2: //TODO
                if (resultCode == getActivity().RESULT_OK) {
//                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//                    File f = new File(imageProfile.getUri());
//                    Uri contentUri = Uri.fromFile(f);
//                    mediaScanIntent.setData(contentUri);
//                    this.sendBroadcast(mediaScanIntent);
                    //Log.e("onActivityResult", "image: " + takenPhotoTempUri);

                    ImageLoader.getInstance().loadImage(takenPhotoTempUri, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            super.onLoadingComplete(imageUri, view, loadedImage);

                            loadedImage = ThumbnailUtils.extractThumbnail(loadedImage, 92, 92);

                            selectedImageView.setImageBitmap(loadedImage);
                        }
                    });
                }
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // DEBUG image problem: Log.e("onSaveInstanceState", "image: "+imageProfile.getUri());

        outState.putString("takenPhotoTempUri", takenPhotoTempUri);

        super.onSaveInstanceState(outState);
    }

    private class ExpandableListViewAdapter extends BaseExpandableListAdapter {
        private final ArrayList<ImageProfile> imageProfiles;
        private final LayoutInflater inflater;

        public ExpandableListViewAdapter(Context context, ArrayList<ImageProfile> imageProfiles) {
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

            if (view == null) {
                view = inflater.inflate(R.layout.adapter_fragment_search_list_parent, null);
                parentViewHolder = new ParentViewHolder();
                parentViewHolder.title = (TextView) view.findViewById(R.id.textView);
                parentViewHolder.numOfMatchedTags = (TextView) view.findViewById(R.id.numOfMatchedTags);
                parentViewHolder.imageView = (ImageView) view.findViewById(R.id.imageView);
                parentViewHolder.averageConfidenceLevel = (TextView) view.findViewById(R.id.textView9);

                view.setTag(parentViewHolder);
            } else {
                parentViewHolder = (ParentViewHolder) view.getTag();
            }

            final ImageProfile imageProfile = getGroup(parentPosition);

            double averageConfidenceLevel = 0;
            int count = 0;
            for (Tag tag : imageProfile.getTags()) {
                averageConfidenceLevel += tag.getConfidence();
                count++;
            }
            if (count > 0) {
                averageConfidenceLevel = averageConfidenceLevel / count;
            }

            parentViewHolder.title.setText(imageProfile.getTitle());
            parentViewHolder.numOfMatchedTags.setText("Matching Tags Count: " + count);
            parentViewHolder.averageConfidenceLevel.setText("Average Tags Confidence Level: " + Math.round(averageConfidenceLevel) + "%");

            ImageLoader.getInstance().displayImage("http://res.cloudinary.com/" + BootstrapApplication.CLOUDINARY_CLOUD_NAME + "/image/upload/w_92,h_92,c_thumb/" + imageProfile.getFilename() + "." + imageProfile.getExtension(), parentViewHolder.imageView);

            return view;
        }


        @Override
        public View getChildView(int parentPosition, int childPosition, boolean isExpandable, View theConvertView, ViewGroup parent) {
            View resultView = theConvertView;
            ChildViewHolder childViewHolder;

            if (resultView == null) {
                resultView = inflater.inflate(R.layout.adapter_fragment_search_list_child, null);
                childViewHolder = new ChildViewHolder();
                childViewHolder.tag = (TextView) resultView.findViewById(R.id.textView3);
                childViewHolder.confidence = (TextView) resultView.findViewById(R.id.textView7);
                resultView.setTag(childViewHolder);
            } else {
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
            TextView numOfMatchedTags;
            ImageView imageView;
            TextView averageConfidenceLevel;
        }

        private final class ChildViewHolder {
            TextView tag;
            TextView confidence;
        }
    }
}
