package com.example.unserhoersaal.utils;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.unserhoersaal.enums.TagEnum;
import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.model.MessageModel;
import com.example.unserhoersaal.model.ThreadModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class ArrayListUtil {

  /**
   * Utility class for the sorting and filter options for model lists.
   * Used to sort and filter meetings, threads and answers by various parameters.
   */

  /** Sorting options for MeetingsModel lists. */
  public void sortMeetingList(List<MeetingsModel> meetingsModelList, String sortingOption) {

    switch (sortingOption) {
      case "newest":
        sortMeetingListByEventTime(meetingsModelList, "descending");
        break;
      case "oldest":
        sortMeetingListByEventTime(meetingsModelList, "ascending");
        break;
    }
  }

  /** Sorting options for ThreadModel lists. */
  public void sortThreadList(List<ThreadModel> threadsModelList, String sortingOption) {

    switch (sortingOption) {
      case "newest":
        sortThreadListByEventTimeDesc(threadsModelList);
        break;
      case "likes":
        sortThreadListByLikesDesc(threadsModelList);
        break;
      case "answers":
        sortThreadListByAnswersDesc(threadsModelList);
        break;
      case "page number asc":
        sortThreadListByPageNumber(threadsModelList, "ascending");
        break;
      case "page number desc":
        sortThreadListByPageNumber(threadsModelList, "descending");
        break;
    }
  }

  /** Filter options for ThreadModel lists. */
  public void filterThreadList(List<ThreadModel> threadsModelList,
                               List<ThreadModel> fullThreadsList, String filterOption,
                               MeetingsModel currentMeeting, String userId) {

    switch (filterOption) {
      case "answered":
        filterThreadListByAnswerStatus(threadsModelList, true);
        break;
      case "not answered":
        filterThreadListByAnswerStatus(threadsModelList, false);
        break;
      case "course provider":
        filterThreadListByCourseProvider(threadsModelList, currentMeeting);
        break;
      case "own":
        filterThreadListByOwnThreads(threadsModelList, userId);
        break;
      case "tag subject matter":
        filterThreadListByTag(threadsModelList, TagEnum.SUBJECT_MATTER);
        break;
      case "tag organisation":
        filterThreadListByTag(threadsModelList, TagEnum.ORGANISATION);
        break;
      case "tag error":
        filterThreadListByTag(threadsModelList, TagEnum.MISTAKE);
        break;
      case "tag examination":
        filterThreadListByTag(threadsModelList, TagEnum.EXAMINATION);
        break;
      case "tag other":
        filterThreadListByTag(threadsModelList, TagEnum.OTHER);
        break;
      case "reset":
        resetThreadList(threadsModelList, fullThreadsList);
        break;
    }
  }

  /** Sort answers by likes. */
  public void sortAnswersByLikes(List<MessageModel> messageModelList) {
    messageModelList.sort(new Comparator<MessageModel>() {
      @Override
      public int compare(MessageModel messageModel, MessageModel t1) {
        return t1.getLikes() - messageModel.getLikes();
      }
    });
  }


  /** Sort meetings by event time. */
  private void sortMeetingListByEventTime(List<MeetingsModel> meetingsModelList, String order) {
    meetingsModelList.sort(new Comparator<MeetingsModel>() {
      @Override
      public int compare(MeetingsModel meetingsModel, MeetingsModel t1) {
        if (order.equals("ascending")) {
          return meetingsModel.getEventTime().compareTo(t1.getEventTime());
        }
        return t1.getEventTime().compareTo(meetingsModel.getEventTime());
      }
    });
  }

  /** Sort threads by creation time -descending */
  private void sortThreadListByEventTimeDesc(List<ThreadModel> threadsModelList) {
    threadsModelList.sort(new Comparator<ThreadModel>() {
      @Override
      public int compare(ThreadModel threadModel, ThreadModel t1) {
        if (threadModel.getCreationTime() == null || t1.getCreationTime() == null) {
          return 0;
        }
        return t1.getCreationTime().compareTo(threadModel.getCreationTime());
      }
    });
  }

  /** Sort threads by likes -descending */
  private void sortThreadListByLikesDesc(List<ThreadModel> threadsModelList) {
    threadsModelList.sort(new Comparator<ThreadModel>() {
      @Override
      public int compare(ThreadModel threadModel, ThreadModel t1) {
        return t1.getLikes() - threadModel.getLikes();
      }
    });
  }

  /** Sort threads by comments/answers -descending */
  private void sortThreadListByAnswersDesc(List<ThreadModel> threadsModelList) {
    threadsModelList.sort(new Comparator<ThreadModel>() {
      @Override
      public int compare(ThreadModel threadModel, ThreadModel t1) {
        return t1.getAnswersCount() - threadModel.getAnswersCount();
      }
    });
  }

  /** Sort threads by the page number. */
  private void sortThreadListByPageNumber(List<ThreadModel> threadsModelList, String order) {
    threadsModelList.sort(new Comparator<ThreadModel>() {
      @Override
      public int compare(ThreadModel threadModel, ThreadModel t1) {
        if (order.equals("ascending")) {
          return Integer.parseInt(threadModel.getPageNumber()) -
                  Integer.parseInt(t1.getPageNumber());
        } else {
          return Integer.parseInt(t1.getPageNumber()) -
                  Integer.parseInt(threadModel.getPageNumber());
        }
      }
    });
  }

  /** Filter threads by answered status. */
  private void filterThreadListByAnswerStatus(List<ThreadModel> threadsModelList,
                                              Boolean answered) {
    List<ThreadModel> filteredList = new ArrayList<>();
      for (int i = 0; i < threadsModelList.size(); i++) {
        if (threadsModelList.get(i).getAnswered() == answered) {
          filteredList.add(threadsModelList.get(i));
        }
      }
    threadsModelList.clear();
    threadsModelList.addAll(filteredList);
  }

  /** Filter threads and just show threads created by the course provider. */
  private void filterThreadListByCourseProvider(List<ThreadModel> threadsModelList,
                                                MeetingsModel currentMeeting) {
    List<ThreadModel> filteredList = new ArrayList<>();
    for (int i = 0; i < threadsModelList.size(); i++) {
      if (threadsModelList.get(i).getCreatorId() == currentMeeting.getCreatorId()) {
        filteredList.add(threadsModelList.get(i));
      }
    }
    threadsModelList.clear();
    threadsModelList.addAll(filteredList);
  }

  /** Filter threads and just show threads created by the current user. */
  private void filterThreadListByOwnThreads(List<ThreadModel> threadsModelList,
                                            String userId) {
    List<ThreadModel> filteredList = new ArrayList<>();
    for (int i = 0; i < threadsModelList.size(); i++) {
      if (threadsModelList.get(i).getCreatorId() == userId) {
        filteredList.add(threadsModelList.get(i));
      }
    }
    threadsModelList.clear();
    threadsModelList.addAll(filteredList);
  }

  /** Filter threads by tag. */
  private void filterThreadListByTag(List<ThreadModel> threadsModelList, TagEnum tagEnum) {
    List<ThreadModel> filteredList = new ArrayList<>();
    for (int i = 0; i < threadsModelList.size(); i++) {
      List<TagEnum> usedTags = new ArrayList<>(threadsModelList.get(i).getTags());
      for (int j = 0; j < usedTags.size(); j++) {
        if (usedTags.get(j) == tagEnum) {
          filteredList.add(threadsModelList.get(i));
        }
      }
      }
    threadsModelList.clear();
    threadsModelList.addAll(filteredList);
    }

  /** Reset filter/show all threads */
  private void resetThreadList(List<ThreadModel> threadModelList,
                               List<ThreadModel> fullThreadModelList) {
    threadModelList.clear();
    threadModelList.addAll(fullThreadModelList);
  }
}
