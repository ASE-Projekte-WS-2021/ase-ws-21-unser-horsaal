package com.example.unserhoersaal.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.model.UserCourse;
import java.util.ArrayList;
import java.util.List;


/** Coursesadapter. */
public class CoursesAdapter extends RecyclerView.Adapter<CoursesAdapter.ViewHolder> {

  private static final String TAG = "CoursesAdapter";

  private List<UserCourse> localDataSet = new ArrayList<>();
  private OnNoteListener onNoteListener;


  /** Viewholder. */
  public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final TextView courseNameTextView;
    private final TextView keyTextView;
    private final ImageView shareImageView;
    OnNoteListener onNoteListener;

    /** Constructor. */
    public ViewHolder(View view, OnNoteListener onNoteListener) {
      super(view);
      this.onNoteListener = onNoteListener;
      courseNameTextView = (TextView) view.findViewById(R.id.courseItemTitle);
      keyTextView = (TextView) view.findViewById(R.id.courseItemEnterNumber);
      shareImageView = (ImageView) view.findViewById(R.id.courseItemShareImageView);

      view.setOnClickListener(this);
    }

    public TextView getCourseNameTextView() {
      return courseNameTextView;
    }

    public TextView getKeyTextView() {
      return keyTextView;
    }

    @Override
    public void onClick(View v) {
      onNoteListener.onNoteClick(getAdapterPosition());
    }
  }


  public CoursesAdapter(List<UserCourse> dataSet, OnNoteListener onNoteListener) {
    localDataSet = dataSet;
    this.onNoteListener = onNoteListener;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
    View view = LayoutInflater.from(viewGroup.getContext())
            .inflate(R.layout.simple_course_item, viewGroup, false);

    return new ViewHolder(view, onNoteListener);
  }

  @Override
  public void onBindViewHolder(ViewHolder viewHolder, final int position) {

    if (viewHolder.getKeyTextView() != null && viewHolder.getCourseNameTextView() != null) {
      System.out.println(viewHolder.getCourseNameTextView());
      System.out.println(viewHolder.getKeyTextView());
      viewHolder.getCourseNameTextView().setText(localDataSet.get(position).name);
      viewHolder.getKeyTextView().setText(localDataSet.get(position).key);
    }
  }

  @Override
  public int getItemCount() {
    return localDataSet.size();
  }

  /** Interface to get the clicked Course. */
  public interface OnNoteListener {
    void onNoteClick(int position);
  }

}
