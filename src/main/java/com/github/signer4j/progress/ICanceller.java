package com.github.signer4j.progress;

@FunctionalInterface
public interface ICanceller {
  ICanceller NOTHING = (r) -> {};
  
  void cancelCode(Runnable cancelCode);
}