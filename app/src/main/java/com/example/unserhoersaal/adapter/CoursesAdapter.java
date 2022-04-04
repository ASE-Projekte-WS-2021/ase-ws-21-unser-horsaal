package com.example.unserhoersaal.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.databinding.CourseItemCardBinding;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.utils.QueryBuilder;
import java.util.ArrayList;
import java.util.List;

/** Coursesadapter. */
public class CoursesAdapter extends RecyclerView.Adapter<CoursesAdapter.ViewHolder> {

  private static final String TAG = "CoursesAdapter";

  private final List<CourseModel> originalLocalDataSet;
  private List<CourseModel> filteredLocalDataSet;

  public CoursesAdapter(List<CourseModel> dataSet) {
    this.originalLocalDataSet = dataSet;
    this.filteredLocalDataSet = dataSet;
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
    CourseModel courseModel = filteredLocalDataSet.get(position);
    viewHolder.connect(courseModel);
  }

  @Override
  public int getItemCount() {
    return this.filteredLocalDataSet.size();
  }

  //code reference: https://stackoverflow.com/a/17960339/13620136
  /** JavaDoc. */
  public Filter getFilter() {
    return new Filter() {
      @Override
      protected FilterResults performFiltering(CharSequence query) {
        FilterResults filterResults = new FilterResults();
        ArrayList<CourseModel> filteredList = new ArrayList<>();
        if (query == null || query.length() == 0) {
          filterResults.values = originalLocalDataSet;
          filterResults.count = originalLocalDataSet.size();
        } else {
          for (int i = 0; i < originalLocalDataSet.size(); i++) {
            CourseModel courseModel = originalLocalDataSet.get(i);

            boolean queryResult = new QueryBuilder()
                    .addTitle(courseModel.getTitle())
                    .addInstitution(courseModel.getInstitution())
                    .addCreatorName(courseModel.getCreatorName())
                    .addDescription(courseModel.getDescription())
                    .matchUserQuery(query);

            if (queryResult) {
              filteredList.add(courseModel);
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
        filteredLocalDataSet = (List<CourseModel>) results.values;
        notifyDataSetChanged();
      }
    };
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
