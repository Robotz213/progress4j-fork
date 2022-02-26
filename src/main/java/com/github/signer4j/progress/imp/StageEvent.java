package com.github.signer4j.progress.imp;

import com.github.signer4j.progress.IStageEvent;
import com.github.signer4j.progress.IState;

class StageEvent extends Event implements IStageEvent {

  private final boolean end;

  StageEvent(IState state, String message, int stackSize, boolean end) {
    super(state, message, stackSize);
    this.end = end;
  }

  @Override
  public final boolean isEnd() {
    return this.end;
  }
  
  @Override
  public final boolean isStart() {
    return !end;
  }
}
