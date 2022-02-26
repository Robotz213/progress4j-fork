package com.github.signer4j.progress.imp;

import java.awt.Image;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.github.signer4j.progress.IProgressFactory;
import com.github.signer4j.progress.IProgressView;

import io.reactivex.disposables.Disposable;

public class ProgressFactory implements IProgressFactory {

  private Map<String, Entry> steps = Collections.synchronizedMap(new HashMap<>());
  
  private Image windowIcon;
  
  public ProgressFactory() {
    this(Images.PROGRESS_ICON.asImage());
  }
  
  public ProgressFactory(Image windowIcon) {
    this.windowIcon = windowIcon;
  }
  
  @Override
  public IProgressView get() {
    StepProgress sp =  new StepProgress(windowIcon);
    steps.put(sp.getName(), new Entry(sp, sp.disposeObservable().subscribe(p -> steps.remove(p.getName()).token.dispose())));
    return sp;
  }

  public void display() {
    synchronized(steps) {
      steps.values().forEach(e -> e.progress.display());
    }
  }
  
  public void undisplay() {
    synchronized(steps) {
      steps.values().forEach(e -> e.progress.undisplay());
    }
  }
  
  private static class Entry {
    public final IProgressView progress;
    public final Disposable token;
    
    private Entry(IProgressView progress, Disposable token) {
      this.progress = progress;
      this.token = token;
    }
  }
}
