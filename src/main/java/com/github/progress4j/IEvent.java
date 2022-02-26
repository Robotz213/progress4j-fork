package com.github.progress4j;

public interface IEvent extends IState {
  String getMessage();

  int getStackSize();
  
  boolean isIndeterminated();
}
