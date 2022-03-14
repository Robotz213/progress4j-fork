package com.github.progress4j.imp;

import static com.github.utils4j.imp.Throwables.tryRun;

import com.github.progress4j.IProgress;
import com.github.progress4j.IQuietlyProgress;
import com.github.progress4j.IStage;

public class QuietlyProgress extends ProgressWrapper implements IQuietlyProgress {
  
  public static IQuietlyProgress wrap(IProgress progress) {
    return new QuietlyProgress(progress);
  }
  
  private QuietlyProgress(IProgress progress) {
    super(progress);
  }
  
  @Override
  public void begin(IStage stage) {
    tryRun(() -> progress.begin(stage));
  }

  @Override
  public void begin(IStage stage, int total) {
    tryRun(() -> progress.begin(stage, total));
  }

  @Override
  public void step(String mensagem, Object... params) {
    tryRun(() -> progress.step(mensagem, params));
  }
  
  @Override
  public void info(String mensagem, Object... params) {
    tryRun(() -> progress.info(mensagem, params));
  }
  
  @Override
  public void end() {
    tryRun(() -> progress.end());
  }
}
