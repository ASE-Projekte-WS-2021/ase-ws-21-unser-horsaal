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
import com.example.unserhoersaal.databinding.FragmentPollBinding;
import com.example.unserhoersaal.viewmodel.PollViewModel;

/** View, which displays the voting during a meeting. */
public class PollFragment extends Fragment {

  private static final String TAG = "PollFragment";

  private FragmentPollBinding binding;
  private PollViewModel pollViewModel;

  public PollFragment() {
    // Required empty public constructor
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
            R.layout.fragment_poll, container, false);
    return this.binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    this.initViewModel();
    this.connectBinding();
    this.setupScrolling();
  }

  private void initViewModel() {
    this.pollViewModel = new ViewModelProvider(requireActivity())
            .get(PollViewModel.class);
    this.pollViewModel.init();
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
  }

  /**Hides Infocontainer when scrolling down and shows it when scrolling to top.*/
  private void setupScrolling() {
    View infocontainer = this.binding.pollFragmentInfoContainer;
    this.binding.pollFragmentPollRecycler.addOnScrollListener(new RecyclerView
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