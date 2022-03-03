package com.example.unserhoersaal.views;

import android.app.Activity;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.databinding.FragmentEnterCourseBinding;
import com.example.unserhoersaal.utils.DeepLinkEnum;
import com.example.unserhoersaal.utils.DeepLinkMode;
import com.example.unserhoersaal.utils.KeyboardUtil;
import com.example.unserhoersaal.viewmodel.CourseHistoryViewModel;
import com.example.unserhoersaal.viewmodel.EnterCourseViewModel;
import com.google.zxing.Result;

/** Fragment for entering a course.*/
public class EnterCourseFragment extends Fragment {

  private static final String TAG = "EnterCourseFragment";

  private EnterCourseViewModel enterCourseViewModel;
  private CourseHistoryViewModel courseHistoryViewModel;
  private NavController navController;
  private FragmentEnterCourseBinding binding;
  private DeepLinkMode deepLinkMode;
  private CodeScanner codeScanner;

  public EnterCourseFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    this.binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_enter_course, container, false);
    return this.binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    this.navController = Navigation.findNavController(view);
    this.deepLinkMode = DeepLinkMode.getInstance();

    this.initViewModel();
    this.connectBinding();
    this.setupToolbar();
    //this.initQrCodeScanner(view);

    if (this.deepLinkMode.getDeepLinkMode() == DeepLinkEnum.ENTER_COURSE) {
      this.enterCourseViewModel.dataBindingCourseIdInput.setValue(this.deepLinkMode.getCodeMapping());
      this.enterCourseViewModel.checkCode();
      this.binding.enterCourseFragmentConfirmationDialog.setVisibility(View.VISIBLE);
    }
  }

  private void initViewModel() {
    this.enterCourseViewModel = new ViewModelProvider(requireActivity())
            .get(EnterCourseViewModel.class);
    this.courseHistoryViewModel = new ViewModelProvider(requireActivity())
            .get(CourseHistoryViewModel.class);
    this.enterCourseViewModel.init();
    this.courseHistoryViewModel.init();
    this.enterCourseViewModel.getCourse()
            .observe(getViewLifecycleOwner(), model -> {
              if (model != null) {
                KeyboardUtil.hideKeyboard(getActivity());
                this.binding.enterCourseFragmentConfirmationDialog.setVisibility(View.VISIBLE);
              }
            });
    this.enterCourseViewModel.getCourseId().observe(getViewLifecycleOwner(), id -> {
      if (id != null) {
        openNewCourse(id);
      }
    });
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.enterCourseViewModel);
  }

  /** Creates a new course if the code is correct. */
  public void openNewCourse(String id) {
    KeyboardUtil.hideKeyboard(getActivity());
    this.courseHistoryViewModel.setCourseId(id);
    this.enterCourseViewModel.resetEnterCourseId();
    this.deepLinkMode.setDeepLinkMode(DeepLinkEnum.DEFAULT);
    this.navController.navigate(R.id.action_enterCourseFragment_to_courseHistoryFragment);
  }

  private void setupToolbar() {
    this.binding.enterCourseFragmentToolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
    this.binding.enterCourseFragmentToolbar.setNavigationOnClickListener(v -> {
      this.navController.navigate(R.id.action_enterCourseFragment_to_coursesFragment);
    });
  }

  //https://github.com/yuriy-budiyev/code-scanner
  private void initQrCodeScanner(View view) {
    //TODO: @micha; need exit button!!! --> extra fragment?
    this.binding.enterCourseFragmentQrImage.setOnClickListener(v -> {
      this.binding.enterCourseFragmentCodeScannerView.setVisibility(View.VISIBLE);
      final Activity activity = getActivity();
      CodeScannerView scannerView = view.findViewById(R.id.scanner_view);
      assert activity != null;
      codeScanner = new CodeScanner(activity, scannerView);
      codeScanner.setDecodeCallback(result
              -> activity.runOnUiThread(()
              -> Toast.makeText(activity, result.getText(), Toast.LENGTH_SHORT).show()));
      scannerView.setOnClickListener(view1
              -> codeScanner.startPreview());
    });
  }

  @Override
  public void onPause() {
    super.onPause();
    this.codeScanner.releaseResources();
  }
}