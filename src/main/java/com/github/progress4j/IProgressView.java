package com.github.progress4j;

import com.github.utils4j.ICanceller;

public interface IProgressView extends IProgress, ICanceller {

  void display();

  void undisplay(); 
  
  IProgressView reset();
}
