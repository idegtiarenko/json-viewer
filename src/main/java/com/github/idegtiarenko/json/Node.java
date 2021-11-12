package com.github.idegtiarenko.json;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public abstract class Node {

    private final String name;
    private final int from;
    private final int to;

    public int getSize() {
        return to - from;
    }

    public abstract String getValue();

    public abstract List<Node> getChildren();

    public Node getChild(int i) {
        return getChildren().get(i);
    }

    public int getChildrenCount() {
        return getChildren().size();
    }

    public abstract int getRecursiveChildrenCount();
}

@EqualsAndHashCode(callSuper = true)
@ToString
final class ValueNode extends Node {
    private final String value;

    public ValueNode(String name, int from, int to, String value) {
        super(name, from, to);
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public List<Node> getChildren() {
        return List.of();
    }

    @Override
    public int getRecursiveChildrenCount() {
        return 0;
    }
}

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
abstract class ComplexNode extends Node {
    private final List<Node> children;
    private final int recursiveChildrenCount;

    public ComplexNode(String name, int from, int to, List<Node> children) {
        super(name, from, to);
        this.children = children;
        this.recursiveChildrenCount = calculateRecursiveChildrenCount(children);
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public List<Node> getChildren() {
        return children;
    }

    @Override
    public int getRecursiveChildrenCount() {
        return recursiveChildrenCount;
    }

    private static int calculateRecursiveChildrenCount(List<Node> children) {
        var size = 0;
        for (Node node : children) {
            size += 1 + node.getRecursiveChildrenCount();
        }
        return size;
    }
}

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
final class ObjectNode extends ComplexNode {

    public ObjectNode(String name, int from, int to, List<Node> children) {
        super(name, from, to, children);
    }
}

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
final class ArrayNode extends ComplexNode {

    public ArrayNode(String name, int from, int to, List<Node> children) {
        super(name, from, to, children);
    }
}
