package com.github.alantr7.torus.world;

public enum Pitch {

    FORWARD(0), UP(90), DOWN(-90);

    public final int rotV;

    Pitch(int rotV) {
        this.rotV = rotV;
    }

}
