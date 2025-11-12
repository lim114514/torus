package com.github.alantr7.torus.structure;

import com.github.alantr7.torus.world.BlockLocation;

import java.util.Collection;

public interface Conductor {

    Collection<BlockLocation> getConnectedNodes();

}
