package com.example.unserhoersaal.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.model.ThreadModel;

import java.util.ArrayList;
import java.util.List;

public class ThreadAdapter extends RecyclerView.Adapter<ThreadAdapter.ViewHolder> {

  private static final String TAG = "ThreadAdapter";

  private List<ThreadModel> localDataSet = new ArrayList<>();
  private OnNoteListener onNoteListener;

  public ThreadAdapter(List<ThreadModel> dataSet, OnNoteListener onNoteListener) {
    this.localDataSet = dataSet;
    this.onNoteListener = onNoteListener;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
    View view = LayoutInflater.from(viewGroup.getContext())
            .inflate(R.layout.thread_item, viewGroup, false);
    return new ViewHolder(view, this.onNoteListener);
  }

  @Override
  public void onBindViewHolder(ViewHolder viewHolder, int position) {
    if (viewHolder.getThreadTitle() != null
            && viewHolder.getThreadText() != null
            && viewHolder.getThreadLikes() != null
            && viewHolder.getThreadTime() != null
            && viewHolder.getThreadAuthor() != null) {
      viewHolder.getThreadTitle().setText(this.localDataSet.get(position).getTitle());
      viewHolder.getThreadText().setText(this.localDataSet.get(position).getTextThread());
      viewHolder.getThreadLikes()
              .setText(Integer.toString(this.localDataSet.get(position).getLikes()));
      viewHolder.getThreadTime()
              .setText(Config.OLD_FORMAT.format(this.localDataSet.get(position).getCreationTime()));
      viewHolder.getThreadAuthor().setText(this.localDataSet.get(position).getCreatorId());
    }
  }

  @Override
  public int getItemCount() {
    return this.localDataSet.size();
  }

  public interface OnNoteListener {
    void onNoteClick(int position);
  }

  public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private TextView threadTitle;
    private TextView threadText;
    private TextView threadLikes;
    private TextView threadTime;
    private TextView threadAuthor;
    private OnNoteListener onNoteListener;

    public ViewHolder(View view, OnNoteListener onNoteListener) {
      super(view);
      this.onNoteListener = onNoteListener;
      this.threadTitle = view.findViewById(R.id.threadItemTitle);
      this.threadText = view.findViewById(R.id.threadItemDescription);
      this.threadLikes = view.findViewById(R.id.threadItemLikesCount);
      this.threadTime = view.findViewById(R.id.threadItemTime);
      this.threadAuthor = view.findViewById(R.id.threadItemAuthor);

      view.setOnClickListener(this);
    }

    public TextView getThreadTitle() {
      return this.threadTitle;
    }

    public TextView getThreadText() {
      return this.threadText;
    }

    public TextView getThreadLikes() {
      return this.threadLikes;
    }

    public TextView getThreadTime() {
      return this.threadTime;
    }

    public TextView getThreadAuthor() {
      return  this.threadAuthor;
    }

    @Override
    public void onClick(View v) {
      this.onNoteListener.onNoteClick(getAdapterPosition());
    }
  }
}
