package com.github.signer4j.progress.imp;

import com.github.signer4j.progress.IEvent;
import com.github.signer4j.progress.IState;

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
