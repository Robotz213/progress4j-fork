package com.github.progress4j;

public interface IQuietlyProgress extends IProgress {
  void begin(IStage stage);
  
  void begin(IStage stage, int total);
  
  void step(String mensagem, Object... params);

  void info(String mensagem, Object... params);
  
  void end();
}
