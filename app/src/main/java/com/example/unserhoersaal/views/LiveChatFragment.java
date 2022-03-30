package com.example.unserhoersaal.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unserhoersaal.R;
import com.example.unserhoersaal.databinding.FragmentLiveChatBinding;
import com.example.unserhoersaal.viewmodel.LiveChatViewModel;

/** Displays the chat during a meeting. */
public class LiveChatFragment extends Fragment {

  private static final String TAG = "LiveChatFragment";

  private FragmentLiveChatBinding binding;
  private LiveChatViewModel liveChatViewModel;

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
    initViewModel();
    this.connectBinding();
    this.setupScrolling();
  }

  private void initViewModel() {
    this.liveChatViewModel = new ViewModelProvider(requireActivity())
            .get(LiveChatViewModel.class);
    this.liveChatViewModel.init();
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
  }

  /**Hides Infocontainer when scrolling down and shows it when scrolling to top.*/
  private void setupScrolling() {
    View infocontainer = this.binding.liveChatFragmentInfoContainer;
    this.binding.liveChatFragmentChatRecycler.addOnScrollListener(new RecyclerView
            .OnScrollListener() {

      @Override
      public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (!recyclerView.canScrollVertically(-1)) {
          infocontainer.setVisibility(View.VISIBLE);
        } else {
          infocontainer.setVisibility(View.GONE);
        }
      }
    });
  }
}