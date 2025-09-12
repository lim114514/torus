package com.github.alantr7.torus.math;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class IntArrayBuilder {

    private final List<Integer> elements = new LinkedList<>();

    public void add(int... elements) {
        for (int el : elements)
            this.elements.add(el);
    }

    public int[] build() {
        int[] elements = new int[this.elements.size()];
        for (int i = 0; i < elements.length; i++) {
            elements[i] = this.elements.get(i);
        }
        return elements;
    }

}
