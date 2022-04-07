package com.example.unserhoersaal.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unserhoersaal.R;
import com.example.unserhoersaal.databinding.LiveChatItemBinding;
import com.example.unserhoersaal.model.LiveChatMessageModel;
import com.example.unserhoersaal.viewmodel.LiveChatViewModel;

import java.util.List;

/** Chatadapter. */
public class LiveChatAdapter extends RecyclerView.Adapter<LiveChatAdapter.ViewHolder> {

  private static final String TAG = "ChatAdapter";

  private final List<LiveChatMessageModel> localDataSet;
  private final LiveChatViewModel liveChatViewModel;

  public LiveChatAdapter(List<LiveChatMessageModel> dataSet, LiveChatViewModel liveChatViewModel) {
    this.localDataSet = dataSet;
    this.liveChatViewModel = liveChatViewModel;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
    LiveChatItemBinding binding = DataBindingUtil
            .inflate(LayoutInflater.from(viewGroup.getContext()),
                    R.layout.live_chat_item,
                    viewGroup,
                    false);
    return new ViewHolder(binding);
  }

  @Override
  public void onBindViewHolder(ViewHolder viewHolder, int position) {
    LiveChatMessageModel liveChatMessageModel = this.localDataSet.get(position);
    viewHolder.connect(liveChatMessageModel, liveChatViewModel);
  }

  @Override
  public int getItemCount() {
    return this.localDataSet.size();
  }

  /** Viewholder. */
  public class ViewHolder extends RecyclerView.ViewHolder {

    private final LiveChatItemBinding binding;

    /** Constructor. */
    public ViewHolder(LiveChatItemBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    /** connect updated list with adapter child. */
    public void connect(LiveChatMessageModel liveChatMessageModel, LiveChatViewModel liveChatViewModel) {
      this.binding.setModel(liveChatMessageModel);
      this.binding.setVm(liveChatViewModel);
      this.binding.executePendingBindings();
    }

  }

}
