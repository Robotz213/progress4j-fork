package com.github.progress4j;

import java.awt.Container;

public interface IContainerProgressView<T extends Container> extends IProgressView {
  
  void interrupt();
  
  T asContainer();
}
