package com.example.unserhoersaal.views;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.adapter.LiveChatAdapter;
import com.example.unserhoersaal.databinding.FragmentLiveChatBinding;
import com.example.unserhoersaal.model.LiveChatMessageModel;
import com.example.unserhoersaal.utils.KeyboardUtil;
import com.example.unserhoersaal.utils.StateData;
import com.example.unserhoersaal.viewmodel.LiveChatViewModel;
import com.l4digital.fastscroll.FastScrollRecyclerView;

import java.security.Key;
import java.util.List;

/** Displays the chat during a meeting. */
public class LiveChatFragment extends Fragment {

  private static final String TAG = "LiveChatFragment";

  private FragmentLiveChatBinding binding;
  private LiveChatViewModel liveChatViewModel;
  private NavController navController;
  public LiveChatAdapter liveChatAdapter;
  private int messageSize;
  private FastScrollRecyclerView recyclerView;

  public LiveChatFragment() {
    //Required empty public constructor
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    this.binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_live_chat, container, false);
    return this.binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    this.initViewModel();
    this.connectAdapter();
    this.connectBinding();
    this.setupScrolling();
  }

  @SuppressLint("NotifyDataSetChanged")
  private void initViewModel() {
    this.liveChatViewModel = new ViewModelProvider(requireActivity())
            .get(LiveChatViewModel.class);
    this.liveChatViewModel.init();
    this.liveChatViewModel.getSldLiveChatMessages()
            .observe(getViewLifecycleOwner(), this::messageLiveDataCallback);
  }

  @SuppressLint("NotifyDataSetChanged")
  private void messageLiveDataCallback(StateData<List<LiveChatMessageModel>> listStateData) {
    this.resetBinding();
    KeyboardUtil.hideKeyboard(getActivity());

    if (listStateData.getData() != null) {
      messageSize = listStateData.getData().size();
    }
    this.liveChatAdapter.notifyDataSetChanged();
    Log.d(TAG, "in messagelist callback: " + messageSize);

    //recyclerView.scrollToPosition(messageSize - 1);
    if (listStateData.getStatus() == StateData.DataStatus.LOADING) {
      this.binding.liveChatFragmentSendButton.setEnabled(false);
    } else if (listStateData.getStatus() == StateData.DataStatus.ERROR) {
      Toast.makeText(getContext(),
              listStateData.getError().getMessage(), Toast.LENGTH_SHORT).show();
    }
  }

  private void resetBinding() {
    this.binding.liveChatFragmentSendButton.setEnabled(true);
  }

  private void connectAdapter() {
    this.liveChatAdapter =
            new LiveChatAdapter(this.liveChatViewModel.getSldLiveChatMessages().getValue().getData(),
                    this.liveChatViewModel);
    this.liveChatAdapter.registerAdapterDataObserver(
            new RecyclerView.AdapterDataObserver() {
              @Override
              public void onChanged() {
                super.onChanged();
                Log.d(TAG, "in messagelist callback: " + messageSize);
                recyclerView.scrollToPosition(messageSize - 1);
              }
            }
    );
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.liveChatViewModel);
    this.binding.setAdapter(this.liveChatAdapter);
    this.recyclerView = this.binding.liveChatFragmentChatRecycler;
    this.binding.setFragment(this);
  }

  public void sendMessage() {
    liveChatViewModel.sendMessage();
    binding.liveChatFragmentInputField.getEditText().getText().clear();
  }

  @SuppressLint("NotifyDataSetChanged")
  public void onClickEditText() {
    final Handler handler = new Handler(Looper.getMainLooper());
    handler.postDelayed(() -> {
      liveChatAdapter.notifyDataSetChanged();
      recyclerView.scrollToPosition(messageSize - 1);
    }, 500);
  }

  @Override
  public void onPause() {
    super.onPause();
    KeyboardUtil.hideKeyboard(getActivity());
  }

  private void setupScrolling() {
    View infoContainer = this.binding.liveChatFragmentInfoContainer;
    this.binding.liveChatFragmentChatRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (!recyclerView.canScrollVertically(-1)) {
          infoContainer.setVisibility(View.VISIBLE);
        } else {
          infoContainer.setVisibility(View.GONE);
        }
      }
    });
  }
}