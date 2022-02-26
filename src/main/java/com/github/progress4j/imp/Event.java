package com.github.progress4j.imp;

import com.github.progress4j.IEvent;
import com.github.progress4j.IState;

abstract class Event extends StateWrapper implements IEvent {

  private final String message;

  private final int stackSize;

  protected Event(IState state, String message, int stackSize) {
    super(state);
    this.stackSize = stackSize;
    this.message = message;
  }
  

  @Override
  public final int getStackSize() {
    return this.stackSize;
  }
  
  @Override
  public final String getMessage() {
    return this.message;
  }
  
  @Override
  public final boolean isIndeterminated() {
    return this.getTotal() < 0;
  }
  
  @Override
  public final String toString() {
    return "[" + stackSize + "]" + this.getStage().toString();
  }

}
