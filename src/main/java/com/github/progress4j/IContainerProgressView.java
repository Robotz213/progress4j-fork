package com.github.progress4j;

import java.awt.Container;

public interface IContainerProgressView<T extends Container> extends IProgressView, IIsContainer<T> , IProgressViewHandler {
}
