package com.example.unserhoersaal.views;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.databinding.FragmentCreatePollBinding;
import com.example.unserhoersaal.model.PollModel;
import com.example.unserhoersaal.utils.KeyboardUtil;
import com.example.unserhoersaal.utils.StateData;
import com.example.unserhoersaal.utils.StateLiveData;
import com.example.unserhoersaal.viewmodel.PollViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

/**Create poll.*/
public class CreatePollFragment extends Fragment {

  private FragmentCreatePollBinding binding;
  private NavController navController;
  private PollViewModel pollViewModel;

  public CreatePollFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    this.binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_poll, container,
            false);
    return this.binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    this.navController = Navigation.findNavController(view);
    this.initViewModel();
    this.connectBinding();
    this.initToolbar();
    this.initSwitch();
    this.initAddRemoveOption();
  }

  private void initViewModel() {
    this.pollViewModel = new ViewModelProvider(requireActivity()).get(PollViewModel.class);
    this.pollViewModel.init();
    this.pollViewModel.getPollModel().observe(getViewLifecycleOwner(), this::pollLiveDataCallback);
  }

  private void pollLiveDataCallback(StateData<PollModel> pollModelStateData) {
    this.resetBindings();
    KeyboardUtil.hideKeyboard(getActivity());

    if (pollModelStateData.getStatus() == StateData.DataStatus.LOADING) {
      //TODO
    } else if (pollModelStateData.getStatus() == StateData.DataStatus.ERROR) {
      //TODO
    } else if (pollModelStateData.getStatus() == StateData.DataStatus.UPDATE){
      this.navController.navigateUp();
    }
  }

  private void resetBindings() {
    //TODO
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.pollViewModel);
  }

  private void initToolbar() {
    this.binding.createPollFragmentToolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
    this.binding.createPollFragmentToolbar.setNavigationOnClickListener( v ->
      this.navController.navigateUp()
    );
  }

  private void initSwitch() {
    this.binding.createPollFragmentSwitch.setOnClickListener(v -> this.toggleYesNoSwitch());
  }

  private void initAddRemoveOption() {
    this.binding.createPollFragmentAddOptionButton.setOnClickListener(v -> this.addPollOption());
    this.binding.createPollFragmentRemoveOptionButton.setOnClickListener(v ->
            this.removePollOption());
  }

  /** Toggles switch and changes visibility and hint of inputfields and buttons accordingly.*/
  private void toggleYesNoSwitch() {
    if (this.binding.createPollFragmentSwitch.isChecked()) {
      checkSwitch();
    } else {
      uncheckSwitch();
    }
  }

  private void checkSwitch() {
    this.binding.createPollFragmentAddOptionButton.setVisibility(View.GONE);
    this.binding.createPollFragmentSwitch.setChecked(true);
    this.removeAdditionalPollOptions();
    this.binding.createPollFragmentOption1InputField.setHint(R.string.create_poll_yes);
    this.binding.createPollFragmentOption2InputField.setHint(R.string.create_poll_no);
    this.binding.createPollFragmentOption1InputField.setEnabled(false);
    this.binding.createPollFragmentOption2InputField.setEnabled(false);
    this.binding.createPollFragmentOption1EditText.setText(null);
    this.binding.createPollFragmentOption2EditText.setText(null);
    this.binding.createPollFragmentOption3EditText.setText(null);
    this.binding.createPollFragmentOption4EditText.setText(null);
  }

  private void uncheckSwitch() {
    this.binding.createPollFragmentAddOptionButton.setVisibility(View.VISIBLE);
    this.binding.createPollFragmentAddOptionButton.setBackgroundTintList(ColorStateList
            .valueOf(getResources().getColor(R.color.app_blue, null)));
    this.binding.createPollFragmentAddOptionButton.setEnabled(true);
    this.binding.createPollFragmentSwitch.setChecked(false);
    this.binding.createPollFragmentOption1InputField.setHint(R.string.create_poll_option_1_hint);
    this.binding.createPollFragmentOption2InputField.setHint(R.string.create_poll_option_2_hint);
    this.binding.createPollFragmentOption1InputField.setEnabled(true);
    this.binding.createPollFragmentOption2InputField.setEnabled(true);
  }

  private void removeAdditionalPollOptions() {
    this.binding.createPollFragmentOption3InputField.setVisibility(View.GONE);
    this.binding.createPollFragmentOption4InputField.setVisibility(View.GONE);
    this.binding.createPollFragmentRemoveOptionButton.setVisibility(View.GONE);
  }

  private void addPollOption() {
    if (this.binding.createPollFragmentOption3InputField.getVisibility() == View.GONE) {
      this.binding.createPollFragmentOption3InputField.setVisibility(View.VISIBLE);
      this.binding.createPollFragmentRemoveOptionButton.setVisibility(View.VISIBLE);
    } else if (this.binding.createPollFragmentOption4InputField.getVisibility() == View.GONE) {
      this.binding.createPollFragmentOption4InputField.setVisibility(View.VISIBLE);
      this.binding.createPollFragmentAddOptionButton.setEnabled(false);
      this.binding.createPollFragmentAddOptionButton.setBackgroundTintList(ColorStateList
              .valueOf(getResources().getColor(R.color.grey, null)));
    }
  }

  private void removePollOption() {
    if (this.binding.createPollFragmentOption4InputField.getVisibility() == View.VISIBLE) {
      this.binding.createPollFragmentOption4EditText.setText(null);
      this.binding.createPollFragmentOption4InputField.setVisibility(View.GONE);
    } else if (this.binding.createPollFragmentOption3InputField.getVisibility() == View.VISIBLE) {
      this.binding.createPollFragmentOption3EditText.setText(null);
      this.binding.createPollFragmentOption3InputField.setVisibility(View.GONE);
      this.binding.createPollFragmentRemoveOptionButton.setVisibility(View.GONE);
    }
    this.binding.createPollFragmentAddOptionButton.setEnabled(true);
    this.binding.createPollFragmentAddOptionButton.setBackgroundTintList(ColorStateList
            .valueOf(getResources().getColor(R.color.app_blue, null)));
  }
}