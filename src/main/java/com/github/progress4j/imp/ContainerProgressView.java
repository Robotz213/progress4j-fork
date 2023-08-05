/* MIT License
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

import java.awt.Container;

import com.github.progress4j.IContainerProgressView;
import com.github.progress4j.IProgressView;
import com.github.progress4j.IStageEvent;
import com.github.progress4j.IStepEvent;
import com.github.utils4j.imp.Dates;
import com.github.utils4j.imp.Ids;

import io.reactivex.disposables.Disposable;

abstract class ContainerProgressView<T extends Container> extends ProgressWrapper implements IContainerProgressView<T> {

  private Disposable stepToken, stageToken, disposeToken; 
  
  protected ContainerProgressView() {
    this(Ids.next());
  }
  
  protected ContainerProgressView(String name) {
    super(new DefaultProgress("requisição: " + Dates.timeNow() + " id: " + name));
  }
  
  @Override
  public final void dispose() {
    super.dispose();
    disposeTokens();
    doDispose();
  }
  
  protected void doDispose() {
  }

  @Override
  public final IProgressView reset() {
    super.reset();
    this.interrupt();
    this.disposeTokens();
    this.bind();
    this.undisplay();
    return this;
  }
  
  private void disposeTokens() {
    if (stepToken != null)
      stepToken.dispose();
    
    if (stageToken != null)
      stageToken.dispose();
    
    if (disposeToken != null)
      disposeToken.dispose();
  } 

  protected final void bind() {
    bind(Thread.currentThread());  //add current thread is very important!    
    this.stepToken = stepObservable().subscribe(this::stepToken); //link stepToken
    this.stageToken = stageObservable().subscribe(this::stageToken); //link stageToken
    this.disposeToken = disposeObservable().subscribe((p) -> this.disposeTokens());
  }

  protected abstract void bind(Thread thread);
  
  protected abstract void stepToken(IStepEvent event);
  
  protected abstract void stageToken(IStageEvent event);
}
