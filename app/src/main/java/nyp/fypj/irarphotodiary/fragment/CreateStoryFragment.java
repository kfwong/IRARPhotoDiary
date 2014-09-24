package nyp.fypj.irarphotodiary.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nyp.fypj.irarphotodiary.R;
import nyp.fypj.irarphotodiary.activity.LoginActivity;
import nyp.fypj.irarphotodiary.activity.SplashActivity;

public class CreateStoryFragment extends ListFragment {

    private CreateStoryListAdapter createStoryListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        HashMap<String, String> datum1 = new HashMap<String, String>();
        datum1.put("title", "January");
        datum1.put("description", "January (Description)");
        HashMap<String, String> datum2 = new HashMap<String, String>();
        datum2.put("title", "February");
        datum2.put("description", "February (Description)");

        List<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
        data.add(datum1);
        data.add(datum2);

        createStoryListAdapter = new CreateStoryListAdapter(this.getListView().getContext(), data);
        setListAdapter(createStoryListAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        HashMap<String, String> datum = (HashMap<String, String>) getListAdapter().getItem(position);
        Intent intent = new Intent(getListView().getContext(), LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.create_story_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.createStoryAddNew:
                HashMap<String, String> datum = new HashMap<String, String>();
                datum.put("title","March");
                datum.put("description","March (Description)");
                createStoryListAdapter.addNew(datum);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private class CreateStoryListAdapter extends BaseAdapter {

        private List<HashMap<String, String>> data;
        private LayoutInflater layoutInflater;

        private CreateStoryListAdapter(Context context, List<HashMap<String, String>> data) {
            this.data = data;
            this.layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder viewHolder;

            if(convertView == null){
                view = layoutInflater.inflate(R.layout.adapter_fragment_create_story, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.createStoryItemImage = (ImageView) view.findViewById(R.id.createStoryItemImage);
                viewHolder.createStoryItemTitle = (TextView) view.findViewById(R.id.createStoryItemTitle);
                viewHolder.createStoryItemDescription = (TextView) view.findViewById(R.id.createStoryItemDescription);

                view.setTag(viewHolder);

            }else{
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }

            HashMap<String, String> datum = data.get(position);
            //viewHolder.createStoryItemImage
            viewHolder.createStoryItemTitle.setText(datum.get("title"));
            viewHolder.createStoryItemDescription.setText(datum.get("description"));
            return view;
        }

        public void addNew(HashMap<String, String> datum){
            data.add(datum);
            notifyDataSetChanged();
        }

        private class ViewHolder{
            public ImageView createStoryItemImage;
            public TextView createStoryItemTitle;
            public TextView createStoryItemDescription;
        }
    }

    // Dumb bug fix for calling nested fragments onActivityResult
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        // notifying nested fragments (support library bug fix)
//        final FragmentManager childFragmentManager = getChildFragmentManager();
//
//        if (childFragmentManager != null) {
//            final List<Fragment> nestedFragments = childFragmentManager.getFragments();
//
//            if (nestedFragments == null || nestedFragments.size() == 0) return;
//
//            for (Fragment childFragment : nestedFragments) {
//                //TODO: need to prevent double executing while attaching same fragment
//                if (childFragment != null && !childFragment.isDetached() && !childFragment.isRemoving()) {
//                    childFragment.onActivityResult(requestCode, resultCode, data);
//                }
//            }
//        }
//    }
}
