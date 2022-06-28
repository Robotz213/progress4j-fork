package com.github.progress4j.imp;

import com.github.progress4j.IStage;

public class Stage implements IStage {
  private String message;
  
  public Stage(String message) {
    this.message = message;
  }
  
  @Override
  public String toString() {
    return message;
  }
}
