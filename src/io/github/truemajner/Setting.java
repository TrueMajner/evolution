package io.github.truemajner;

public class Setting<T> {
    private final String name;
    private final T defaultValue;
    private T value;

    public Setting(String name, T value) {
        this.name = name;
        this.defaultValue = value;
        this.value = value;
    }

    public void resetValue() {
        this.value = this.defaultValue;
    }

    public T getValue() {
        return this.value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
