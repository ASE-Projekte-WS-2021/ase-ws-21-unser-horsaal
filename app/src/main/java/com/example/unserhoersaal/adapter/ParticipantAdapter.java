package com.example.unserhoersaal.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.databinding.ParticipantListItemBinding;
import com.example.unserhoersaal.model.UserModel;
import java.util.List;

public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantAdapter.ViewHolder> {

  private static final String TAG = "ParticipantAdapter";

  private List<UserModel> localDataSet;

  public ParticipantAdapter(List<UserModel> dataSet) {
    this.localDataSet = dataSet;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
    ParticipantListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.participant_list_item, viewGroup, false);
    return new ViewHolder(binding);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
    UserModel userModel = this.localDataSet.get(position);
    viewHolder.connect(userModel);
  }

  @Override
  public int getItemCount() {
    return this.localDataSet.size();
  }

  /** Viewholder for an thread item. */
  public class ViewHolder extends RecyclerView.ViewHolder {
    private final ParticipantListItemBinding binding;

    /** Constructor. */
    public ViewHolder(ParticipantListItemBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    public void connect(UserModel model) {
      this.binding.setModel(model);
      this.binding.executePendingBindings();
    }

  }
}
