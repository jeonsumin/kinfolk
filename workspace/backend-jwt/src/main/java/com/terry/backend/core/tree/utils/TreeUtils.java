package com.terry.backend.core.tree.utils;

import com.terry.backend.core.tree.Tree;

import java.util.List;
import java.util.stream.Collectors;

public class TreeUtils {

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T extends Tree> List<T> make(T root, List<T> contents) {
        List<T> array = null;
        if (root == null) {
            array = contents.parallelStream().filter(x -> x.getParentId() == null)
                    .sorted((a, b) -> a.compareTo(b)).collect(Collectors.toList());
            final List<T> rd_contents = contents.parallelStream().filter(x -> x.getParentId() != null)
                    .collect(Collectors.toList());
            for (T node : array) {
                node.setChildren(TreeUtils.make(node, rd_contents));
            }
        } else {
            array = contents.parallelStream().filter(x -> root.getId().contentEquals(x.getParentId()))
                    .sorted((a, b) -> a.compareTo(b)).collect(Collectors.toList());
            final List<T> rd_contents = contents.parallelStream()
                    .filter(x -> !root.getId().contentEquals(x.getParentId())).collect(Collectors.toList());
            for (T node : array) {
                node.setChildren(TreeUtils.make(node, rd_contents));
            }
        }
        return array;
    }

}
