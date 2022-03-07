package com.example.unserhoersaal.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.databinding.AnswerItemBinding;
import com.example.unserhoersaal.model.MessageModel;
import com.example.unserhoersaal.viewmodel.CurrentCourseViewModel;
import java.util.List;

/** Chatadapter. */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

  private static final String TAG = "ChatAdapter";

  private final List<MessageModel> localDataSet;
  private final CurrentCourseViewModel currentCourseViewModel;

  public ChatAdapter(List<MessageModel> dataSet, CurrentCourseViewModel currentCourseViewModel) {
    this.localDataSet = dataSet;
    this.currentCourseViewModel = currentCourseViewModel;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
    AnswerItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.answer_item, viewGroup, false);
    return new ViewHolder(binding);
  }

  @Override
  public void onBindViewHolder(ViewHolder viewHolder, int position) {
    MessageModel messageModel = this.localDataSet.get(position);
    viewHolder.connect(messageModel, currentCourseViewModel);
  }

  @Override
  public int getItemCount() {
    return this.localDataSet.size();
  }

  /** Viewholder. */
  public class ViewHolder extends RecyclerView.ViewHolder {

    private final AnswerItemBinding binding;

    /** Constructor. */
    public ViewHolder(AnswerItemBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    public void connect(MessageModel messageModel, CurrentCourseViewModel currentCourseViewModel) {
      this.binding.setModel(messageModel);
      this.binding.setVm(currentCourseViewModel);
      this.binding.executePendingBindings();
    }

  }

}

