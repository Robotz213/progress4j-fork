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
    this.disposeTokens();
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
    stepToken = progress.stepObservable().subscribe(window::stepToken); //link stepToken
    stageToken = progress.stageObservable().subscribe(window::stageToken); //link stageToken
  }

  @Override
  public void cancelCode(Runnable cancelCode) {
    this.window.cancelCode(cancelCode);
  }
}
