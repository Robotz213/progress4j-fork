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

import static com.github.utils4j.gui.imp.SwingTools.invokeLater;
import static com.github.utils4j.imp.Throwables.runQuietly;
import static javax.swing.SwingUtilities.invokeAndWait;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import com.github.progress4j.IContainerProgressView;
import com.github.utils4j.imp.Pair;

import io.reactivex.disposables.Disposable;

class StackProgressView extends ProgressFrameView {
  
  private final Map<String, Pair<IContainerProgressView<?>, Disposable>> tickets = new HashMap<>();
  
  protected StackProgressView() {
    super(new StackProgressFrame());
  }
  
  @Override
  public final void begin(String message) {
    runQuietly(() -> super.begin(message));
  }
  
  @Override
  public final void info(String message, Object... params) {
    runQuietly(() -> super.info(message, params));
  }
  
  @Override
  public final void end() {
    runQuietly(super::end);
  }
  
  final void cancel(Thread thread) {
    synchronized(tickets) {
      tickets.values().stream().map(Pair::getKey).filter(c -> c.isFrom(thread)).forEach(IContainerProgressView::cancel);
    }
  }
  
  final void push(IContainerProgressView<?> progress) throws InterruptedException {
    asContainer().add(progress.asContainer());
    Disposable ticketDetail = progress.detailStatus().subscribe((targetDetail) -> onDetail(progress));
    synchronized(tickets) {
      tickets.put(progress.getName(), Pair.of(progress, ticketDetail));
    }
    cancelCode(progress::interrupt);//this is very important!
  }

  final void remove(IContainerProgressView<?> pv) {
    asContainer().remove(pv.asContainer());
    synchronized(tickets) {
      tickets.remove(pv.getName()).getValue().dispose();
    }
  }
  
  final void setMode(Mode mode) {
    Mode previous = asContainer().getMode();
    asContainer().setMode(mode);
    if (previous != mode) {
      invokeLater(() -> {
        asContainer().applyDetail(false);
        if (Mode.HIDDEN == mode) {
          asContainer().pack();
        }
      });
    }
  }

  @Override
  protected void doDispose() {
    synchronized(tickets) {
      tickets.values().stream().map(Pair::getValue).forEach(Disposable::dispose);
      tickets.clear();
    }
    super.doDispose();
  }
  
  private void onDetail(IContainerProgressView<?> progress) {
    synchronized(tickets) {
      tickets.values().stream().map(Pair::getKey).filter(p -> p != progress).forEach(other -> other.showSteps(false));
      progress.showSteps(!progress.isStepsVisible());
      ((StackProgressFrame)asContainer()).repack();
    }
  }

  @SuppressWarnings("serial")
  static class StackProgressFrame extends ProgressFrame {
    
    private static final Dimension MININUM_SIZE = new Dimension(450, 144);
    
    StackProgressFrame() {
      super(new ProgressBox());
    }
    
    @Override
    protected void remove(Container container) {
      super.remove(container);
      repack(1);
    }
      
    @Override
    protected void add(Container container) {
      super.add(container);
      repack();
    }
    
    @Override
    protected void onRestore(WindowEvent e) {
      super.onRestore(e);
      applyDetail(false);
      repackSingle();
    }
    
    protected void repackSingle() {
      repack(getHandlerContainer().getComponentCount() == 1);
    }
    
    private void repack() {
      repack(false);
    }
    
    private void repack(boolean force) { 
      repack(2, force);
    }
    
    private void repack(int max) { 
      repack(max, false);
    }
    
    private void repack(int max, boolean force) { 
      invokeLater(() -> {
        if (!isMaximized()) {
          if (getHandlerContainer().getComponentCount() == max || !isDetailed() || force) {
            getHandlerContainer().revalidate();
            pack();
          }
        }
      });
    }

    @Override
    protected final void packDetail() {
      pack();
    }
    
    @Override
    protected void yesCancel() {
      super.cancel();
    }
    
    @Override
    public void cancelCode(Runnable cancelCode) throws InterruptedException {
      AtomicReference<InterruptedException> ex = new AtomicReference<InterruptedException>();
      try {
        invokeAndWait(() -> {
          try {
            super.cancelCode(cancelCode);
          } catch (InterruptedException e) {
            ex.set(e);
          }
        });
      } catch (Exception e) {
        super.cancelCode(cancelCode);
      }
      if (ex.get() != null) {
        throw ex.get();
      }
    }
    
    protected Dimension getDefaultMininumSize() {
      return MININUM_SIZE;
    }
  }
}