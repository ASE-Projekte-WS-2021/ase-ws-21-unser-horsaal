package com.example.unserhoersaal.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.model.Message;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** Chatadapter. */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

  private static final String TAG = "ChatAdapter";

  private List<Message> localDataSet = new ArrayList<>();

  public ChatAdapter(List<Message> dataSet) {
    this.localDataSet = dataSet;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
    View view = LayoutInflater.from(viewGroup.getContext())
            .inflate(R.layout.simple_question_item, viewGroup, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder viewHolder, int position) {
    viewHolder.getMessage().setText(this.localDataSet.get(position).getMessageText());
    viewHolder.getDate().setText(calculateDate(this.localDataSet.get(position).getTime()));
  }

  @Override
  public int getItemCount() {
    return this.localDataSet.size();
  }

  private String calculateDate(Long timeInMillis) {
    String date;
    if (System.currentTimeMillis() - timeInMillis < 1000 * 3600 * 24) {
      date = Config.RECENT_FORMAT.format(new Date(timeInMillis));
    } else {
      date = Config.OLD_FORMAT.format(new Date(timeInMillis));
    }
    return date;
  }

  /** Viewholder. */
  public class ViewHolder extends RecyclerView.ViewHolder {

    private TextView message;
    private TextView date;

    /** Constructor. */
    public ViewHolder(View view) {
      super(view);

      this.message = (TextView) view.findViewById(R.id.questionTextTextView);
      this.date = (TextView) view.findViewById(R.id.questionDateTextView);
    }

    public TextView getMessage() {
      return this.message;
    }

    public TextView getDate() {
      return this.date;
    }
  }
}

