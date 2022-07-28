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

import java.awt.Image;

import com.github.progress4j.IProgressFactory;
import com.github.progress4j.IProgressView;

public class ConcurrentProgressFactory implements IProgressFactory {  

  private final ThreadLocal<IProgressView> threadLocal;

  public ConcurrentProgressFactory() {
    this(Images.PROGRESS_ICON.asImage());
  }
  
  public ConcurrentProgressFactory(Image icon) {
    this.threadLocal = new FactoryThreadLocal(icon);
  }
  
  @Override
  public IProgressView get() {
    return threadLocal.get(); 
  }

  private class FactoryThreadLocal extends ThreadLocal<IProgressView> {
    
    private final IProgressFactory factory;
    
    private FactoryThreadLocal(Image icon) {
      this.factory = new ProgressFactoryDisposeNotifier(icon);
    }
    
    @Override
    protected IProgressView initialValue() {
      return factory.get();
    }
  }
  
  private class ProgressFactoryDisposeNotifier extends SimpleProgressFactory {
    
    ProgressFactoryDisposeNotifier(Image icon) {
      super(icon);
    }
    
    @Override
    protected void onDisposed(IProgressView pv) {
      threadLocal.remove();
      super.onDisposed(pv);
    }      
  }
}