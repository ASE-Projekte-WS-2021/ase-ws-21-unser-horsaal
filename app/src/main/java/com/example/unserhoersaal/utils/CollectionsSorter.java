package com.example.unserhoersaal.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.model.ThreadModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class CollectionsSorter {

  /**
   * Utility class for the sortings options of model lists.
   * Used to sort meetings, threads and answers by various parameters.
   */

  /** Sorting options for MeetingsModel lists*/

  /** sort by event time -ascending */
  public void sortMeetingListByEventTimeAsc(List<MeetingsModel> meetingsModelList) {
    Collections.sort(meetingsModelList, new Comparator<MeetingsModel>() {
      @Override
      public int compare(MeetingsModel meetingsModel, MeetingsModel t1) {
        return meetingsModel.getEventTime().compareTo(t1.getEventTime());
      }
    });
  }

  /** sort by event time -descending */
  public void sortMeetingListByEventTimeDesc(List<MeetingsModel> meetingsModelList) {
    Collections.sort(meetingsModelList, new Comparator<MeetingsModel>() {
      @Override
      public int compare(MeetingsModel meetingsModel, MeetingsModel t1) {
        return t1.getEventTime().compareTo(meetingsModel.getEventTime());
      }
    });
  }

  /** Sorting options for ThreadModel lists*/

  /** sort by creation time -descending */
  public void sortThreadListByEventTimeDesc(List<ThreadModel> threadsModelList) {
    Collections.sort(threadsModelList, new Comparator<ThreadModel>() {
      @Override
      public int compare(ThreadModel threadModel, ThreadModel t1) {
        if (threadModel.getCreationTime() == null || t1.getCreationTime() == null) {
          return 0;
        }
        return t1.getCreationTime().compareTo(threadModel.getCreationTime());
      }
    });
  }

  /** sort by likes -descending*/
  public void sortThreadListByLikesDesc(List<ThreadModel> threadsModelList) {
    Collections.sort(threadsModelList, new Comparator<ThreadModel>() {
      @Override
      public int compare(ThreadModel threadModel, ThreadModel t1) {
        return t1.getLikes() - threadModel.getLikes();
      }
    });
  }

  /** sort by comments/answers -descending*/
  public void sortThreadListByAnswersDesc(List<ThreadModel> threadsModelList) {
    Collections.sort(threadsModelList, new Comparator<ThreadModel>() {
      @Override
      public int compare(ThreadModel threadModel, ThreadModel t1) {
        return t1.getAnswersCount() - threadModel.getAnswersCount();
      }
    });
  }

  /** show just answered threads */
  public void filterThreadListByAnswerStatus(List<ThreadModel> threadsModelList, Boolean answered) {
    List<ThreadModel> newList = new ArrayList<>();
      for (int i = 0; i < threadsModelList.size(); i++) {
        if (threadsModelList.get(i).getAnswered() == answered) {
          newList.add(threadsModelList.get(i));
        }
      }
    threadsModelList.clear();
    threadsModelList.addAll(newList);
  }


}
