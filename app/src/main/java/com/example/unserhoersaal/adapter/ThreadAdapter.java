package com.example.unserhoersaal.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.databinding.SimpleCourseItemBinding;
import com.example.unserhoersaal.databinding.ThreadItemBinding;
import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.model.ThreadModel;
import java.util.ArrayList;
import java.util.List;

/** Adapter for the RecyclerView inCourseMeetingRepository. */
public class ThreadAdapter extends RecyclerView.Adapter<ThreadAdapter.ViewHolder> {

  private static final String TAG = "ThreadAdapter";

  private List<ThreadModel> localDataSet;

  public ThreadAdapter(List<ThreadModel> dataSet) {
    this.localDataSet = dataSet;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
    ThreadItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.simple_course_item, viewGroup, false);
    return new ViewHolder(binding);
  }

  @Override
  public void onBindViewHolder(ViewHolder viewHolder, int position) {
    ThreadModel threadModel = localDataSet.get(position);
    viewHolder.connect(threadModel);
  }

  @Override
  public int getItemCount() {
    return this.localDataSet.size();
  }

  /** Viewholder for an thread item. */
  public class ViewHolder extends RecyclerView.ViewHolder {
    private final ThreadItemBinding binding;

    /** Constructor. */
    public ViewHolder(ThreadItemBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    public void connect(ThreadModel model) {
      this.binding.setModel(model);
      this.binding.executePendingBindings();
    }

  }

}
