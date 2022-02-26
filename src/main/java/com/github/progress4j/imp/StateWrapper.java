package com.github.progress4j.imp;

import com.github.progress4j.IStage;
import com.github.progress4j.IState;

public class StateWrapper implements IState {

  private final IState state;
  
  public StateWrapper(IState state) {
    this.state = state;
  }
  
  @Override
  public int getStep() {
    return state.getStep();
  }

  @Override
  public int getTotal() {
    return state.getTotal();
  }

  @Override
  public IStage getStage() {
    return state.getStage();
  }

  @Override
  public long getTime() {
    return state.getTime();
  }

  @Override
  public String getStepTree() {
    return state.getStepTree();
  }

  @Override
  public boolean isAborted() {
    return state.isAborted();
  }

  @Override
  public Throwable getAbortCause() {
    return state.getAbortCause();
  }
}
