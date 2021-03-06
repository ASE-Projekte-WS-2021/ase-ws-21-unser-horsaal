package com.example.unserhoersaal.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.databinding.FragmentResetPasswordBinding;
import com.example.unserhoersaal.utils.KeyboardUtil;
import com.example.unserhoersaal.utils.StateData;
import com.example.unserhoersaal.viewmodel.LoginViewModel;

/** JavaDoc for this Fragment. */
public class ResetPasswordFragment extends Fragment {

  private FragmentResetPasswordBinding binding;
  private LoginViewModel loginViewModel;

  public ResetPasswordFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    this.binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reset_password, container,
            false);
    return this.binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    this.initViewModel();
    this.connectBinding();
  }

  private void initViewModel() {
    this.loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
    this.loginViewModel.init();
    this.loginViewModel
            .getEmailSentLiveData()
            .observe(getViewLifecycleOwner(), this::userLiveDataCallback);
  }


  private void userLiveDataCallback(StateData<Boolean> booleanStateData) {
    KeyboardUtil.hideKeyboard(getActivity());

    if (booleanStateData == null) {
      Toast.makeText(getContext(), Config.UNSPECIFIC_ERROR, Toast.LENGTH_SHORT).show();
      return;
    }

    if (booleanStateData.getStatus() == StateData.DataStatus.UPDATE) {
      Toast.makeText(getContext(),
              Config.AUTH_VERIFICATION_EMAIL_SENT,
              Toast.LENGTH_LONG)
              .show();
    }
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.loginViewModel);
  }

  @Override
  public void onPause() {
    super.onPause();
    this.loginViewModel.setDefaultInputState();
    this.loginViewModel.setLiveDataComplete();
  }

}