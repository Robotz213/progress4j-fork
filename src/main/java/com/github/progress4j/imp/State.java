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

import com.github.progress4j.IStage;
import com.github.progress4j.IState;
import com.github.utils4j.imp.StopWatch;
import com.github.utils4j.imp.Throwables;

class State implements IState {

  private int step;
  private int total;
  private final IState parent;
  private final IStage stage; 
  private Throwable abortCause = null;
  
  private final StopWatch stopWatch;

  State(IState parent, IStage stage, int total) {
    this(parent, stage, 0, total);
  }
  
  private State(IState parent, IStage stage, int step, int total) {
    this.parent = parent;
    this.stage = stage;
    this.step = step;
    this.total = total;
    this.stopWatch = new StopWatch();
    this.stopWatch.start();
  }
  
  @Override
  public final int getStep() {
    return step;
  }
  
  @Override
  public final String getStepTree() {
    return (parent != null ? parent.getStepTree() + "." : "") + getStep();
  }
  
  @Override
  public final Throwable getAbortCause() {
    return this.abortCause;
  }
  
  public final long incrementAndGet() { 
    if (isAborted())
      return -1; //lock increment if aborted!
    if(++step > total && total > 0) {
      stopWatch.getLogger().warn(
        "Stage {} tem mais passos que o total. Step: {}, Total: {} ", 
        new Object[]{
          this.stage, 
          this.step, 
          this.total
        }
      );
      total = step; //auto fix!
    }
    return step;
  }
  
  @Override
  public final int getTotal() {
    return total;
  }
  
  @Override
  public final IStage getStage() {
    return stage;
  }
  
  @Override
  public final long getTime() {
    return stopWatch.getTime();
  }
  
  @Override
  public final boolean isAborted() {
    return abortCause != null;
  }

  final IState end() {
    stopWatch.stop();
    step = total; //TODO e no caso de total for -1?
    return this;
  }

  final IState abort(Throwable e) {
    if (e != null) {
      this.stopWatch.stop((this.abortCause = e).getMessage());
    }
    return this;
  }
  
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("State [step=");
    builder.append(step);
    builder.append(", total=");
    builder.append(total);
    builder.append(", ");
    if (stage != null) {
      builder.append("stage=");
      builder.append(stage);
      builder.append(", ");
    }
    builder.append("time=");
    builder.append(getTime());
    builder.append(", ");
    builder.append("abort=");
    builder.append(isAborted());
    if (abortCause != null) {
      builder.append(", ");
      builder.append("abortCause:\n");
      builder.append(Throwables.rootTrace(abortCause));
    }
    builder.append("]");
    return builder.toString();
  }

}
