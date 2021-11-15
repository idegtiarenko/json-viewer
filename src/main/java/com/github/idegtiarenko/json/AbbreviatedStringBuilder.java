package com.github.idegtiarenko.json;

public class AbbreviatedStringBuilder {

    private final StringBuilder sb = new StringBuilder();
    private final int limit;

    public AbbreviatedStringBuilder(int limit) {
        this.limit = limit;
    }

    public AbbreviatedStringBuilder append(char c) {
        if (getRemaining() > 0) {
            sb.append(c);
        }
        return this;
    }

    public AbbreviatedStringBuilder append(String str) {
        var remaining = getRemaining();
        if (remaining < str.length()) {
            sb.append(str.substring(0, remaining));
        } else {
            sb.append(str);
        }
        return this;
    }

    public AbbreviatedStringBuilder append(String... strs) {
        for (String str : strs) {
            append(str);
        }
        return this;
    }

    public boolean isLimitReached() {
        return sb.length() > limit;
    }

    public int getRemaining() {
        return limit - sb.length();
    }

    @Override
    public String toString() {
        return sb.toString();
    }
}
