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

import java.util.function.Consumer;

import com.github.progress4j.IProgress;
import com.github.progress4j.IStage;
import com.github.progress4j.IStageEvent;
import com.github.progress4j.IState;
import com.github.progress4j.IStepEvent;
import com.github.utils4j.imp.Args;

import io.reactivex.Observable;

public class SingleThreadProgress extends ProgressWrapper {
  
  public static IProgress wrap(IProgress progress) {
    return new SingleThreadProgress(progress);
  }
  
  private final Thread progressOwner;
  
  private SingleThreadProgress(IProgress progress) {
    this(progress, Thread.currentThread());
  }
  
  private SingleThreadProgress(IProgress progress, Thread progressOwner) {
    super(progress);
    this.progressOwner = Args.requireNonNull(progressOwner, "progressOwner is null");
  }
  
  protected final boolean isOwner() {
    return Thread.currentThread() == progressOwner;
  }
  
  private void checkOwner() {
    if (!isOwner()) {
      throw new IllegalStateException("Unabled to use this progress instance on this thread. Create your own");
    }
  }
  
  @Override
  public final void begin(IStage stage) throws InterruptedException {
    if (isOwner()) {
      progress.begin(stage);
    }
  }

  @Override
  public final void begin(IStage stage, int total) throws InterruptedException {
    if (isOwner()) {
      progress.begin(stage, total);
    }
  }

  @Override
  public final void step(String mensagem, Object... params) throws InterruptedException{
    if (isOwner()) {
      progress.step(mensagem, params);
    }
  }
  
  @Override
  public final void info(String mensagem, Object... params) throws InterruptedException {
    if (isOwner()) {
      progress.info(mensagem, params);
    }
  }

  @Override
  public final void end() throws InterruptedException {
    if (isOwner()) {
      progress.end();
    }
  }

  @Override
  public final <T extends Throwable> T abort(T e) {
    checkOwner();
    return progress.abort(e);
  }

  @Override
  public final boolean isClosed() {
    checkOwner();
    return progress.isClosed();
  }

  @Override
  public final IProgress stackTracer(Consumer<IState> consumer) {
    checkOwner();
    progress.stackTracer(consumer);
    return this;
  }

  @Override
  public final IProgress reset() {
    checkOwner();
    progress.reset();
    return this;
  }

  @Override
  public final Observable<IStepEvent> stepObservable() {
    checkOwner();
    return progress.stepObservable();
  }

  @Override
  public final Observable<IStageEvent> stageObservable() {
    checkOwner();
    return progress.stageObservable();
  }
  
  @Override
  public final Observable<IProgress> disposeObservable() {
    checkOwner();
    return progress.disposeObservable();
  }
  
  @Override
  public final void dispose() {
    checkOwner();
    progress.dispose();
  }

  @Override
  public final Throwable getAbortCause() {
    checkOwner();
    return progress.getAbortCause();
  }
  
}
