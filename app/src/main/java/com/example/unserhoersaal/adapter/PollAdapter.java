package com.example.unserhoersaal.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.databinding.PollItemBinding;
import com.example.unserhoersaal.model.PollModel;
import com.example.unserhoersaal.utils.StateData;
import com.example.unserhoersaal.utils.StateLiveData;
import com.example.unserhoersaal.viewmodel.PollViewModel;
import java.util.List;

/** Adapter for pollItems. */
public class PollAdapter extends RecyclerView.Adapter<PollAdapter.ViewHolder> {

  private static final String TAG = "PollAdapter";

  private final List<PollModel> localDataSet;
  private final PollViewModel pollViewModel;
  private StateLiveData<PollModel> pollModelStateLiveData;
  public Boolean isEnabled = true;

  public PollAdapter(List<PollModel> localDataSet, PollViewModel pollViewModel,
                     LifecycleOwner lifecycleOwner) {
    this.localDataSet = localDataSet;
    this.pollViewModel = pollViewModel;
    this.pollModelStateLiveData = pollViewModel.getPollModel();
    this.pollViewModel.getPollModel().observe(
            lifecycleOwner, this:: pollsLiveDataCallback);
  }

  @SuppressLint("NotifyDataSetChanged")
  private void pollsLiveDataCallback(StateData<PollModel> pollModelStateData) {
    if (pollModelStateData == null) {
      return;
    }
    if (pollModelStateData.getStatus() == StateData.DataStatus.LOADING) {
      this.isEnabled = false;
    }
    if (pollModelStateData.getStatus() == StateData.DataStatus.CREATED) {
      this.isEnabled = true;
    }
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
    viewHolder.connect(pollModel, pollViewModel, isEnabled);
  }

  @Override
  public int getItemCount() {
    return this.localDataSet.size();
  }

  /** ViewHolder for pollItmes. */
  public class ViewHolder extends RecyclerView.ViewHolder {
    private final PollItemBinding binding;

    public ViewHolder(PollItemBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    /** Connect the dataBinding attributes. */
    public void connect(PollModel model, PollViewModel pollViewModel, Boolean isEnabled) {
      this.binding.setModel(model);
      this.binding.setVm(pollViewModel);
      this.binding.executePendingBindings();
      this.binding.setIsEnabled(isEnabled);
    }
  }
}
