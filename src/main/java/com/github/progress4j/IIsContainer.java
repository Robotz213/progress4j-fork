package com.github.progress4j;

import java.awt.Container;

public interface IIsContainer<T extends Container> {

  T asContainer();

}