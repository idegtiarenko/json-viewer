package com.github.idegtiarenko.json;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AbbreviatedStringBuilderTest {

    @Test
    void shouldCreateString() {
        var builder = new AbbreviatedStringBuilder(10);
        for (int i = 0; i < 5; i++) {
            builder.append(Integer.toString(i));
        }
        assertThat(builder.toString()).isEqualTo("01234");
    }

    @Test
    void shouldCreateAbbreviatedFromChars() {
        var builder = new AbbreviatedStringBuilder(10);
        for (int i = 0; i < 1000; i++) {
            builder.append((char) ('0' + i % 10));
        }
        assertThat(builder.toString()).isEqualTo("0123456789");
    }

    @Test
    void shouldCreateAbbreviatedFromString() {
        var builder = new AbbreviatedStringBuilder(10);
        for (int i = 0; i < 1000; i++) {
            builder.append(Integer.toString(i % 10));
        }
        assertThat(builder.toString()).isEqualTo("0123456789");
    }

    @Test
    void shouldCreateAbbreviatedStringWhenAddingSingleHugeString() {
        var builder = new AbbreviatedStringBuilder(10);
        for (int i = 0; i < 5; i++) {
            builder.append(Integer.toString(i));
        }
        builder.append("0123456789");
        assertThat(builder.toString()).isEqualTo("0123401234");
    }


}