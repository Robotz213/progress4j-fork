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

public class ProgressWrapper implements IProgress {

  protected final IProgress progress;
  
  protected ProgressWrapper(IProgress progress) {
    this.progress = Args.requireNonNull(progress, "progress is null");
  }
  
  @Override
  public String getName() {
    return progress.getName();
  }

  @Override
  public void begin(String stage) throws InterruptedException {
    progress.begin(stage);
  }

  @Override
  public void begin(String stage, int total) throws InterruptedException {
    progress.begin(stage, total);
  }
  
  @Override
  public void begin(IStage stage) throws InterruptedException {
    progress.begin(stage);
  }

  @Override
  public void begin(IStage stage, int total) throws InterruptedException {
    progress.begin(stage, total);
  }

  @Override
  public void step(String mensagem, Object... params) throws InterruptedException{
    progress.step(mensagem, params);
  }
  
  @Override
  public void info(String mensagem, Object... params) throws InterruptedException {
    progress.info(mensagem, params);
  }

  @Override
  public void end() throws InterruptedException {
    progress.end();
  }

  @Override
  public <T extends Throwable> T abort(T e) {
    return progress.abort(e);
  }

  @Override
  public boolean isClosed() {
    return progress.isClosed();
  }

  @Override
  public IProgress stackTracer(Consumer<IState> consumer) {
    progress.stackTracer(consumer);
    return this;
  }

  @Override
  public IProgress reset() {
    progress.reset();
    return this;
  }

  @Override
  public Observable<IStepEvent> stepObservable() {
    return progress.stepObservable();
  }

  @Override
  public Observable<IStageEvent> stageObservable() {
    return progress.stageObservable();
  }
  
  @Override
  public Observable<IProgress> disposeObservable() {
    return progress.disposeObservable();
  }
  
  @Override
  public void dispose() {
    progress.dispose();
  }

  @Override
  public Throwable getAbortCause() {
    return progress.getAbortCause();
  }

  @Override
  public void skip(long steps) throws InterruptedException {
    progress.skip(steps);
  }
}
