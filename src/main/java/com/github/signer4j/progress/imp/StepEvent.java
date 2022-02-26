package com.github.signer4j.progress.imp;

import com.github.signer4j.progress.IState;
import com.github.signer4j.progress.IStepEvent;

class StepEvent extends Event implements IStepEvent {
  StepEvent(IState state, String message, int stackSize) {
    super(state, message, stackSize);
  }
}
