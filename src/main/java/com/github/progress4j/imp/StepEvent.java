package com.github.progress4j.imp;

import com.github.progress4j.IState;
import com.github.progress4j.IStepEvent;

class StepEvent extends Event implements IStepEvent {
  private boolean info;
  StepEvent(IState state, String message, int stackSize, boolean info) {
    super(state, message, stackSize);
    this.info = info;
  }
  
  @Override
  public boolean isInfo() {
    return info;
  }
}
