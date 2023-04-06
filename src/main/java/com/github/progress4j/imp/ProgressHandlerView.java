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

import com.github.progress4j.IProgressHandler;
import com.github.progress4j.IStageEvent;
import com.github.progress4j.IStepEvent;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.Ids;

import io.reactivex.Observable;

abstract class ProgressHandlerView<T extends Container> extends ContainerProgressView<T> {

  private final IProgressHandler<T> handler;
  
  protected ProgressHandlerView(IProgressHandler<T> handler) {
    this(Ids.next(), handler);
  }
  
  protected ProgressHandlerView(String name, IProgressHandler<T> handler) {
    super(name);
    this.handler = Args.requireNonNull(handler, "handler is null");
    this.bind();
  }
  
  @Override
  public final T asContainer() {
    return handler.asContainer();
  }
  
  @Override
  public final Observable<Boolean> detailStatus() {
    return handler.detailStatus();
  }
  
  @Override
  public final void showSteps(boolean visible) {
    handler.showSteps(visible);
  }
  
  @Override
  public final boolean isStepsVisible() {
    return handler.isStepsVisible();
  }
  
  @Override
  public Observable<Boolean> cancelClick() {
    return handler.cancelClick();
  }
  
  @Override
  public boolean isCanceled() {
    return handler.isCanceled();
  }

  @Override
  protected void doDispose() {
    handler.dispose();
    super.doDispose();
  }
  
  @Override
  public final void interrupt() {
    handler.cancel();
  }
  
  @Override
  public final void cancelCode(Runnable cancelCode) throws InterruptedException {
    handler.cancelCode(cancelCode);
  }

  @Override
  protected final void stepToken(IStepEvent event) {
    handler.stepToken(event);
  }

  @Override
  protected final void stageToken(IStageEvent event) {
    handler.stageToken(event);
  }
  
  @Override
  protected void bind(Thread thread) {
    handler.bind(thread);
  }
  
  @Override
  public boolean isFrom(Thread thread) {
    return handler.isFrom(thread);
  }
  
  @Override
  public void cancel() {
    handler.cancel();
  }
}
