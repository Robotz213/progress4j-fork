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
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
class ProgressBox extends ProgressHandler<ProgressBox> {

  private static final String SHOW_DETAILS = "<html><u>Ver detalhes</u></html>";
  
  private static final String HIDE_DETAILS = "<html><u>Esconder detalhes</u></html>";
  
  private final JPanel southPane = new JPanel();

  private final JLabel detailsPane = new JLabel(SHOW_DETAILS);
  
  ProgressBox() {
    setupLayout();
  }
  
  private void setupLayout() {
    setLayout(new BorderLayout());
    add(north(), BorderLayout.NORTH);
    add(center(), BorderLayout.CENTER);
    add(south(), BorderLayout.SOUTH);
  }

  @Override
  public final ProgressBox asContainer() {
    return this;
  }
  
  private JScrollPane center() {
    scrollPane.setVisible(false);
    return scrollPane;
  }
  
  private JPanel south() {
    JButton cancelButton = new JButton("Cancelar");
    cancelButton.addActionListener(this::onCancel);
    JButton cleanButton = new JButton("Limpar");
    cleanButton.setPreferredSize(cancelButton.getPreferredSize());
    cleanButton.addActionListener(this::onClear);    
    southPane.setLayout(new MigLayout("fillx", "push[][]", "[][]"));
    southPane.add(cleanButton);
    southPane.add(cancelButton);
    southPane.setVisible(false);
    return southPane;
  }
  
  private JPanel north() {
    final JPanel north = new JPanel();
    north.setLayout(new GridLayout(3, 1, 0, 12));
    north.add(progressBar);
    north.add(detail());
    north.add(new JSeparator());
    return north;
  }

  private JLabel detail() {
    detailsPane.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    detailsPane.setHorizontalAlignment(SwingConstants.CENTER);
    detailsPane.setVerticalAlignment(SwingConstants.CENTER);
    detailsPane.setForeground(Color.BLUE);
    detailsPane.setFont(new Font("Tahoma", Font.ITALIC, 12));
    detailsPane.addMouseListener(new MouseAdapter(){  
      public void mouseClicked(MouseEvent e) {
        detailStatus.onNext(isShowDetail());
      }
    });
    return detailsPane;
  }
  
  final boolean isShowDetail() {
    return SHOW_DETAILS.equals(detailsPane.getText());
  }

  final boolean isHideDetail() {
    return !isShowDetail();
  }

  private Mode mode = Mode.NORMAL;
  
  @Override
  public final void showSteps(boolean visible) {
    invokeLater(() -> {
      detailsPane.setText(visible ? HIDE_DETAILS: SHOW_DETAILS);
      southPane.setVisible(visible && Mode.NORMAL.equals(mode));
      super.showSteps(visible && Mode.NORMAL.equals(mode));
    });
  }
  
  @Override
  protected void setMode(Mode mode) {
    invokeLater(() -> {
      setVisible(!Mode.HIDDEN.equals(mode));
      if (Mode.BATCH.equals(this.mode)) {
        this.mode = mode;
        return;
      }
      if (Mode.BATCH.equals(mode)) {
        if (isHideDetail()) {
          detailsPane.setText(SHOW_DETAILS);
        }
        southPane.setVisible(false);
        super.showSteps(false);
      } else if (Mode.NORMAL.equals(mode)) {
        ; //TODO we have to go back here
      }
      this.mode = mode;
    });
  }

  @Override
  protected Mode getMode() {
    return this.mode;
  }
}
