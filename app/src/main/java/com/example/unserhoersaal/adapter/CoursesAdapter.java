package com.example.unserhoersaal.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
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

  public CoursesAdapter(List<UserCourse> dataSet, OnNoteListener onNoteListener) {
    this.localDataSet = dataSet;
    this.onNoteListener = onNoteListener;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
    View view = LayoutInflater.from(viewGroup.getContext())
            .inflate(R.layout.simple_course_item, viewGroup, false);
    return new ViewHolder(view, this.onNoteListener);
  }

  @Override
  public void onBindViewHolder(ViewHolder viewHolder, int position) {
    if (viewHolder.getKeyTextView() != null && viewHolder.getCourseNameTextView() != null) {
      viewHolder.getCourseNameTextView().setText(this.localDataSet.get(position).getName());
      viewHolder.getKeyTextView().setText(this.localDataSet.get(position).getKey());
    }
  }

  @Override
  public int getItemCount() {
    return this.localDataSet.size();
  }

  /** Interface to get the clicked Course. */
  public interface OnNoteListener {
    void onNoteClick(int position);
  }

  /** Viewholder. */
  public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private TextView courseNameTextView;
    private TextView keyTextView;
    private ImageView shareImageView;
    private OnNoteListener onNoteListener;

    /** Constructor. */
    public ViewHolder(View view, OnNoteListener onNoteListener) {
      super(view);
      this.onNoteListener = onNoteListener;
      this.courseNameTextView = (TextView) view.findViewById(R.id.courseItemTitle);
      this.keyTextView = (TextView) view.findViewById(R.id.courseItemEnterNumber);

      view.setOnClickListener(this);
    }

    public TextView getCourseNameTextView() {
      return this.courseNameTextView;
    }

    public TextView getKeyTextView() {
      return this.keyTextView;
    }

    @Override
    public void onClick(View v) {
      this.onNoteListener.onNoteClick(getAdapterPosition());
    }
  }
}
