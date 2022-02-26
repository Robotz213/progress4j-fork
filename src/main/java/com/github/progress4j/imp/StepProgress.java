package com.github.progress4j.imp;

import java.awt.Image;

import com.github.progress4j.IProgressView;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.Ids;

import io.reactivex.disposables.Disposable;

class StepProgress extends ProgressWrapper implements IProgressView {

  private final ProgressWindow window;
  
  private Disposable stepToken, stageToken; 
  
  protected StepProgress(Image windowsIcon) {
    this(Ids.next("progress-"), windowsIcon);
  }
  
  protected StepProgress(String name, Image windowsIcon) {
    super(new DefaultProgress(name));
    this.window = new ProgressWindow(windowsIcon, Images.LOG.asIcon());
    this.attach();
  }

  @Override
  public void display() {
    this.window.reveal();
  }

  @Override
  public void undisplay() {
    this.window.unreveal();
  }
  
  @Override
  public void dispose() {
    super.dispose();
    this.window.exit();
  }
  
  @Override
  public final IProgressView reset() {
    super.reset();
    this.window.cancel();
    this.disposeTokens();
    this.attach();
    this.undisplay();
    return this;
  }
  
  private void disposeTokens() {
    stepToken.dispose();
    stageToken.dispose();
  } 

  private void attach() {
    cancelCode(() -> {}); //add current thread is very important!
    stepToken = progress.stepObservable().subscribe(e -> {
      this.window.stepToken(e);
    });
    stageToken = progress.stageObservable().subscribe(e -> {
      this.window.stageToken(e);
    });
  }

  @Override
  public void cancelCode(Runnable cancelCode) {
    Args.requireNonNull(cancelCode, "cancelCode is null");
    this.window.cancelCode(cancelCode);
  }
}
