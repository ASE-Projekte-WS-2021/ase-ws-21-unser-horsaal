package com.example.unserhoersaal.utils;

import com.example.unserhoersaal.enums.FilterEnum;
import com.example.unserhoersaal.enums.SortEnum;
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
  public void sortThreadList(List<ThreadModel> threadsModelList, SortEnum sortingOption) {

    switch (sortingOption) {
      case NEWEST:
        sortThreadListByEventTimeDesc(threadsModelList);
        break;
      case MOST_LIKES:
        sortThreadListByLikesDesc(threadsModelList);
        break;
      case MOST_COMMENTED:
        sortThreadListByAnswersDesc(threadsModelList);
        break;
      case PAGE_COUNT_UP:
        sortThreadListByPageNumber(threadsModelList, "ascending");
        break;
      case PAGE_COUNT_DOWN:
        sortThreadListByPageNumber(threadsModelList, "descending");
        break;
    }
  }

  /** Filter options for ThreadModel lists. */
  public void filterThreadList(List<ThreadModel> threadsModelList,
                               List<ThreadModel> fullThreadsList, FilterEnum filterOption,
                               MeetingsModel currentMeeting, String userId) {

    switch (filterOption) {
      case SOLVED:
        filterThreadListByAnswerStatus(threadsModelList, true);
        break;
      case UNSOLVED:
        filterThreadListByAnswerStatus(threadsModelList, false);
        break;
      case CREATOR:
        filterThreadListByCourseProvider(threadsModelList, currentMeeting);
        break;
      case OWN:
        filterThreadListByOwnThreads(threadsModelList, userId);
        break;
      case SUBJECT_MATTER:
        filterThreadListByTag(threadsModelList, TagEnum.SUBJECT_MATTER);
        break;
      case ORGANISATION:
        filterThreadListByTag(threadsModelList, TagEnum.ORGANISATION);
        break;
      case MISTAKE:
        filterThreadListByTag(threadsModelList, TagEnum.MISTAKE);
        break;
      case EXAMINATION:
        filterThreadListByTag(threadsModelList, TagEnum.EXAMINATION);
        break;
      case OTHER:
        filterThreadListByTag(threadsModelList, TagEnum.OTHER);
        break;
      case NONE:
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
