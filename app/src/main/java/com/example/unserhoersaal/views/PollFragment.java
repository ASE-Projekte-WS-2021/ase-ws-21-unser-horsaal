package com.example.unserhoersaal.views;

import android.annotation.SuppressLint;
import android.os.Bundle;
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
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.adapter.PollAdapter;
import com.example.unserhoersaal.databinding.FragmentPollBinding;
import com.example.unserhoersaal.model.PollModel;
import com.example.unserhoersaal.utils.StateData;
import com.example.unserhoersaal.viewmodel.PollViewModel;
import java.util.List;

/** View, which displays the voting during a meeting. */
public class PollFragment extends Fragment {

  private static final String TAG = "PollFragment";

  private FragmentPollBinding binding;
  private PollViewModel pollViewModel;
  private PollAdapter pollAdapter;

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
    this.connectAdapter();
    this.connectBinding();
  }

  private void initViewModel() {
    this.pollViewModel = new ViewModelProvider(requireActivity())
            .get(PollViewModel.class);
    this.pollViewModel.init();
    this.pollViewModel.getPolls().observe(getViewLifecycleOwner(), this:: pollsLiveDataCallback);
  }

  @SuppressLint("NotifyDataSetChanged")
  private void pollsLiveDataCallback(StateData<List<PollModel>> listStateData) {
    if (listStateData == null) {
      return;
    }
    this.resetBindings();
    this.pollViewModel.sortNewestFirst(listStateData.getData());
    this.pollAdapter.notifyDataSetChanged();

    if (listStateData.getStatus() == StateData.DataStatus.LOADING) {
      this.binding.pollFragmentProgressSpinner.setVisibility(View.VISIBLE);
    } else if (listStateData.getStatus() == StateData.DataStatus.ERROR) {
      Toast.makeText(getContext(),
              listStateData.getError().getMessage(), Toast.LENGTH_SHORT).show();
    }
    if (listStateData.getData().size() == 0) {
      this.binding.pollFragmentNoPollsTextView.setVisibility(View.VISIBLE);
    } else {
      this.binding.pollFragmentNoPollsTextView.setVisibility(View.GONE);
    }
  }

  private void resetBindings() {
    this.binding.pollFragmentProgressSpinner.setVisibility(View.GONE);
  }

  private void connectAdapter() {
    this.pollAdapter = new PollAdapter(this.pollViewModel.getPolls().getValue().getData(),
            this.pollViewModel);
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.pollViewModel);
    this.binding.setAdapter(this.pollAdapter);
  }

  @Override
  public void onResume() {
    super.onResume();
    this.pollViewModel.resetPollData();
  }
}