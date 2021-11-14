package com.github.idegtiarenko.json.ui.components;

import javafx.beans.value.ObservableValueBase;

public class MutableObservableValue<T> extends ObservableValueBase<T> {

    private T value = null;

    @Override
    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
        fireValueChangedEvent();
    }

    public void reset() {
        this.setValue(null);
    }
}
