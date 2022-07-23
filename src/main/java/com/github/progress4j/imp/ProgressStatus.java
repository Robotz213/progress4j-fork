package com.github.progress4j.imp;

import java.io.File;

import com.github.progress4j.IProgress;
import com.github.progress4j.IStage;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.DownloadStatus;

public class ProgressStatus extends DownloadStatus {
  
  private long total;  
  
  private int increment = 1;  
  
  private final IStage stage;  
  
  private final IProgress progress;

  public ProgressStatus(IProgress progress, IStage stage) {
    this(progress, stage, null);
  }

  public ProgressStatus(IProgress progress, IStage stage, File saveHere) {
    super(false, saveHere);
    this.stage = Args.requireNonNull(stage, "stage is null");
    this.progress = Args.requireNonNull(progress, "progress is null");
  }  
  
  @Override
  protected void onStepStart(long total) throws InterruptedException {
    this.total = total;
    progress.begin(stage, 100);
  }
  
  @Override
  protected void onStepEnd() throws InterruptedException {
    progress.end();
  }
  
  @Override
  protected void onStepFail(Throwable e) throws InterruptedException {
    onStepEnd();
  }
  
  @Override
  protected void onStepStatus(long written) throws InterruptedException { 
    float percent = 100f * written / total;
    if (percent >= increment) {
      progress.step("Baixados %d%%", increment++);
    }
    if (increment <= percent) {  // <= or < ?
      long diff = (long)(percent - increment + 1);
      progress.skip(diff);
      increment += diff;
    }
  } 
}
