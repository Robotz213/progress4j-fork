package com.github.progress4j;

import java.awt.Container;

import io.reactivex.Observable;

public interface IContainerProgressView<T extends Container> extends IProgressView, IIsContainer<T> {
  
  Observable<Boolean> detailStatus();

  void showSteps(boolean visible);

  boolean isStepsVisible();
}
