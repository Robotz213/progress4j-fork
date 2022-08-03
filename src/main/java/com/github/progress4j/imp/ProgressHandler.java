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
import static com.github.utils4j.imp.Strings.computeTabs;
import static com.github.utils4j.imp.Threads.startAsync;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.progress4j.IProgressHandler;
import com.github.progress4j.IStageEvent;
import com.github.progress4j.IStepEvent;
import com.github.utils4j.IDisposable;
import com.github.utils4j.gui.imp.Dialogs;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.Stack;
import com.github.utils4j.imp.Throwables;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

@SuppressWarnings("serial")
abstract class ProgressHandler<T extends ProgressHandler<T>> extends JPanel implements IProgressHandler<T>, IDisposable {

//  static {
//    UIManager.put("ProgressBarUI","javax.swing.plaf.metal.MetalProgressBarUI" );
//  }
  
  protected static final Logger LOGGER = LoggerFactory.getLogger(ProgressBox.class); 
  
  private final JTextArea textArea = new JTextArea();
  
  private final Stack<ProgressState> stackState = new Stack<>();

  private final Map<Thread, List<Runnable>> cancelCodes = new HashMap<>(2);  
  
  private long lineNumber = 0;
  
  protected final JScrollPane scrollPane = new JScrollPane();
  
  protected final JProgressBar progressBar = new JProgressBar();

  protected final BehaviorSubject<Boolean> detailStatus = BehaviorSubject.create();

  protected ProgressHandler() {
    setupLayout();
  }

  protected void onCancel(ActionEvent e) {
    Dialogs.Choice choice = Dialogs.getBoolean(
      "Deseja mesmo cancelar esta a operação?",
      "Cancelamento da operação", 
      false
    );
    if (choice == Dialogs.Choice.YES) {
      yesCancel();
    }
  }
  
  protected void yesCancel() {
    cancel();
  }
  
  private final void setupLayout() {
    setupScroll();
    resetProgress();
  }
  
  @Override
  public final Observable<Boolean> detailStatus() {
    return this.detailStatus;
  }

  private final void setupScroll() {
    textArea.setRows(8);
    textArea.setColumns(10);
    textArea.setEditable(false);
    textArea.getCaret().setDot(Integer.MAX_VALUE);
    scrollPane.setViewportView(textArea);
  }
  
  protected final void onClear(ActionEvent e) {
    textArea.setText("");
  }
  
  @Override
  public void showSteps(boolean visible) {
    scrollPane.setVisible(visible);  
  }
  
  @Override
  public boolean isStepsVisible() {
    return scrollPane.isVisible();
  }

  private void resetProgress() {
    this.textArea.setText("");
    this.progressBar.setIndeterminate(false);
    this.progressBar.setMaximum(0);
    this.progressBar.setMinimum(0);
    this.progressBar.setValue(-1);
    this.progressBar.setStringPainted(true);
    this.progressBar.setString("");
    this.stackState.clear();
  }
  
  @Override
  public void dispose() {
    resetProgress();
  }
  
  @Override
  public final void stepToken(IStepEvent e) {
    Args.requireNonNull(e, "step event is null");
    final int step = e.getStep();
    final int total = e.getTotal();
    final String message = e.getMessage();
    final boolean indeterminated = e.isIndeterminated();
    final StringBuilder text = computeTabs(e.getStackSize());
    final String log;
    if (indeterminated || e.isInfo()){
      log = text.append(message).toString();
    } else {
      log = text.append(format("Passo %s de %s: %s", step, total, message)).toString();
    }
    LOGGER.info(log);
    invokeLater(() -> {
      if (!indeterminated) {
        progressBar.setValue(step);
      }
      if (lineNumber++ > 800) {
        textArea.setText(""); //auto clean
        lineNumber = 0;
      }
      textArea.append(log + "\n\r");
    });
  }
  
  @Override
  public final void stageToken(IStageEvent e) {
    Args.requireNonNull(e, "stage event is null");
    final StringBuilder tabSize = computeTabs(e.getStackSize());
    final String message = e.getMessage();
    final String text = tabSize.append(message).toString();
    LOGGER.info(text);
    invokeLater(() -> {
      if (e.isStart())
        this.stackState.push(new ProgressState(this.progressBar));
      final boolean indeterminated = e.isIndeterminated();
      progressBar.setIndeterminate(indeterminated);
      if (!indeterminated) {
        progressBar.setMaximum(e.getTotal());
        progressBar.setMinimum(0);
        progressBar.setValue(e.getStep());
      }
      this.progressBar.setString(message);
      textArea.append(text + "\n\r");
      if (e.isEnd() && !this.stackState.isEmpty())
        this.stackState.pop().restore(this.progressBar);
    });    
  }

  @Override
  public final synchronized void cancel() {//It's very important to be synchronized
    final Map<Thread, List<Runnable>> copy = new HashMap<>(cancelCodes);
    cancelCodes.clear();
    Runnable interrupt = () -> {
      final List<Runnable> abort = copy.entrySet().stream()
        .peek(k -> {
          Thread thread = k.getKey();
          if (thread != Thread.currentThread()) //ignore EDT ever!
            thread.interrupt();
        })
        .map(Map.Entry::getValue)
        .flatMap(Collection::stream)
        .collect(toList());
      startAsync("canceling", () -> abort.forEach(cc -> Throwables.tryRun(cc::run)));
    };
    invokeLater(interrupt);
  }
  
  @Override
  public final void cancelCode(Runnable code) {
    Args.requireNonNull(code, "cancelCode is null");
    bind(Thread.currentThread(), code);
  }
  
  @Override
  public final void bind(Thread thread) {
    Args.requireNonNull(thread, "thread is null");
    bind(thread, () -> {});
  }

  private synchronized final void bind(Thread thread, Runnable code) {
    List<Runnable> codes = cancelCodes.get(thread);
    if (codes == null)
      cancelCodes.put(thread, codes = new ArrayList<>(2));
    codes.add(code);
  }
  
  private static class ProgressState {
    private final String message;
    private final int maximum, minimum, value;
    private final boolean indeterminated;
    
    private ProgressState(JProgressBar bar) {
      value = bar.getValue();
      message = bar.getString();
      maximum = bar.getMaximum();
      minimum = bar.getMinimum();
      indeterminated = bar.isIndeterminate();
    }
    
    private void restore(JProgressBar bar) {
      bar.setValue(value);
      bar.setString(message);
      bar.setMaximum(maximum);
      bar.setMinimum(minimum);
      bar.setIndeterminate(indeterminated);
    }
  }

  protected abstract void setMode(Mode mode);
}
