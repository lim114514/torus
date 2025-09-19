package com.github.alantr7.torus.structure;

import com.github.alantr7.torus.machine.*;

import java.util.HashMap;
import java.util.Map;

public class Structures {

    private static final Map<String, Structure> structures = new HashMap<>();

    public static final BlockBreaker BLOCK_BREAKER = register(new BlockBreaker());

    public static final EnergyCable ENERGY_CABLE = register(new EnergyCable());
    public static final ItemCable ITEM_CABLE = register(new ItemCable());
    public static final FluidPipe FLUID_CABLE = register(new FluidPipe());

    public static final InventoryInterface INVENTORY_INTERFACE = register(new InventoryInterface());

    public static final SolarGenerator SOLAR_GENERATOR = register(new SolarGenerator());

    private static <T extends Structure> T register(T t) {
        structures.put(t.getId(), t);
        return t;
    }

    public static Structure getStructureById(String id) {
        return structures.get(id);
    }

}
