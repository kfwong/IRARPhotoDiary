package nyp.fypj.irarphotodiary.fragment;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;

import nyp.fypj.irarphotodiary.R;
import nyp.fypj.irarphotodiary.dto.Album;
import nyp.fypj.irarphotodiary.dto.ImageProfile;
import nyp.fypj.irarphotodiary.dto.Tag;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {
    private ExpandableListView expandableListView;
    private ExpandableListViewAdapter expandableListViewAdapter;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        expandableListView = (ExpandableListView) getView().findViewById(R.id.expandableListView);
        //expandableListViewAdapter = new ExpandableListViewAdapter(this, )
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
            ViewHolder viewHolder;

            if(view == null){
                view = inflater.inflate(R.layout.adapter_fragment_search_list_parent, null);
                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) view.findViewById(R.id.textView);
                view.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) view.getTag();
            }

            final ImageProfile imageProfile = getGroup(parentPosition);

            viewHolder.textView.setText(imageProfile.getTitle());

            return view;
        }

        @Override
        public View getChildView(int parentPosition, int childPosition, boolean isExpandable, View theConvertView, ViewGroup parent) {
            View resultView = theConvertView;
            ViewHolder viewHolder;

            if(resultView == null){
                resultView = inflater.inflate(R.layout.adapter_fragment_search_list_child, null);
                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) resultView.findViewById(R.id.textView3);
                resultView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) resultView.getTag();
            }

            final Tag tag = getChild(parentPosition, childPosition);

            viewHolder.textView.setText(tag.getTag());

            return resultView;
        }

        @Override
        public boolean isChildSelectable(int parentPosition, int childPosition) {
            return true;
        }

        private final class ViewHolder{
            TextView textView;
        }
    }
}
