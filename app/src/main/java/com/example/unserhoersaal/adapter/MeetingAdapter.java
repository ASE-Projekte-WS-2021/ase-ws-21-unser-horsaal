package com.example.unserhoersaal.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.databinding.MeetingCardBinding;
import com.example.unserhoersaal.databinding.SimpleCourseMeetingBinding;
import com.example.unserhoersaal.model.MeetingsModel;
import java.util.List;

/** Adapter for the Meetings in CourseHistoryFragment. */
public class MeetingAdapter extends RecyclerView.Adapter<MeetingAdapter.ViewHolder> {

  private static final String TAG = "MeetingAdapter";

  private List<MeetingsModel> localDataSet;

  private String uid;
  private Boolean isCreator;

  public MeetingAdapter(List<MeetingsModel> dataSet, String uid, String creatorId) {
    this.localDataSet = dataSet;
    this.uid = uid;
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

    /** Constructor. */
    public ViewHolder(MeetingCardBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    public void connect(MeetingsModel model) {
      this.binding.setModel(model);
      this.binding.executePendingBindings();
      this.binding.setIsCreator(isCreator);
    }

  }

}
