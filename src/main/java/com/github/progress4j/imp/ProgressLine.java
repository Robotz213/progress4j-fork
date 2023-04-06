/* MIT License
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
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.github.utils4j.imp.NotImplementedException;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
class ProgressLine extends ProgressHandler<ProgressLine> {

  private JButton cancelButton = new JButton();
  
  private JPanel center = new JPanel();
  
  protected ProgressLine() {
    this(true);
  }
  
  protected ProgressLine(boolean showCancel) {
    setupComponents();
    setupLayout(showCancel);
  }

  @Override
  public final ProgressLine asContainer() {
    return this;
  }
  
  private final void setupLayout(boolean showCancel) {
    setLayout(new BorderLayout());
    add(north(showCancel), BorderLayout.NORTH);
    add(center(), BorderLayout.CENTER);
  }

  private void setupComponents() {
    setupCancel();
    setupProgress();
    setupLog();
  }

  private void setupLog() {
    center.setVisible(false);
  }

  private void setupCancel() {
    cancelButton.setIcon(Images.CANCEL.asIcon());
    cancelButton.setToolTipText("Cancelar esta operação.");
    cancelButton.addActionListener(this::onCancel);
  }

  private Component north(boolean showCancel) {
    JPanel north = new JPanel();
    north.setLayout(new MigLayout());
    north.add(progressBar, "pushx, growx");
    if (showCancel) {
      north.add(cancelButton);
    }
    return north;
  }
  
  private void setupProgress() {
    progressBar.setToolTipText("Clique para ver os detalhes.");
    progressBar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    progressBar.addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        detailStatus.onNext(!center.isVisible());
      }
    });
  }
  
  private JPanel center() {
    center.setLayout(new MigLayout());
    center.add(super.scrollPane, "pushx, pushy, growx, growy");
    return center;
  }
  
  @Override
  public final void showSteps(boolean visible) {
    invokeLater(() -> center.setVisible(visible));
  }
  
  @Override
  public final boolean isStepsVisible() {
    return center.isVisible();
  }

  @Override
  protected void setMode(Mode mode) {
    throw new NotImplementedException("This feature not implemented for " + getClass().getCanonicalName());
  }

  @Override
  protected Mode getMode() {
    throw new NotImplementedException("This feature not implemented for " + getClass().getCanonicalName());
  }
}
