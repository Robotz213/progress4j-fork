package com.github.signer4j.progress;

public interface IEvent extends IState {
  String getMessage();

  int getStackSize();
  
  boolean isIndeterminated();
}
