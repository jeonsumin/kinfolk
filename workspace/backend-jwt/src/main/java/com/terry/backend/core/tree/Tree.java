package com.terry.backend.core.tree;

import java.util.List;

public interface Tree<T> extends Comparable<Tree<T>> {

    String getId();

    String getParentId();

    void setChildren(List<T> children);

    List<T> getChildren();

}
