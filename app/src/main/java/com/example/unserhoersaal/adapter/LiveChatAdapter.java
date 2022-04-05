package com.example.unserhoersaal.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unserhoersaal.R;
import com.example.unserhoersaal.databinding.LiveChatItemBinding;
import com.example.unserhoersaal.model.LiveChatMessageModel;
import com.example.unserhoersaal.viewmodel.LiveChatViewModel;
import com.l4digital.fastscroll.FastScroller;

import java.text.SimpleDateFormat;
import java.util.List;

/** Chatadapter. */
public class LiveChatAdapter extends RecyclerView.Adapter<LiveChatAdapter.ViewHolder>
        implements FastScroller.SectionIndexer{

  private static final String TAG = "ChatAdapter";

  private final List<LiveChatMessageModel> localDataSet;
  private final LiveChatViewModel liveChatViewModel;
  private LiveChatMessageModel visibleItem;

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
          int visiblePosition = linearLayoutManager.findFirstVisibleItemPosition();
          if (visiblePosition == )
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
    if (visibleItem == null) {
      return null;
    }
    return sdf.format(this.visibleItem.getCreationTime());
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
