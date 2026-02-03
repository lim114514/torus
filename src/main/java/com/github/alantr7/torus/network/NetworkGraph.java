package com.github.alantr7.torus.network;

import com.github.alantr7.torus.structure.StructureInstance;
import lombok.Getter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class NetworkGraph {

    static int nextId = 0;

    public final int id = ++nextId;

    @Getter
    private boolean isInvalidated;

    public final int spawnTick;

    public Set<Node> nodes = Collections.emptySet();

    public Set<StructureInstance> edges = new HashSet<>();

    public static final NetworkGraph INIT = new NetworkGraph(-1, true);

    public NetworkGraph(int spawnTick, boolean isInvalidated) {
        this.spawnTick = spawnTick;
        this.isInvalidated = isInvalidated;
    }

    public void invalidate() {
        isInvalidated = true;
    }

}
