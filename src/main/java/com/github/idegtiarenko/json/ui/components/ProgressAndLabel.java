package com.github.idegtiarenko.json.ui.components;

import java.util.Comparator;

public record ProgressAndLabel(int current, int total, String label) implements Comparable<ProgressAndLabel> {

    public double ratio() {
        return (double) current / total;
    }

    @Override
    public int compareTo(ProgressAndLabel other) {
        return Comparator.<ProgressAndLabel>comparingInt(it -> it.current).compare(this, other);
    }
}
