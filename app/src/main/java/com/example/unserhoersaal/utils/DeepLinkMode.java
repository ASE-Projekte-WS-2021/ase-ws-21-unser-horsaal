package com.example.unserhoersaal.utils;

import com.example.unserhoersaal.enums.DeepLinkEnum;

/** Singleton Class -- when the user opens the application with a deeplink. this class helps
 * to save the status while the user is logging in or registering with the app. */
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