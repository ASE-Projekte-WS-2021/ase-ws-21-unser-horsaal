package com.example.unserhoersaal.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.model.MeetingsModel;
import java.util.ArrayList;
import java.util.List;

/** Adapter for the Meetings in CourseHistoryFragment. */
public class MeetingAdapter extends RecyclerView.Adapter<MeetingAdapter.ViewHolder> {

  private static final String TAG = "MeetingAdapter";

  private List<MeetingsModel> localDataSet = new ArrayList<>();
  private OnNoteListener onNoteListener;

  public MeetingAdapter(List<MeetingsModel> dataSet, OnNoteListener onNoteListener) {
    this.localDataSet = dataSet;
    this.onNoteListener = onNoteListener;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
    View view = LayoutInflater.from(viewGroup.getContext())
            .inflate(R.layout.simple_course_meeting, viewGroup, false);
    return new ViewHolder(view, this.onNoteListener);
  }

  @Override
  public void onBindViewHolder(ViewHolder viewHolder, int position) {
    if (viewHolder.getMeetingTitle() != null && viewHolder.getMeetingDate() != null) {
      viewHolder.getMeetingTitle().setText(this.localDataSet.get(position).getTitle());
      viewHolder.getMeetingDate()
              .setText(Config.OLD_FORMAT.format(this.localDataSet.get(position).getEventTime()));
    }
  }

  @Override
  public int getItemCount() {
    return this.localDataSet.size();
  }

  /** Interface to handle the click on an item. */
  public interface OnNoteListener {
    void onNoteClick(int position);
  }

  /** ViewHolder for a meeting item. */
  public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private TextView meetingTitle;
    private TextView meetingDate;
    private OnNoteListener onNoteListener;

    /** Constructor. */
    public ViewHolder(View view, OnNoteListener onNoteListener) {
      super(view);
      this.onNoteListener = onNoteListener;
      this.meetingTitle = view.findViewById(R.id.courseMeetingTitle);
      this.meetingDate = view.findViewById(R.id.courseMeetingDate);

      view.setOnClickListener(this);
    }

    public TextView getMeetingTitle() {
      return this.meetingTitle;
    }

    public TextView getMeetingDate() {
      return this.meetingDate;
    }

    @Override
    public void onClick(View v) {
      this.onNoteListener.onNoteClick(getAdapterPosition());
    }
  }
}
