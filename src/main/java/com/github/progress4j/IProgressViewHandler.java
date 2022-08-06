package com.github.progress4j;

import io.reactivex.Observable;

public interface IProgressViewHandler {

  void showSteps(boolean visible);

  Observable<Boolean> detailStatus();

  Observable<Boolean> cancelClick();

  boolean isStepsVisible();

}