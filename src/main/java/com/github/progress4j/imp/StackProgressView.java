package com.github.progress4j.imp;

import static com.github.utils4j.gui.imp.SwingTools.invokeAndWait;
import static com.github.utils4j.gui.imp.SwingTools.invokeLater;
import static com.github.utils4j.imp.Throwables.runQuietly;

import java.awt.Container;
import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.SwingUtilities;

import com.github.progress4j.IContainerProgressView;
import com.github.utils4j.gui.imp.SwingTools;
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
  
  final void push(IContainerProgressView<?> progress) throws InterruptedException {
    asContainer().add(progress.asContainer());
    Disposable ticketDetail = progress.detailStatus().subscribe((targetDetail) -> onDetail(targetDetail, progress));
    synchronized(tickets) {
      tickets.put(progress.getName(), Pair.of(progress, ticketDetail));
    }
    cancelCode(progress::interrupt);
  }
  
  final void remove(IContainerProgressView<?> pv) {
    asContainer().remove(pv.asContainer());
    synchronized(tickets) {
      tickets.remove(pv.getName()).getValue().dispose();
    }
  }
  
  final void setMode(Mode mode) {
    asContainer().setMode(mode);
  }

  @Override
  protected void doDispose() {
    synchronized(tickets) {
      tickets.values().stream().map(Pair::getValue).forEach(Disposable::dispose);
      tickets.clear();
    }
    super.doDispose();
  }
  
  private void onDetail(Boolean targetDetail, IContainerProgressView<?> progress) {
    synchronized(tickets) {
      tickets.values().stream().map(Pair::getKey).filter(p -> p != progress).forEach(other -> {
        other.showSteps(false);
      });
      progress.showSteps(!progress.isStepsVisible());
      ((StackProgressFrame)asContainer()).repack();
    }
  }


  @SuppressWarnings("serial")
  static class StackProgressFrame extends ProgressFrame {
    
    private static final Dimension MININUM_SIZE = new Dimension(450, 144);
    
    StackProgressFrame() {
      super();
    }
    
    @Override
    protected void remove(Container container) {
      super.remove(container);
      repack();
    }
      
    @Override
    protected void add(Container container) {
      super.add(container);
      repack();
    }
    
    private void repack() {
      invokeLater(() -> {
        if (!isMaximized() && (getHandlerContainer().getComponentCount() == 2 || !isDetailed())) {
          getHandlerContainer().revalidate();
          pack();
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
        SwingUtilities.invokeAndWait(() -> {
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