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
import java.util.function.Supplier;

import com.github.progress4j.IProgressFactory;
import com.github.progress4j.IProgressView;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.Pair;

import io.reactivex.disposables.Disposable;

public class ProgressFactory<T extends IProgressView> implements IProgressFactory {  

  private final Map<String, Pair<T, Disposable>> pool = Collections.synchronizedMap(new HashMap<>());
  
  private final Supplier<T> creator;
  
  public ProgressFactory(Supplier<T> creator) {
    this(Images.PROGRESS_ICON.asImage(), creator);
  }
  
  public ProgressFactory(Image icon, Supplier<T> creator) {
    this.creator = Args.requireNonNull(creator, "supplier is null");
  }
  
  public final void display() {
    synchronized(pool) {
      pool.values().forEach(e -> e.getKey().display());
    }
  }
  
  public final void undisplay() {
    synchronized(pool) {
      pool.values().forEach(e -> e.getKey().undisplay());
    }
  }
  
  @Override
  public final void interrupt() {
    synchronized(pool) {
      pool.values().forEach(e -> e.getKey().interrupt());
    }
  } 
  
  @Override
  public final T get() {
    T pv =  creator.get();
    pool.put(pv.getName(), Pair.of(pv, pv.disposeObservable().subscribe(p -> {
      Pair<?, Disposable> item = pool.remove(p.getName());
      if (item != null) {
        item.getValue().dispose();
      }
      onDisposed(pv);      
    })));
    return pv; 
  }

  protected void onDisposed(T progress) {
    
  }
}
