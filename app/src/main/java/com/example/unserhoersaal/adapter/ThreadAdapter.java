package com.example.unserhoersaal.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.databinding.ThreadCardBinding;
import com.example.unserhoersaal.databinding.ThreadItemBinding;
import com.example.unserhoersaal.model.ThreadModel;
import com.example.unserhoersaal.viewmodel.CurrentCourseViewModel;
import com.l4digital.fastscroll.FastScroller;

import java.text.SimpleDateFormat;
import java.util.List;

/** Adapter for the RecyclerView inCourseMeetingRepository. */
public class ThreadAdapter extends RecyclerView.Adapter<ThreadAdapter.ViewHolder> implements FastScroller.SectionIndexer {

  private static final String TAG = "ThreadAdapter";

  private List<ThreadModel> localDataSet;
  private final CurrentCourseViewModel currentCourseViewModel;
  private ThreadModel visibleItem;
  public ThreadAdapter(List<ThreadModel> dataSet, CurrentCourseViewModel currentCourseViewModel) {
    this.localDataSet = dataSet;
    this.currentCourseViewModel = currentCourseViewModel;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
    ThreadCardBinding binding = DataBindingUtil
            .inflate(LayoutInflater.from(viewGroup.getContext()),
                    R.layout.thread_card,
                    viewGroup,
                    false);
    return new ViewHolder(binding);
  }

  @Override
  public void onBindViewHolder(ViewHolder viewHolder, int position) {
    ThreadModel threadModel = this.localDataSet.get(position);
    viewHolder.connect(threadModel, currentCourseViewModel);
  }

  @Override
  public int getItemCount() {
    return this.localDataSet.size();
  }

  /** Viewholder for an thread item. */
  public class ViewHolder extends RecyclerView.ViewHolder {
    private final ThreadCardBinding binding;

    /** Constructor. */
    public ViewHolder(ThreadCardBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    public void connect(ThreadModel model, CurrentCourseViewModel currentCourseViewModel) {
      this.binding.setModel(model);
      this.binding.setVm(currentCourseViewModel);
      this.binding.executePendingBindings();
    }

  }

  /**Source: https://stackoverflow.com/questions/24989218/get-visible-items-in-recyclerview.*/
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
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd:MM:yyyy");
    return sdf.format(this.visibleItem.getCreationTime());

  }
}
