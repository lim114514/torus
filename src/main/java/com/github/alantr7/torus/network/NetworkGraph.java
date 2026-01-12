package com.github.alantr7.torus.network;

import lombok.Getter;

import java.util.Collections;
import java.util.Set;

public class NetworkGraph {

    @Getter
    private boolean isInvalidated;

    public final int spawnTick;

    public Set<Node> nodes = Collections.emptySet();

    public static final NetworkGraph INIT = new NetworkGraph(-1, true);

    public NetworkGraph(int spawnTick, boolean isInvalidated) {
        this.spawnTick = spawnTick;
        this.isInvalidated = isInvalidated;
    }

    public void invalidate() {
        isInvalidated = true;
    }

}
