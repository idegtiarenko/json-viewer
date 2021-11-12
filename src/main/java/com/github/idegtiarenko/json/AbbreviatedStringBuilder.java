package com.github.idegtiarenko.json;

public class AbbreviatedStringBuilder {

    private final StringBuilder sb = new StringBuilder();
    private final int limit;

    public AbbreviatedStringBuilder(int limit) {
        this.limit = limit;
    }

    public AbbreviatedStringBuilder append(char c) {
        if (!isLimitReached()) {
            sb.append(c);
        }
        return this;
    }

    public AbbreviatedStringBuilder append(String str) {
        if (!isLimitReached()) {
            sb.append(str);
        }
        return this;
    }

    public AbbreviatedStringBuilder append(Object... strs) {
        if (!isLimitReached()) {
            for (var str : strs) {
                sb.append(str);
            }
        }
        return this;
    }

    public boolean isLimitReached() {
        return sb.length() > limit;
    }

    @Override
    public String toString() {
        return sb.toString();
    }
}
