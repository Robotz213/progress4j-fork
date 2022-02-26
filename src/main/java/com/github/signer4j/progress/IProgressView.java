package com.github.signer4j.progress;

public interface IProgressView extends IProgress, ICanceller {

  void display();

  void undisplay(); 
  
  IProgressView reset();
}
