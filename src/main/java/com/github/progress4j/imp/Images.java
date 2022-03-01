package com.github.progress4j.imp;

import com.github.utils4j.imp.IPicture;

public enum Images implements IPicture {
  PROGRESS_ICON("/progress.png"),
  
  LOG("/log.png");

  final String path;
  
  Images(String path) {
    this.path = path;
  }

  @Override
  public String path() {
    return path;
  }
}
