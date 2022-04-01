package com.example.unserhoersaal.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unserhoersaal.R;
import com.example.unserhoersaal.databinding.MeetingCardBinding;
import com.example.unserhoersaal.databinding.PollItemBinding;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.model.PollModel;
import com.example.unserhoersaal.utils.PollUtil;
import com.example.unserhoersaal.viewmodel.PollViewModel;

import java.nio.file.attribute.FileTime;
import java.util.List;

public class PollAdapter extends RecyclerView.Adapter<PollAdapter.ViewHolder> {

  private static final String TAG = "PollAdapter";

  private final List<PollModel> localDataSet;
  private final PollViewModel pollViewModel;

  public PollAdapter(List<PollModel> localDataSet, PollViewModel pollViewModel) {
    this.localDataSet = localDataSet;
    this.pollViewModel = pollViewModel;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
    PollItemBinding binding = DataBindingUtil
            .inflate(LayoutInflater.from(viewGroup.getContext()),
                    R.layout.poll_item,
                    viewGroup,
                    false);
    return new ViewHolder(binding);
  }

  @Override
  public void onBindViewHolder(ViewHolder viewHolder, int position) {
    PollModel pollModel = localDataSet.get(position);
    viewHolder.connect(pollModel, pollViewModel);
  }

  @Override
  public int getItemCount() {
    return this.localDataSet.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    private final PollItemBinding binding;

    public ViewHolder(PollItemBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    public void connect(PollModel model, PollViewModel pollViewModel) {
      this.binding.setModel(model);
      this.binding.setVm(pollViewModel);
      this.binding.executePendingBindings();
    }
  }
}
