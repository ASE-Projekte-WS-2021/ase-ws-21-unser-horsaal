package com.example.unserhoersaal.views;

import android.annotation.SuppressLint;
import android.database.DataSetObserver;
import android.inputmethodservice.Keyboard;
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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unserhoersaal.R;
import com.example.unserhoersaal.adapter.ChatAdapter;
import com.example.unserhoersaal.adapter.LiveChatAdapter;
import com.example.unserhoersaal.databinding.FragmentLiveChatBinding;
import com.example.unserhoersaal.model.LiveChatMessageModel;
import com.example.unserhoersaal.utils.KeyboardUtil;
import com.example.unserhoersaal.utils.StateData;
import com.example.unserhoersaal.utils.StateLiveData;
import com.example.unserhoersaal.viewmodel.LiveChatViewModel;

import java.util.List;

/** Displays the chat during a meeting. */
public class LiveChatFragment extends Fragment {

  private static final String TAG = "LiveChatFragment";

  private FragmentLiveChatBinding binding;
  private LiveChatViewModel liveChatViewModel;
  private NavController navController;
  public LiveChatAdapter liveChatAdapter;
  private int messageSize;
  RecyclerView recyclerView;



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

    if (listStateData.getData() != null) {
      messageSize = listStateData.getData().size();
    }
    this.liveChatAdapter.notifyDataSetChanged();
    Log.d("Hier", "in messagelist callback: " + messageSize);

    //recyclerView.scrollToPosition(messageSize - 1);

    if (listStateData.getStatus() == StateData.DataStatus.ERROR) {
      Toast.makeText(getContext(),
              listStateData.getError().getMessage(), Toast.LENGTH_SHORT).show();
    }
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
                Log.d("Hier", "in messagelist callback: " + messageSize);
                recyclerView.scrollToPosition(messageSize - 1);
              }
            }
    );
  }

  private void connectBinding() {

    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.liveChatViewModel);
    this.binding.setAdapter(this.liveChatAdapter);
    recyclerView = this.binding.liveChatFragmentChatRecycler;
    this.binding.setFragment(this);



  }

  public void sendMessage() {
    KeyboardUtil.hideKeyboard(getActivity());
    liveChatViewModel.sendMessage();
    binding.liveChatFragmentInputField.getEditText().getText().clear();
  }

  public void onClickEditText() {
    final Handler handler = new Handler(Looper.getMainLooper());
    handler.postDelayed(new Runnable() {
      @Override
      public void run() {
        liveChatAdapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(messageSize - 1);
      }
    }, 500);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    KeyboardUtil.hideKeyboard(getActivity());
  }
}