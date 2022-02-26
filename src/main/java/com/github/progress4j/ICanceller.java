package com.github.progress4j;

@FunctionalInterface
public interface ICanceller {
  ICanceller NOTHING = (r) -> {};
  
  void cancelCode(Runnable cancelCode);
}