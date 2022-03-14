package com.example.unserhoersaal.utils;

import com.example.unserhoersaal.model.MeetingsModel;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CollectionsSorter {

  /**
   * Utility class for the sortings options of model lists.
   * Used to sort meetings, threads and answers by various parameters.
   */

  /** Sorting options for MeetingsModel */

  /** sort by event time -ascending */
  public List<MeetingsModel> sortMeetingListByEventTimeAsc(List<MeetingsModel> meetingsModelList) {
    Collections.sort(meetingsModelList, new Comparator<MeetingsModel>() {
      @Override
      public int compare(MeetingsModel meetingsModel, MeetingsModel t1) {
        return meetingsModel.getEventTime().compareTo(t1.getEventTime());
      }
    });
    return meetingsModelList;
  }

  /** sort by event time -descending */
  public List<MeetingsModel> sortMeetingListByEventTimeDesc(List<MeetingsModel> meetingsModelList) {
    Collections.sort(meetingsModelList, new Comparator<MeetingsModel>() {
      @Override
      public int compare(MeetingsModel meetingsModel, MeetingsModel t1) {
        return t1.getEventTime().compareTo(meetingsModel.getEventTime());
      }
    });
    return meetingsModelList;
  }
}
