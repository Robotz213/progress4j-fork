package com.github.progress4j;

public interface IStageEvent extends IEvent {
  
  boolean isEnd();
  
  boolean isStart();
}