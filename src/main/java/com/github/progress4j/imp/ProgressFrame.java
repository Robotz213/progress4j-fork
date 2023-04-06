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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import com.github.progress4j.IProgressHandler;
import com.github.progress4j.IStageEvent;
import com.github.progress4j.IStepEvent;
import com.github.utils4j.gui.imp.Dialogs;
import com.github.utils4j.gui.imp.SimpleFrame;
import com.github.utils4j.imp.Args;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

@SuppressWarnings("serial")
class ProgressFrame extends SimpleFrame implements IProgressHandler<ProgressFrame> {

  protected static final int MIN_DETAIL_HEIGHT = 312; 

  private static final Dimension MININUM_SIZE = new Dimension(450, 154);
  
  protected int currentHeight = getDefaultMinDetailHeight();

  protected final ProgressHandler<?> handler;

  private boolean maximized = false;
  
  private boolean detailed = true;
  
  private Disposable detailTicket;

  private JPanel center;
  
  ProgressFrame() {
    this(Images.PROGRESS_ICON.asImage());
  }
  
  ProgressFrame(ProgressHandler<?> handler) {
    this(Images.PROGRESS_ICON.asImage(), handler);
  }
  
  ProgressFrame(Image icon) {
    this(icon, new ProgressBox());
  }

  ProgressFrame(Image icon, ProgressHandler<?> handler) {
    super("Progresso", icon);    
    this.handler = Args.requireNonNull(handler, "panel is null");
    setupLayout();
    setup();
  }

  private void setupLayout() {
    JPanel owner = new JPanel();
    owner.setLayout(new BorderLayout(0, 0));
    owner.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
    owner.add(header(), BorderLayout.NORTH);
    owner.add(center(), BorderLayout.CENTER);
    setContentPane(owner);
  }
  
  final void setMode(Mode mode) {
    handler.setMode(mode);
  }
  
  final Mode getMode() {
    return handler.getMode();
  }
  
  protected Component center() {
    center = new JPanel();
    center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
    center.add(handler);
    return center;
  }
  
  public final boolean isMaximized() {
    return maximized;
  }
  
  protected int getDefaultMinDetailHeight() {
    return MIN_DETAIL_HEIGHT;
  }
  
  protected Dimension getDefaultMininumSize() {
    return MININUM_SIZE;
  }

  protected final JPanel getHandlerContainer() {
    return center;
  }
  
  @Override
  public final void stepToken(IStepEvent e) {
    this.handler.stepToken(e);
  }
  
  @Override
  public final void stageToken(IStageEvent e) {
    this.handler.stageToken(e);
  }

  @Override
  public final void cancel() {
    this.handler.cancel();
  }
  
  @Override
  public boolean isCanceled() {
    return handler.isCanceled();
  }
  
  @Override
  public void cancelCode(Runnable cancelCode) throws InterruptedException {
    handler.cancelCode(cancelCode);
  }
  
  @Override
  public Observable<Boolean> cancelClick() {
    return handler.cancelClick();
  }
  
  @Override
  public void bind(Thread thread) {
    this.handler.bind(thread);
  }
  
  @Override
  public boolean isFrom(Thread thread) {
    return handler.isFrom(thread);
  }
  
  @Override
  public ProgressFrame asContainer() {
    return this;
  }

  final void exit() {
    invokeLater(super::close);
  }

  final void unreveal() {
    invokeLater(() -> this.setVisible(false));
  }
  
  protected boolean isExpanded() {
    return getBounds().height > getDefaultMininumSize().height;
  }

  protected void add(Container container) {
    invokeLater(() -> {
      center.add(container);
      centerUpdate();
    });
  }

  protected void remove(Container container) {
    invokeLater(() -> {
      center.remove(container); 
      centerUpdate();
    });
  }
  
  private void centerUpdate() {
    center.revalidate();
    center.updateUI();
  }
  
  private JLabel header() {
    final JLabel headerLabel = new JLabel("Registro de atividades");
    headerLabel.setIcon(Images.LOG.asIcon());
    headerLabel.setHorizontalAlignment(SwingConstants.LEFT);
    headerLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
    return headerLabel;
  }

  final void reveal() {
    invokeLater(() -> { 
      this.toCenter();
      this.showToFront(); 
    });
  }
  
  private void setup() {
    setupListeners();
    setFixedMinimumSize(getDefaultMininumSize());
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    toCenter();
  }

  @Override
  public final void dispose() {
    ticketDispose();
    handler.dispose();
    super.dispose();
  }

  private void ticketDispose() {
    if (detailTicket != null) {
      detailTicket.dispose();
      detailTicket = null;
    }
  }
  
  @Override
  public Observable<Boolean> detailStatus() {
    return handler.detailStatus();
  }
  
  @Override
  public void showSteps(boolean visible) {
    this.handler.showSteps(visible);
  }
  
  @Override
  public boolean isStepsVisible() {
    return handler.isStepsVisible();
  }
  
  private void setupListeners() {
    detailTicket = detailStatus().subscribe(this::applyDetail);

    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent windowEvent) {        
        onEscPressed(null);
      }
    });
    addWindowStateListener(new WindowStateListener() {
      public void windowStateChanged(WindowEvent e) {
        if ((e.getNewState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH){
          onMaximized(e);
        } else if ((e.getNewState() & Frame.NORMAL) == Frame.NORMAL) {
          onRestore(e);
        }
      }
    });    
  }
  
  protected void onRestore(WindowEvent e) {
    this.handler.showSteps(isExpanded());
    maximized = false;
  }

  protected void onMaximized(WindowEvent e) {
    maximized = true;
    this.handler.showSteps(!maximized);
    applyDetail(maximized);
  }

  protected void expandTo(int height) {
    setBounds(getBounds().x, getBounds().y, getBounds().width, height);
  }

  protected void expandTo(Dimension dimension) {
    setBounds(getBounds().x, getBounds().y, dimension.width, dimension.height);
  }

  @Override
  protected void onEscPressed(ActionEvent e) {
    Dialogs.Choice choice = Dialogs.getBoolean(
      "Deseja mesmo cancelar a operação?",
      "Cancelamento da operação", 
      false
    );
    if (choice == Dialogs.Choice.YES) {
      yesCancel();
    }
  }

  protected void yesCancel() {
    this.cancel();
    this.unreveal();
  }
  
  protected final boolean isDetailed() {
    return detailed;
  }
  
  protected void packDetail() {
    expandTo(currentHeight);
  }
  
  protected void applyDetail(boolean toshow) {
    if (isMaximized()) {
      if (!toshow) {
        setExtendedState(JFrame.NORMAL);
        expandTo(getDefaultMininumSize());
      }
    } else if (toshow) {
      packDetail();
    } else {
      expandTo(getDefaultMininumSize().height);
    }
    showSteps(toshow);
    this.detailed = !toshow;
  }
  
  public static void main(String[] args) {
    invokeLater(() -> {
      ProgressFrame p = new ProgressFrame();
      p.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      p.reveal();
    });
  }
}


