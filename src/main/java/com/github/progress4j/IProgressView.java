package com.github.progress4j;

public interface IProgressView extends IProgress, ICanceller {

  void display();

  void undisplay(); 
  
  IProgressView reset();
}
