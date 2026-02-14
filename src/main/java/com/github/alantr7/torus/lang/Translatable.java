package com.github.alantr7.torus.lang;

import static com.github.alantr7.torus.lang.Localization.translate;

public class Translatable {

    public final String key;

    public Translatable(String key) {
        this.key = key;
    }

    public final String get() {
        return translate(key);
    }

}
