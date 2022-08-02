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
import com.github.progress4j.IProgressView;
import com.github.progress4j.IStage;
import com.github.progress4j.IStageEvent;
import com.github.progress4j.IState;
import com.github.progress4j.IStepEvent;

import io.reactivex.Observable;

public enum ProgressIdle implements IProgressView {
  INSTANCE;
  
  private volatile boolean  interrupted = true;
  
  private ProgressIdle() {
  }

  @Override
  public final String getName() {
    return INSTANCE.name();
  }
  
  @Override
  public final void begin(String stage) throws InterruptedException {
    checkInterrupted();
  }

  @Override
  public final void begin(String stage, int total) throws InterruptedException {
    checkInterrupted();
  }
  
  @Override
  public final void begin(IStage stage) throws InterruptedException {
    checkInterrupted();
  }

  @Override
  public final void begin(IStage stage, int total) throws InterruptedException {
    checkInterrupted();
  }

  @Override
  public final void step(String message, Object... args) throws InterruptedException {
    checkInterrupted();
  }
  
  @Override
  public final void skip(long steps) throws InterruptedException {
    checkInterrupted();
  }

  @Override
  public final void info(String message, Object... args) throws InterruptedException {
    checkInterrupted();    
  }
  
  @Override
  public final void end() throws InterruptedException {
    checkInterrupted();
  }
  
  @Override
  public final void interrupt() {
    interrupted = true;
  }
  
  private void checkInterrupted() throws InterruptedException {
    if (interrupted) {
      Thread.currentThread().interrupt();
      interrupted = false;
    }
    if (Thread.currentThread().isInterrupted()) {
      throw abort(new InterruptedException("A thread foi interrompida!"));
    }
  }
  
  @Override
  public final <T extends Throwable> T abort(T exception) {
    return exception;
  }
  
  @Override
  public final Throwable getAbortCause() {
    return null;
  }
  
  @Override
  public final boolean isClosed() {
    return false;
  }
  
  @Override
  public final Observable<IStepEvent> stepObservable() {
    return null;
  }

  @Override
  public final Observable<IStageEvent> stageObservable() {
    return null;
  }

  @Override
  public final Observable<IProgress> disposeObservable() {
    return null;
  }
  
  @Override
  public final IProgress stackTracer(Consumer<IState> consumer) {
    return this;
  }

  @Override
  public final void dispose() {
    ;
  }

  @Override
  public void cancelCode(Runnable code) {
  }

  @Override
  public void display() {
  }

  @Override
  public void undisplay() {
  }

  @Override
  public IProgressView reset() {
    return this;
  }
}
