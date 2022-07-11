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
import com.github.utils4j.imp.Ids;
import com.github.utils4j.imp.Stack;
import com.github.utils4j.imp.Throwables;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

public class DefaultProgress implements IProgress {

  private boolean closed = false;

  private final Stack<State> stack = new Stack<State>();
  
  private BehaviorSubject<IStepEvent> stepSubject;
  
  private BehaviorSubject<IStageEvent> stageSubject;
  
  private BehaviorSubject<IProgress> disposeSubject;
  
  private final String name;
  
  public DefaultProgress() {
    this(Ids.next());
  }

  public DefaultProgress(String name) {
    this.name = Args.requireText(name, "name can't be null");
    this.resetObservables();
  }

  @Override
  public final String getName() {
    return name;
  }
  
  @Override
  public final void begin(IStage stage) throws InterruptedException {
    begin(stage, -1);
  }

  @Override
  public final void begin(IStage stage, int total) throws InterruptedException {
    checkInterrupted();
    closed = false;
    State state = new State(stack.isEmpty() ? null : stack.peek(), stage, total);
    notifyStage(state, stage.beginString(), false);
    stack.push(state);
  }

  @Override
  public final void step(String message, Object... args) throws InterruptedException {
    send(true, message, args);
  }
  
  @Override
  public final void skip(long steps) throws InterruptedException {
    checkInterrupted();
    State currentState;
    if (stack.isEmpty() || (currentState = stack.peek()).isAborted())
      return;
    currentState.incrementAndGet(steps);
    notifyStep(currentState, "Skip", false);
  }

  @Override
  public final void info(String message, Object... args) throws InterruptedException {
    send(false, message, args);
  }

  private void send(boolean advance, String message, Object... args) throws InterruptedException {
    checkInterrupted();
    State currentState;
    if (stack.isEmpty() || (currentState = stack.peek()).isAborted())
      return;
    if (advance) {
      currentState.incrementAndGet(1);
    }
    notifyStep(currentState, String.format(message, args), !advance);
  }
  
  @Override
  public final void end() throws InterruptedException {
    checkInterrupted();
    if (stack.isEmpty() || stack.peek().isAborted())
      return;
    State state = stack.pop();
    state.end();
    String message = state.getStage().endString() + " em " + state.getTime() + "ms";
    notifyStage(state, message, true);
  }
  
  private void checkInterrupted() throws InterruptedException {
    if (Thread.currentThread().isInterrupted()) {
      throw abort(new InterruptedException(CANCELED_OPERATION_MESSAGE));
    }
  }

  @Override
  public final <T extends Throwable> T abort(T e) {
    State currentState;
    if (stack.isEmpty() || (currentState = stack.peek()).isAborted())
      return e;
    String message = e.getMessage() + ". Causa: " + Throwables.rootTrace(e); 
    notifyStep(currentState.abort(e), message, true);
    message = currentState.getStage().endString() + " abortado em " + currentState.getTime() + "ms";
    notifyStage(currentState, message, true);
    return e;
  }
  
  @Override
  public final Throwable getAbortCause() {
    return stack.isEmpty() ? null : stack.peek().getAbortCause();
  }
  
  @Override
  public final boolean isClosed() {
    return closed;
  }
  
  @Override
  public final IProgress reset() {
    if (!isClosed()) {
      try {
        this.stack.clear();
        this.complete();
      }finally {
        this.resetObservables();
        this.closed = true;
      }
    }
    return this;
  }

  private void complete() {
    try {
      stepSubject.onComplete();
    }finally {
      try {
        stageSubject.onComplete();
      }finally {
        disposeSubject.onComplete();
      }
    }
  }
  
  private void notifyStep(IState state, String message, boolean info) {
    this.stepSubject.onNext(new StepEvent(state, message, this.stack.size(), info));
  }

  private void notifyStage(IState state, String message, boolean end) {
    this.stageSubject.onNext(new StageEvent(state, message, this.stack.size(), end));
  }
  
  private void resetObservables() {
    this.stepSubject = BehaviorSubject.create();
    this.stageSubject = BehaviorSubject.create();
    this.disposeSubject = BehaviorSubject.create();
  }
  
  @Override
  public final Observable<IStepEvent> stepObservable() {
    return this.stepSubject;
  }

  @Override
  public final Observable<IStageEvent> stageObservable() {
    return this.stageSubject;
  }

  @Override
  public final Observable<IProgress> disposeObservable() {
    return this.disposeSubject;
  }
  
  @Override
  public final IProgress stackTracer(Consumer<IState> consumer) {
    this.stack.forEach(consumer);
    return this;
  }

  @Override
  public final void dispose() {
    this.disposeSubject.onNext(this);
    this.reset();
  }
}
