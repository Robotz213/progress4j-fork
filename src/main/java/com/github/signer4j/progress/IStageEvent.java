package com.github.signer4j.progress;

public interface IStageEvent extends IEvent {
  
  boolean isEnd();
  
  boolean isStart();
}