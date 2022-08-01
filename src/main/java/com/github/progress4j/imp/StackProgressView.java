package com.github.progress4j.imp;

import static com.github.utils4j.gui.imp.SwingTools.invokeAndWait;
import static com.github.utils4j.gui.imp.SwingTools.invokeLater;

import java.awt.Container;
import java.awt.Dimension;

import com.github.progress4j.IContainerProgressView;

class StackProgressView extends ProgressFrameView {
  
  protected StackProgressView() {
    super(new StackProgressFrame());
  }
  
  final void push(IContainerProgressView<?> progress) {
    asContainer().add(progress.asContainer());
    cancelCode(progress::interrupt);
  }
  
  final void remove(ProgressHandlerView<?> pv) {
    asContainer().remove(pv.asContainer());
  }
  
  final void setMode(Mode mode) {
    asContainer().setMode(mode);
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
        if (!isDetailed() && !isMaximized()) {
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
    public void cancelCode(Runnable cancelCode) {
      invokeAndWait(() -> super.cancelCode(cancelCode));
    }
    
    protected Dimension getDefaultMininumSize() {
      return MININUM_SIZE;
    }
  }
}