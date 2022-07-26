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


package com.github.progress4j;

import java.util.function.Consumer;

import com.github.utils4j.IDisposable;

import io.reactivex.Observable;

public interface IProgress extends IDisposable {

  String CANCELED_OPERATION_MESSAGE = "Operação cancelada!";
  
  String getName();
  
  void begin(String stage) throws InterruptedException;

  void begin(IStage stage) throws InterruptedException;
  
  void begin(String stage, int total) throws InterruptedException;

  void begin(IStage stage, int total) throws InterruptedException;
  
  void step(String mensagem, Object... params) throws InterruptedException;
  
  void skip(long steps) throws InterruptedException;

  void info(String mensagem, Object... params) throws InterruptedException;

  void end() throws InterruptedException;
  
  <T extends Throwable> T abort(T e);
  
  Throwable getAbortCause();
  
  boolean isClosed();
  
  IProgress stackTracer(Consumer<IState> consumer);
  
  IProgress reset();
  
  Observable<IStepEvent> stepObservable();

  Observable<IStageEvent> stageObservable();
  
  Observable<IProgress> disposeObservable();
}
