package com.github.alantr7.torus.utils;

import java.util.LinkedList;
import java.util.List;

public class ByteArrayBuilder {

    private final List<Byte> elements = new LinkedList<>();

    public void add(int... elements) {
        for (int el : elements)
            this.elements.add((byte) el);
    }

    public byte[] build() {
        byte[] elements = new byte[this.elements.size()];
        for (int i = 0; i < elements.length; i++) {
            elements[i] = this.elements.get(i);
        }
        return elements;
    }

}
