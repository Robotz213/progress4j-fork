/*
* MIT License
* 
* Copyright (c) 2022 Leonardo de Lima Oliveira
* 
* https://github.com/l3onardo-oliv3ira
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/


package com.github.progress4j.imp;

import java.awt.Image;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.github.progress4j.IProgressFactory;
import com.github.progress4j.IProgressView;

import io.reactivex.disposables.Disposable;

public class ProgressFactory implements IProgressFactory {

  private Map<String, Entry> steps = Collections.synchronizedMap(new HashMap<>());
  
  private Image windowIcon;
  
  public ProgressFactory() {
    this(Images.PROGRESS_ICON.asImage().orElse(null));
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
