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

import com.github.progress4j.IEvent;
import com.github.progress4j.IState;

abstract class Event extends StateWrapper implements IEvent {

  private final String message;

  private final int stackSize;

  protected Event(IState state, String message, int stackSize) {
    super(state);
    this.stackSize = stackSize;
    this.message = message;
  }
  

  @Override
  public final int getStackSize() {
    return this.stackSize;
  }
  
  @Override
  public final String getMessage() {
    return this.message;
  }
  
  @Override
  public final boolean isIndeterminated() {
    return this.getTotal() < 0;
  }
  
  @Override
  public final String toString() {
    return "[" + stackSize + "]" + this.getStage().toString();
  }

}
