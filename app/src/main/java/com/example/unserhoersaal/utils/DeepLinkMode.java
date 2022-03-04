package com.example.unserhoersaal.utils;

import com.example.unserhoersaal.enums.DeepLinkEnum;

public class DeepLinkMode {

  private static DeepLinkMode instance;
  private DeepLinkEnum deepLinkEnum = DeepLinkEnum.DEFAULT;
  private String codeMapping = null;

  /** Gives back an Instance of Mode. */
  public static DeepLinkMode getInstance() {
    if (instance == null) {
      instance = new DeepLinkMode();
    }
    return instance;
  }

  public String getCodeMapping() {
    return this.codeMapping;
  }

  public void setCodeMapping(String codeMapping) {
    this.codeMapping = codeMapping;
  }

  public DeepLinkEnum getDeepLinkMode() {
    return this.deepLinkEnum;
  }

  public void setDeepLinkMode(DeepLinkEnum deepLinkEnum) {
    this.deepLinkEnum = deepLinkEnum;
  }

}