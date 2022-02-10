package com.example.unserhoersaal.adapter;



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.model.Message;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** Chatadapter. */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

  private static final String TAG = "ChatAdapter";

  private List<Message> localDataSet = new ArrayList<>();
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


  public ChatAdapter(List<Message> dataSet) {
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

    viewHolder.getMessage().setText(localDataSet.get(position).getMessageText());
    viewHolder.getDate().setText(calculateDate(localDataSet.get(position).getTime()));
  }

  @Override
  public int getItemCount() {
    return localDataSet.size();
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

