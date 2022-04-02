package com.example.unserhoersaal.views;

import android.annotation.SuppressLint;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
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
  private LiveChatAdapter liveChatAdapter;
  private int messageSize;
  RecyclerView recyclerView;
  ScrollView scrollView;



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
    recyclerView.scrollToPosition(messageSize - 1);
    scrollView.fullScroll(ScrollView.FOCUS_DOWN);

    if (listStateData.getData() != null) {
      messageSize = listStateData.getData().size();
    }
    this.liveChatAdapter.notifyDataSetChanged();
    Log.d("Hier", "in messagelist callback: " + messageSize);

    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
    recyclerView.scrollToPosition(messageSize - 1);

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
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                recyclerView.scrollToPosition(messageSize - 1);
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
              }
            }
    );
  }

  private void connectBinding() {
    scrollView = binding.fragmentLiveChatScrollView;
    scrollView.fullScroll(ScrollView.FOCUS_DOWN);

    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.liveChatViewModel);
    this.binding.setAdapter(this.liveChatAdapter);
    this.binding.fragmentLiveChatScrollView.fullScroll(ScrollView.FOCUS_DOWN);
    recyclerView = this.binding.liveChatFragmentChatRecycler;
    recyclerView.scrollToPosition(messageSize - 1);

    scrollView.fullScroll(ScrollView.FOCUS_DOWN);


  }


}