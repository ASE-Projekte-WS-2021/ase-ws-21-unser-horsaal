package com.example.unserhoersaal.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.databinding.MeetingCardBinding;
import com.example.unserhoersaal.model.MeetingsModel;
import java.util.List;

/**
 * Adapter for the Meetings in CourseHistoryFragment.
 */
public class MeetingAdapter extends RecyclerView.Adapter<MeetingAdapter.ViewHolder> {

  private final List<MeetingsModel> localDataSet;
  private final Boolean isCreator;

  /** Constructor of ThreadAdapter.
   *
   * @param  dataSet  includes the Data of all Meetings in the current Course.
   * @param  uid is the ViewModel of the CourseThreadFragment.
   * @param  creatorId is the Firebase id of the Creator of the Course
   */
  public MeetingAdapter(List<MeetingsModel> dataSet, String uid, String creatorId) {
    this.localDataSet = dataSet;
    if (uid.equals(creatorId)) {
      isCreator = true;
    } else {
      isCreator = false;
    }
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
    MeetingCardBinding binding = DataBindingUtil
            .inflate(LayoutInflater.from(viewGroup.getContext()),
                    R.layout.meeting_card,
                    viewGroup,
                    false);
    return new ViewHolder(binding);
  }

  @Override
  public void onBindViewHolder(ViewHolder viewHolder, int position) {
    MeetingsModel meetingsModel = localDataSet.get(position);
    viewHolder.connect(meetingsModel);
  }

  @Override
  public int getItemCount() {
    return this.localDataSet.size();
  }

  /** ViewHolder for a meeting item. */
  public class ViewHolder extends RecyclerView.ViewHolder {
    private final MeetingCardBinding binding;

    public ViewHolder(MeetingCardBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    /** Connecting Data to the XML of the ViewHolder.
     *
     * @param  model  is Model Data.
     */
    public void connect(MeetingsModel model) {
      this.binding.setModel(model);
      this.binding.executePendingBindings();
      this.binding.setIsCreator(isCreator);
    }

  }

}
