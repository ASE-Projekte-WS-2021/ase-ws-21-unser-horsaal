package com.example.unserhoersaal.views;



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.model.DatabaseCourses;

import java.text.SimpleDateFormat;
import java.util.Date;

/** Chatadapter. */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

  private DatabaseCourses.Message[] localDataSet;
  public static final SimpleDateFormat RECENT_FORMAT = new SimpleDateFormat("HH:mm");
  public static final SimpleDateFormat OLD_FORMAT = new SimpleDateFormat("dd. MMMM");

  /** Viewholder. */
  public static class ViewHolder extends RecyclerView.ViewHolder {
    private final TextView message;
    private final TextView date;

    /** Constructor. */
    public ViewHolder(View view) {
      super(view);

      message = (TextView) view.findViewById(R.id.questionTextTextView);
      date = (TextView) view.findViewById(R.id.questionDateTextView);
    }

    public TextView getMessage() {
      return message;
    }

    public TextView getDate() {
      return date;
    }
  }


  public ChatAdapter(DatabaseCourses.Message[] dataSet) {
    localDataSet = dataSet;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
    View view = LayoutInflater.from(viewGroup.getContext())
            .inflate(R.layout.simple_question_item, viewGroup, false);

    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder viewHolder, final int position) {

    viewHolder.getMessage().setText(localDataSet[position].getMessageText());
    viewHolder.getDate().setText(calculateDate(localDataSet[position].getTime()));
  }

  @Override
  public int getItemCount() {
    return localDataSet.length;
  }

  private String calculateDate(Long timeInMillis) {
    String date;
    if (System.currentTimeMillis() - timeInMillis < 1000 * 3600 * 24) {
      date = RECENT_FORMAT.format(new Date(timeInMillis));
    } else {
      date = OLD_FORMAT.format(new Date(timeInMillis));
    }
    return date;
  }
}

