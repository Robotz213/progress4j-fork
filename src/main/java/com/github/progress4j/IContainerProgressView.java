package com.github.progress4j;

import java.awt.Container;

import io.reactivex.Observable;

public interface IContainerProgressView<T extends Container> extends IProgressView {
  
  void interrupt();
  
  T asContainer();
  
  Observable<Boolean> detailStatus();

  void showComponents(boolean visible);
}
