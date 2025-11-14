package com.github.alantr7.torus.structure;

import com.github.alantr7.torus.world.TorusWorld;

public class FlowMeter {

    public final TorusWorld world;

    private int supplied;

    private int consumed;

    public int lastUpdateTick;

    public FlowMeter(TorusWorld world) {
        this.world = world;
    }

    public void update(int value) {
        if (lastUpdateTick == world.getTicks()) {
            if (value >= 0) {
                supplied += value;
            } else {
                consumed -= value;
            }
        } else {
            lastUpdateTick = world.getTicks();
            if (value >= 0) {
                supplied = value;
                consumed = 0;
            } else {
                supplied = 0;
                consumed = -value;
            }
        }
    }

    public boolean isNeutral() {
        return getSupplied() - getConsumed() == 0;
    }

    public int getSupplied() {
        return (lastUpdateTick + 1) == world.getTicks() ? supplied : 0;
    }

    public int getConsumed() {
        return (lastUpdateTick + 1) == world.getTicks() ? consumed : 0;
    }

}
