package com.example.unserhoersaal.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.databinding.ThreadCardBinding;
import com.example.unserhoersaal.model.ThreadModel;
import com.example.unserhoersaal.utils.QueryBuilder;
import com.example.unserhoersaal.viewmodel.CurrentCourseViewModel;
import java.util.ArrayList;
import com.l4digital.fastscroll.FastScroller;
import java.text.SimpleDateFormat;
import java.util.List;

/** Adapter for the RecyclerView inCourseMeetingRepository. */
public class ThreadAdapter extends RecyclerView.Adapter<ThreadAdapter.ViewHolder> implements FastScroller.SectionIndexer {

  private static final String TAG = "ThreadAdapter";

  private final List<ThreadModel> originalLocalDataSet;
  private List<ThreadModel> filteredLocalDataSet;
  private final CurrentCourseViewModel currentCourseViewModel;
  private ThreadModel visibleItem;

  /** JavaDoc. */
  public ThreadAdapter(List<ThreadModel> dataSet, CurrentCourseViewModel currentCourseViewModel) {
    this.originalLocalDataSet = dataSet;
    this.filteredLocalDataSet = dataSet;
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
    ThreadModel threadModel = this.filteredLocalDataSet.get(position);
    viewHolder.connect(threadModel, currentCourseViewModel);
  }

  @Override
  public int getItemCount() {
    if (this.filteredLocalDataSet == null) {
      return 0;
    }

    return this.filteredLocalDataSet.size();
  }

  /** Filters local data set according to the user query and model properties
   * added to the query builder. */
  //code reference: https://stackoverflow.com/a/17960339/13620136
  public Filter getFilter() {
    return new Filter() {
      @Override
      protected FilterResults performFiltering(CharSequence query) {
        FilterResults filterResults = new FilterResults();
        ArrayList<ThreadModel> filteredList = new ArrayList<>();
        if (query == null || query.length() == 0) {
          filterResults.values = originalLocalDataSet;
          filterResults.count = originalLocalDataSet.size();
        } else {
          for (int i = 0; i < originalLocalDataSet.size(); i++) {
            ThreadModel threadModel = originalLocalDataSet.get(i);

            boolean queryResult = new QueryBuilder()
                    .addText(threadModel.getText())
                    .addPageNumber(threadModel.getPageNumber())
                    .addPageNumber(threadModel.getPageNumber())
                    .addHashTag(threadModel.getHashtag())
                    .addTags(threadModel.getTags())
                    .addCreatorName(threadModel.getCreatorName())
                    .matchUserQuery(query);

            if (queryResult) {
              filteredList.add(threadModel);
            }
          }
          filterResults.values = filteredList;
          filterResults.count = filteredList.size();
        }
        return filterResults;
      }

      @SuppressLint("NotifyDataSetChanged")
      @Override
      protected void publishResults(CharSequence constraint, FilterResults results) {
        filteredLocalDataSet = (List<ThreadModel>) results.values;
        notifyDataSetChanged();
      }
    };
  }

  /** Viewholder for an thread item. */
  public class ViewHolder extends RecyclerView.ViewHolder {
    private final ThreadCardBinding binding;

    /** Constructor. */
    public ViewHolder(ThreadCardBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    /** adds viewmodel and binding to each recyleview item. */
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
