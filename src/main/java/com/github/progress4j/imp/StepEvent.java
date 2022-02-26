package com.github.progress4j.imp;

import com.github.progress4j.IState;
import com.github.progress4j.IStepEvent;

class StepEvent extends Event implements IStepEvent {
  StepEvent(IState state, String message, int stackSize) {
    super(state, message, stackSize);
  }
}
