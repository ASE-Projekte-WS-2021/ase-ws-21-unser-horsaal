package com.example.unserhoersaal.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.databinding.PollItemBinding;
import com.example.unserhoersaal.model.PollModel;
import com.example.unserhoersaal.utils.StateData;
import com.example.unserhoersaal.viewmodel.PollViewModel;
import com.l4digital.fastscroll.FastScroller;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Adapter for pollItems.
 */
public class PollAdapter extends RecyclerView.Adapter<PollAdapter.ViewHolder> implements
        FastScroller.SectionIndexer {

  private final List<PollModel> localDataSet;
  private final PollViewModel pollViewModel;
  public Boolean isEnabled = true;
  private PollModel visibleItem;

  /** Constructor of ThreadAdapter.
   *
   * @param  localDataSet  includes the Data all Polls.
   * @param  pollViewModel is the ViewModel of the PollFragment.
   * @param  lifecycleOwner the LifeCycleOwner passed by the PollFragment.
   */
  public PollAdapter(List<PollModel> localDataSet, PollViewModel pollViewModel,
                     LifecycleOwner lifecycleOwner) {
    this.localDataSet = localDataSet;
    this.pollViewModel = pollViewModel;
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

  //code reference: https://stackoverflow.com/questions/24989218/get-visible-items-in-recyclerview.*/
  @Override
  public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
    super.onAttachedToRecyclerView(recyclerView);
    RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
    if (manager instanceof LinearLayoutManager && getItemCount() > 0) {
      LinearLayoutManager linearLayoutManager = (LinearLayoutManager) manager;
      recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
          super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
          super.onScrolled(recyclerView, dx, dy);
          int visiblePosition = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
          if (visiblePosition > -1) {
            visibleItem = localDataSet.get(visiblePosition);
          }
        }
      });
    }
  }

  @Override
  public CharSequence getSectionText(int position) {
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    return sdf.format(this.visibleItem.getCreationTime());

  }

  /**
   * ViewHolder for pollItmes.
   */
  public class ViewHolder extends RecyclerView.ViewHolder {
    private final PollItemBinding binding;

    public ViewHolder(PollItemBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    /**
     * Connect the dataBinding attributes.
     */
    public void connect(PollModel model, PollViewModel pollViewModel, Boolean isEnabled) {
      this.binding.setModel(model);
      this.binding.setVm(pollViewModel);
      this.binding.executePendingBindings();
      this.binding.setIsEnabled(isEnabled);
    }
  }
}
