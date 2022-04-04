package com.example.unserhoersaal.utils;

import com.example.unserhoersaal.enums.FilterEnum;
import com.example.unserhoersaal.enums.SortEnum;
import com.example.unserhoersaal.enums.TagEnum;
import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.model.MessageModel;
import com.example.unserhoersaal.model.ThreadModel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility class for the sorting and filter options for model lists.
 * Used to sort and filter meetings, threads and answers by various parameters.
 */
public class ArrayListUtil {

  private static final String TAG = "ArrayListUtil";

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
      default:
        break;
    }
  }

  /** Filter options for ThreadModel lists. */
  public void filterThreadList(List<ThreadModel> threadsModelList,
                               StateLiveData<List<ThreadModel>> outFilteredThreads,
                               FilterEnum filterOption, ArrayList<FilterEnum> enumArray,
                               MeetingsModel currentMeeting, String userId) {

    switch (filterOption) {
      case SOLVED:
        filterThreadListByAnswerStatus(threadsModelList, true, enumArray,
                outFilteredThreads);
        break;
      case UNSOLVED:
        filterThreadListByAnswerStatus(threadsModelList, false, enumArray,
                outFilteredThreads);
        break;
      case CREATOR:
        filterThreadListByCourseProvider(threadsModelList, currentMeeting, outFilteredThreads);
        break;
      case OWN:
        filterThreadListByOwnThreads(threadsModelList, userId, outFilteredThreads);
        break;
      case LECTURE_PERIOD:
        filterThreadListByLecturePeriod(threadsModelList, currentMeeting, outFilteredThreads);
        break;
      case SUBJECT_MATTER:
        filterThreadListByTag(threadsModelList, TagEnum.SUBJECT_MATTER,  outFilteredThreads);
        break;
      case ORGANISATION:
        filterThreadListByTag(threadsModelList, TagEnum.ORGANISATION, outFilteredThreads);
        break;
      case MISTAKE:
        filterThreadListByTag(threadsModelList, TagEnum.MISTAKE, outFilteredThreads);
        break;
      case EXAMINATION:
        filterThreadListByTag(threadsModelList, TagEnum.EXAMINATION, outFilteredThreads);
        break;
      case OTHER:
        filterThreadListByTag(threadsModelList, TagEnum.OTHER, outFilteredThreads);
        break;
      case NONE:
        resetThreadList(threadsModelList, outFilteredThreads, enumArray, currentMeeting, userId);
        break;
      case RESET:
        refreshList(threadsModelList, outFilteredThreads);
        break;
      default:
        break;
    }
  }

  /** Sort answers by likes. */
  public void sortAnswersByLikes(List<MessageModel> messageModelList) {
    messageModelList.sort((messageModel, t1) -> t1.getLikes() - messageModel.getLikes());
  }

  /** Sort meetings by event time. */
  public void sortMeetingListByEventTime(List<MeetingsModel> meetingsModelList) {
    meetingsModelList.sort((meetingsModel, t1) ->
            t1.getEventTime().compareTo(meetingsModel.getEventTime()));
  }

  /** Sort threads by creation time - descending. */
  private void sortThreadListByEventTimeDesc(List<ThreadModel> threadsModelList) {
    threadsModelList.sort((threadModel, t1) -> {
      if (threadModel.getCreationTime() == null || t1.getCreationTime() == null) {
        return 0;
      }
      return t1.getCreationTime().compareTo(threadModel.getCreationTime());
    });
  }

  /** Sort threads by likes - descending. */
  private void sortThreadListByLikesDesc(List<ThreadModel> threadsModelList) {
    threadsModelList.sort((threadModel, t1) -> t1.getLikes() - threadModel.getLikes());
  }

  /** Sort threads by comments/answers -descending. */
  private void sortThreadListByAnswersDesc(List<ThreadModel> threadsModelList) {
    threadsModelList.sort((threadModel, t1) ->
            t1.getAnswersCount() - threadModel.getAnswersCount());
  }

  /** Sort threads by the page number. */
  private void sortThreadListByPageNumber(List<ThreadModel> threadsModelList, String order) {
    threadsModelList.sort((threadModel, t1) -> {
      if (threadModel.getPageNumber() == null || t1.getPageNumber() == null) {
        return 0;
      }
      if (order.equals("ascending")) {
        return Integer.parseInt(threadModel.getPageNumber())
                - Integer.parseInt(t1.getPageNumber());
      } else {
        return Integer.parseInt(t1.getPageNumber())
                - Integer.parseInt(threadModel.getPageNumber());
      }
    });
  }

  /** Filter threads by answered status. */
  private void filterThreadListByAnswerStatus(List<ThreadModel> threadsModelList,
                                              Boolean answered, ArrayList<FilterEnum> enumArray,
                                              StateLiveData<List<ThreadModel>> outFilteredThreads) {

    if (answered && enumArray.contains(FilterEnum.UNSOLVED)) {
      enumArray.remove(FilterEnum.UNSOLVED);
      if (outFilteredThreads.getValue() != null) {
        for (int i = 0; i < outFilteredThreads.getValue().getData().size(); i++) {
          if (outFilteredThreads.getValue().getData().get(i).getAnswered()) {
            threadsModelList.add(outFilteredThreads.getValue().getData().get(i));
          }
        }
      }
    } else if (!answered && enumArray.contains(FilterEnum.SOLVED)) {
      enumArray.remove(FilterEnum.SOLVED);
      if (outFilteredThreads.getValue() != null) {
        for (int i = 0; i < outFilteredThreads.getValue().getData().size(); i++) {
          if (!outFilteredThreads.getValue().getData().get(i).getAnswered()) {
            threadsModelList.add(outFilteredThreads.getValue().getData().get(i));
          }
        }
      }
    }

    List<ThreadModel> filteredList = new ArrayList<>();
    List<ThreadModel> outFilteredList = new ArrayList<>();

    for (int i = 0; i < threadsModelList.size(); i++) {
      if (threadsModelList.get(i).getAnswered() == answered) {
        filteredList.add(threadsModelList.get(i));
      } else {
        outFilteredList.add(threadsModelList.get(i));
      }
    }

    threadsModelList.clear();
    threadsModelList.addAll(filteredList);

    refillAndRemoveDuplicatesFromList(outFilteredThreads, outFilteredList);
  }

  /** Filter threads and just show threads created by the course provider. */
  private void filterThreadListByCourseProvider(
          List<ThreadModel> threadsModelList,
          MeetingsModel currentMeeting,
          StateLiveData<List<ThreadModel>> outFilteredThreads) {
    List<ThreadModel> filteredList = new ArrayList<>();
    List<ThreadModel> outFilteredList = new ArrayList<>();
    for (int i = 0; i < threadsModelList.size(); i++) {
      if (threadsModelList.get(i).getCreatorId().equals(currentMeeting.getCreatorId())) {
        filteredList.add(threadsModelList.get(i));
      } else {
        outFilteredList.add(threadsModelList.get(i));
      }
    }
    threadsModelList.clear();
    threadsModelList.addAll(filteredList);

    refillAndRemoveDuplicatesFromList(outFilteredThreads, outFilteredList);
  }

  /** Filter threads and just show threads created by the current user. */
  private void filterThreadListByOwnThreads(List<ThreadModel> threadsModelList,
                                            String userId,
                                            StateLiveData<List<ThreadModel>> outFilteredThreads) {

    List<ThreadModel> filteredList = new ArrayList<>();
    List<ThreadModel> outFilteredList = new ArrayList<>();
    for (int i = 0; i < threadsModelList.size(); i++) {
      if (threadsModelList.get(i).getCreatorId().equals(userId)) {
        filteredList.add(threadsModelList.get(i));
      } else {
        outFilteredList.add(threadsModelList.get(i));
      }
    }
    threadsModelList.clear();
    threadsModelList.addAll(filteredList);

    refillAndRemoveDuplicatesFromList(outFilteredThreads, outFilteredList);
  }

  /** Filter threads by tag. */
  private void filterThreadListByTag(List<ThreadModel> threadsModelList, TagEnum tagEnum,
                                     StateLiveData<List<ThreadModel>> outFilteredThreads) {
    List<ThreadModel> filteredList = new ArrayList<>();
    List<ThreadModel> outFilteredList = new ArrayList<>();
    for (int i = 0; i < threadsModelList.size(); i++) {
      List<TagEnum> usedTags = new ArrayList<>(threadsModelList.get(i).getTags());
      if (usedTags.contains(tagEnum)) {
        filteredList.add(threadsModelList.get(i));
      } else {
        outFilteredList.add(threadsModelList.get(i));
      }
    }
    threadsModelList.clear();
    threadsModelList.addAll(filteredList);

    refillAndRemoveDuplicatesFromList(outFilteredThreads, outFilteredList);
  }

  /** Filter threads by lecture period. */
  private void filterThreadListByLecturePeriod(
          List<ThreadModel> threadsModelList,
          MeetingsModel currentMeeting,
          StateLiveData<List<ThreadModel>> outFilteredThreads) {
    List<ThreadModel> filteredList = new ArrayList<>();
    List<ThreadModel> outFilteredList = new ArrayList<>();
    for (int i = 0; i < threadsModelList.size(); i++) {
      if (threadsModelList.get(i).getCreationTime() >= currentMeeting.getEventTime()
              && threadsModelList.get(i).getCreationTime() <= currentMeeting.getEventEndTime()) {
        filteredList.add(threadsModelList.get(i));
      } else {
        outFilteredList.add(threadsModelList.get(i));
      }
    }
    threadsModelList.clear();
    threadsModelList.addAll(filteredList);

    refillAndRemoveDuplicatesFromList(outFilteredThreads, outFilteredList);
  }

  /** Reset filter/show all threads. */
  private void resetThreadList(List<ThreadModel> threadsModelList,
                               StateLiveData<List<ThreadModel>> outFilteredThreads,
                               ArrayList<FilterEnum> enumArray, MeetingsModel currentMeeting,
                               String userId) {
    if (outFilteredThreads.getValue() != null) {
      Set<ThreadModel> threadsModelListAsSet = new HashSet<>(threadsModelList);
      threadsModelListAsSet.addAll(outFilteredThreads.getValue().getData());
      List<ThreadModel> fullThreadList = new ArrayList<>(threadsModelListAsSet);
      threadsModelList.clear();
      threadsModelList.addAll(fullThreadList);
    }
    if (enumArray.contains(FilterEnum.CREATOR)) {
      filterThreadListByCourseProvider(threadsModelList, currentMeeting, outFilteredThreads);
    } else if (enumArray.contains(FilterEnum.OWN)) {
      filterThreadListByOwnThreads(threadsModelList, userId, outFilteredThreads);
    } else if (enumArray.contains(FilterEnum.SOLVED)) {
      filterThreadListByAnswerStatus(threadsModelList, true, enumArray, outFilteredThreads);
    } else if (enumArray.contains(FilterEnum.UNSOLVED)) {
      filterThreadListByAnswerStatus(threadsModelList, false, enumArray,
              outFilteredThreads);
    } else if (enumArray.contains(FilterEnum.SUBJECT_MATTER)) {
      filterThreadListByTag(threadsModelList, TagEnum.SUBJECT_MATTER,  outFilteredThreads);
    } else if (enumArray.contains(FilterEnum.ORGANISATION)) {
      filterThreadListByTag(threadsModelList, TagEnum.ORGANISATION,  outFilteredThreads);
    } else if (enumArray.contains(FilterEnum.EXAMINATION)) {
      filterThreadListByTag(threadsModelList, TagEnum.EXAMINATION,  outFilteredThreads);
    } else if (enumArray.contains(FilterEnum.MISTAKE)) {
      filterThreadListByTag(threadsModelList, TagEnum.MISTAKE,  outFilteredThreads);
    } else if (enumArray.contains(FilterEnum.OTHER)) {
      filterThreadListByTag(threadsModelList, TagEnum.OTHER,  outFilteredThreads);
    } else if (enumArray.contains(FilterEnum.LECTURE_PERIOD)) {
      filterThreadListByLecturePeriod(threadsModelList, currentMeeting, outFilteredThreads);
    }
  }

  private void refreshList(List<ThreadModel> threadsModelList,
                           StateLiveData<List<ThreadModel>> outFilteredThreads) {
    Set<ThreadModel> threadsModelListAsSet = new HashSet<>(threadsModelList);
    List<ThreadModel> fullThreadList = new ArrayList<>(threadsModelListAsSet);
    threadsModelList.clear();
    threadsModelList.addAll(fullThreadList);
    if (outFilteredThreads.getValue() != null) {
      outFilteredThreads.getValue().getData().clear();
    }
  }

  private void refillAndRemoveDuplicatesFromList(
          StateLiveData<List<ThreadModel>> outFilteredThreads,
          List<ThreadModel> outFilteredList) {
    if (outFilteredThreads.getValue() != null) {
      Set<ThreadModel> threadsListAsSet = new HashSet<>(outFilteredThreads.getValue().getData());
      threadsListAsSet.addAll(outFilteredList);
      List<ThreadModel> fullList = new ArrayList<>(threadsListAsSet);
      outFilteredThreads.postUpdate(fullList);
    } else {
      outFilteredThreads.postUpdate(outFilteredList);
    }
  }

}
