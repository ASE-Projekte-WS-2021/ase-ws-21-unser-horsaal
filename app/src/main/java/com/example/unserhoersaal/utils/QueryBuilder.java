package com.example.unserhoersaal.utils;

import androidx.annotation.NonNull;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.TagEnum;
import java.util.List;

/** Used in CourseFragment and QuestionFragment to query all accessable values in a thread. */
public class QueryBuilder {

  public String userQuery;
  public String text;
  public String title;
  public String hashTag;
  public String pageNumber;
  public List<TagEnum> tags;
  public String institution;
  public String creatorName;
  public String description;

  public QueryBuilder addTitle(@NonNull String title) {
    this.title = title;
    return this;
  }

  public QueryBuilder addText(@NonNull String text) {
    this.text = text;
    return this;
  }

  public QueryBuilder addPageNumber(@NonNull String pageNumber) {
    this.pageNumber = pageNumber;
    return this;
  }

  public QueryBuilder addHashTag(@NonNull String hashTag) {
    this.hashTag = hashTag;
    return this;
  }

  public QueryBuilder addTags(@NonNull List<TagEnum> tags) {
    this.tags = tags;
    return this;
  }

  public QueryBuilder addInstitution(@NonNull String institution) {
    this.institution = institution;
    return this;
  }

  public QueryBuilder addCreatorName(@NonNull String creatorName) {
    this.creatorName = creatorName;
    return this;
  }

  public QueryBuilder addDescription(@NonNull String description) {
    this.description = description;
    return this;
  }

  /** compares user query with all properties that are not null and checks if they match. */
  public boolean matchUserQuery(@NonNull CharSequence userQuery) {
    this.userQuery = userQuery.toString().trim().toLowerCase();

    return (this.title != null && !this.title.equals("") && this.matchString(this.title))
            || (this.text != null && !this.text.equals("") && this.matchString(this.text))
            || (this.pageNumber != null
                && !this.pageNumber.equals("") && this.matchString(this.pageNumber))
            || (this.hashTag != null && !this.hashTag.equals("") && this.matchString(this.hashTag))
            || (this.institution != null
                && !this.institution.equals("") && this.matchString(this.institution))
            || (this.creatorName != null
                && !this.creatorName.equals("") && this.matchString(this.creatorName))
            || (this.description != null
                && !this.description.equals("") && this.matchString(this.description))
            || (this.tags != null && this.tags.size() != 0 && this.matchTagList());
  }

  private boolean matchString(String modelVar) {
    return modelVar.toLowerCase().contains(this.userQuery);
  }

  private boolean matchTagList() {
    for (TagEnum tag : this.tags) {
      if (tag == TagEnum.SUBJECT_MATTER && Config.TAG_SUBJECT_MATTER.contains(this.userQuery)) {
        return true;
      } else if (tag == TagEnum.EXAMINATION && Config.TAG_EXAMINATION.contains(this.userQuery)) {
        return true;
      } else if (tag == TagEnum.MISTAKE && Config.TAG_MISTAKE.contains(this.userQuery)) {
        return true;
      } else if (tag == TagEnum.ORGANISATION && Config.TAG_ORGANISATION.contains(this.userQuery)) {
        return true;
      } else if (tag == TagEnum.OTHER && Config.TAG_OTHER.contains(this.userQuery)) {
        return true;
      }
    }

    return false;
  }

}