package com.github.signer4j.progress.imp;

import java.awt.Image;

import javax.swing.ImageIcon;

import com.github.signer4j.progress.IProgressView;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.Ids;

import io.reactivex.disposables.Disposable;

class StepProgress extends ProgressWrapper implements IProgressView {

  private final ProgressWindow window;
  
  private Disposable stepToken, stageToken; 
  
  protected StepProgress(Image icon, ImageIcon log) {
    this(Ids.next("progress-"), icon, log);
  }
  
  protected StepProgress(String name, Image icon, ImageIcon log) {
    super(new DefaultProgress(name));
    this.window = new ProgressWindow(icon, log);
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
