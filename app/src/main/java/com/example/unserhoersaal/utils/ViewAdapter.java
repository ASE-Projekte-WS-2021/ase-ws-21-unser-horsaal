package com.example.unserhoersaal.utils;

import android.view.View;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ViewAdapter {

  @BindingAdapter("initLayoutManager")
  public static void initLayoutManager(View view, int id) {
    RecyclerView courseListRecycler = view.findViewById(id);
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
    courseListRecycler.setLayoutManager(layoutManager);
    courseListRecycler.setItemAnimator(new DefaultItemAnimator());
  }
}
