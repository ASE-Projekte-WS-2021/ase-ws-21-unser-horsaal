package com.example.unserhoersaal.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.databinding.CourseItemCardBinding;
import com.example.unserhoersaal.databinding.SimpleCourseItemBinding;
import com.example.unserhoersaal.model.CourseModel;
import java.util.List;

/** Coursesadapter. */
public class CoursesAdapter extends RecyclerView.Adapter<CoursesAdapter.ViewHolder> {

  private static final String TAG = "CoursesAdapter";

  private final List<CourseModel> localDataSet;

  public CoursesAdapter(List<CourseModel> dataSet) {
    this.localDataSet = dataSet;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
    CourseItemCardBinding binding = DataBindingUtil
            .inflate(LayoutInflater.from(viewGroup.getContext()),
                    R.layout.course_item_card,
                    viewGroup,
                    false);
    return new ViewHolder(binding);
  }

  @Override
  public void onBindViewHolder(ViewHolder viewHolder, int position) {
    CourseModel courseModel = localDataSet.get(position);
    viewHolder.connect(courseModel);
  }

  @Override
  public int getItemCount() {
    return this.localDataSet.size();
  }

  /** Viewholder. */
  public static class ViewHolder extends RecyclerView.ViewHolder {
    private final CourseItemCardBinding binding;

    /** Constructor. */
    public ViewHolder(CourseItemCardBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    public void connect(CourseModel model) {
      this.binding.setModel(model);
      this.binding.executePendingBindings();
    }

  }

}
