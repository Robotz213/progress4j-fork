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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.github.progress4j.IProgressFactory;
import com.github.progress4j.IProgressView;
import com.github.utils4j.imp.Pair;

import io.reactivex.disposables.Disposable;

public class SimpleProgressFactory implements IProgressFactory {  

  private final Map<String, Pair<IProgressView, Disposable>> pool = Collections.synchronizedMap(new HashMap<>());
  
  private Image icon;
  
  public SimpleProgressFactory() {
    this(Images.PROGRESS_ICON.asImage());
  }
  
  public SimpleProgressFactory(Image icon) {
    this.icon = icon;
  }
  
  public void display() {
    synchronized(pool) {
      pool.values().forEach(e -> e.getKey().display());
    }
  }
  
  public void undisplay() {
    synchronized(pool) {
      pool.values().forEach(e -> e.getKey().undisplay());
    }
  }
  
  @Override
  public final IProgressView get() {
    ProgressView pv =  new ProgressView(icon);
    pool.put(pv.getName(), Pair.of(pv, pv.disposeObservable().subscribe(p -> {
      pool.remove(p.getName()).getValue().dispose();
      onDisposed(pv);      
    })));
    return pv; 
  }

  protected void onDisposed(IProgressView progress) {
    
  }  
}
