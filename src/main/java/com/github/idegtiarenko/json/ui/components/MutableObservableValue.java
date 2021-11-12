package com.github.idegtiarenko.json.ui.components;

import javafx.beans.value.ObservableValueBase;

import java.util.Optional;

public class MutableObservableValue<T> extends ObservableValueBase<T> {

    private T value = null;

    @Override
    public T getValue() {
        return value;
    }

    public void setOptionalValue(Optional<T> value) {
        value.ifPresentOrElse(this::setValue, this::reset);
    }

    public void setValue(T value) {
        this.value = value;
        fireValueChangedEvent();
    }

    public void reset() {
        this.setValue(null);
    }
}
