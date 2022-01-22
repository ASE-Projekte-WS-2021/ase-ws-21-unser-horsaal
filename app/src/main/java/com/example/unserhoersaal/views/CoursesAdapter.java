package com.example.unserhoersaal.views;



import static android.content.Context.CLIPBOARD_SERVICE;

import android.content.ClipboardManager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.model.Message;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/** Coursesadapter. */
public class CoursesAdapter extends RecyclerView.Adapter<CoursesAdapter.ViewHolder> {

    private HashMap<String, String> localDataSet;
    private ArrayList<String> names = new ArrayList<String>();
    private ArrayList<String> keys = new ArrayList<String>();
    public static final SimpleDateFormat RECENT_FORMAT = new SimpleDateFormat("HH:mm");
    public static final SimpleDateFormat OLD_FORMAT = new SimpleDateFormat("dd. MMMM");

    /** Viewholder. */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView courseNameTextView;
        private final TextView keyTextView;
        private final ImageView shareImageView;

        /** Constructor. */
        public ViewHolder(View view) {
            super(view);

            courseNameTextView = (TextView) view.findViewById(R.id.courseItemTitle);
            keyTextView = (TextView) view.findViewById(R.id.courseItemEnterNumber);
            shareImageView = (ImageView) view.findViewById(R.id.courseItemShareImageView);

            shareImageView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                }
            });
        }

        public TextView getCourseNameTextView() {
            return courseNameTextView;
        }

        public TextView getKeyTextView() {
            return keyTextView;
        }
    }


    public CoursesAdapter(HashMap<String, String> dataSet) {
        localDataSet = dataSet;
        for (Map.Entry<String, String> entry : localDataSet.entrySet()) {
            keys.add(entry.getKey());
            names.add(entry.getValue());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.simple_course_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        if (viewHolder.getKeyTextView() != null && viewHolder.getCourseNameTextView() != null) {
            System.out.println(viewHolder.getCourseNameTextView());
            System.out.println(viewHolder.getKeyTextView());
            viewHolder.getCourseNameTextView().setText(names.get(position));
            viewHolder.getKeyTextView().setText(keys.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

}















