package com.github.progress4j.imp;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.InputStream;

import javax.swing.ImageIcon;

import com.github.utils4j.imp.IPicture;

public enum Images implements IPicture {
  PROGRESS_ICON("/progress.png"),
  
  LOG("/log.png");

  final String path;
  
  Images(String path) {
    this.path = path;
  }
  
  @Override
  public InputStream asStream() {
    return getClass().getResourceAsStream(path);
  }
  
  @Override
  public Image asImage() {
    return Toolkit.getDefaultToolkit().createImage(getClass().getResource(path));
  }

  @Override
  public ImageIcon asIcon() {
    return new ImageIcon(getClass().getResource(path));
  }
}
