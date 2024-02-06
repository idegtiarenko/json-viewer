package com.github.idegtiarenko.json;

import java.util.List;

public record Node(
        Type type,
        String name,
        int from,
        int to,
        String value,
        List<Node> children,
        int recursiveChildrenCount
) {

    public int size() {
        return to - from;
    }

    public int childrenCount() {
        return children.size();
    }

    public Node child(int i) {
        return children.get(i);
    }

    public static Node value(String name, int from, int to, String value) {
        return new Node(Type.VALUE, name, from, to, value, List.of(), 0);
    }

    public static Node array(String name, int from, int to, List<Node> children) {
        return new Node(Type.ARRAY, name, from, to, null, children, calculateRecursiveChildrenCount(children));
    }

    public static Node object(String name, int from, int to, List<Node> children) {
        return new Node(Type.OBJECT, name, from, to, null, children, calculateRecursiveChildrenCount(children));
    }

    private static int calculateRecursiveChildrenCount(List<Node> children) {
        return children.stream().mapToInt(Node::recursiveChildrenCount).sum();
    }

    public enum Type {
        VALUE, ARRAY, OBJECT
    }
}
