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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.progress4j.IProgressFactory;
import com.github.progress4j.IProgressView;
import com.github.utils4j.ICanceller;
import com.github.utils4j.IInterruptable;
import com.github.utils4j.imp.Args;

import io.reactivex.disposables.Disposable;

public class MultiThreadedProgressFactory implements IProgressFactory, ICanceller {  

  private StackProgressView stack;
  
  private Disposable stackTicketCancel;
  
  private final ThreadLocalProgressFactory threadLocal;

  private final AtomicInteger stackSize = new AtomicInteger(0);
  
  private final List<Runnable> codes = new ArrayList<>(2);

  public MultiThreadedProgressFactory() {
    this.threadLocal = new ThreadLocalProgressFactory();
  }
  
  @Override
  public final void cancelCode(Runnable code) {
    Args.requireNonNull(code, "code is null");
    codes.add(code);
  }
  
  @Override
  public boolean ifCanceller(Runnable code) {
    this.cancelCode(code);
    return true;
  }  
  
  @Override
  public void interrupt() {
    this.threadLocal.interrupt();
    this.codes.clear();
  }
  
  @Override
  public IProgressView get() {
    return threadLocal.get(); 
  }
  
  private class ThreadLocalProgressFactory extends ThreadLocal<IProgressView> implements IInterruptable {
    
    private final ProgressLineFactory lineFactory;
    private final StackProgressFactory stackFactory;
    
    private ThreadLocalProgressFactory() {
      this.stackFactory = new StackProgressFactory();
      this.lineFactory = new DisposerProgressFactory();
    }
    
    @Override
    protected IProgressView initialValue() {
      synchronized(stackSize) {
        ProgressLineView newLine = lineFactory.get();
        if (stack == null) {
          stack = stackFactory.get();
          stack.setMode(Mode.HIDDEN);
          stackTicketCancel = stack.cancelClick().subscribe(b -> codes.forEach(c -> runQuietly(c::run)));
          stack.display();
          stack.begin("Processando em lote...");
        } else {
          stack.setMode(Mode.BATCH);
        }
        stack.push(newLine);
        stackSize.incrementAndGet();
        return newLine;
      }
    }

    @Override
    public void interrupt() {
      if (stack != null) {
        stack.interrupt();
      }
      lineFactory.interrupt();
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
        Thread.interrupted();//clean interrupted state
        stack.info("Assinado pacote da %s", pv.getName());
        try {
          stack.remove(pv);
        } finally {
          pv.dispose();
          if (total == 0) {
            try {
              stack.end();
              stack.undisplay();
            } finally {
              stack.dispose();
              stack = null;
              stackTicketCancel.dispose();
              stackTicketCancel = null;
            }
          }
        }
      }
    }      
  }
}