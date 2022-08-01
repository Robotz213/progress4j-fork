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

import static com.github.utils4j.imp.Throwables.runQuietly;

import java.util.concurrent.atomic.AtomicInteger;

import com.github.progress4j.IProgressFactory;
import com.github.progress4j.IProgressView;

public class MultiThreadedProgressFactory implements IProgressFactory {  

  private StackProgressView stack;
  
  private final ThreadLocal<IProgressView> threadLocal;

  private final AtomicInteger stackSize = new AtomicInteger(0);

  public MultiThreadedProgressFactory() {
    this.threadLocal = new ThreadLocalProgressFactory();
  }
  
  @Override
  public IProgressView get() {
    return threadLocal.get(); 
  }
  
  private class ThreadLocalProgressFactory extends ThreadLocal<IProgressView> {
    
    private final ProgressLineFactory factory;
    private final StackProgressFactory context;
    
    private ThreadLocalProgressFactory() {
      this.factory = new DisposerProgressFactory();
      this.context = new StackProgressFactory();
    }
    
    @Override
    protected IProgressView initialValue() {
      synchronized(stackSize) {
        ProgressLineView newProgress = factory.get();
        if (stack == null) {
          stack = context.get();
          stack.setMode(Mode.HIDDEN);
          stack.display();
          runQuietly(() -> stack.begin("Processando em lote..."));
        } else {
          stack.setMode(Mode.BATCH);
        }
        stack.push(newProgress);
        stackSize.incrementAndGet();
        return newProgress;
      }
    }
  }
  
  private class DisposerProgressFactory extends ProgressLineFactory {
    
    DisposerProgressFactory() {
      super();
    }
    
    @Override
    protected void onDisposed(ProgressLineView pv) {
      synchronized(stackSize) {
        threadLocal.remove();
        int total = stackSize.decrementAndGet();
        runQuietly(() -> stack.info("Assinado pacote da %s", pv.getName()));
        try {
          stack.remove(pv);
        } finally {
          if (total == 0) {
            try {
              runQuietly(() -> stack.end());
              stack.undisplay();
            } finally {
              stack.dispose();
              stack = null;
            }
          }
        }
      }
    }      
  }
}