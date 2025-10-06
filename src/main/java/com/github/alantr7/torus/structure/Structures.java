package com.github.alantr7.torus.structure;

import com.github.alantr7.torus.machine.*;

import java.util.LinkedHashMap;
import java.util.Map;

public class Structures {

    private static int nextStructureId = 2;
    private static final Map<String, Structure> structures = new LinkedHashMap<>();

    public static final BlockBreaker BLOCK_BREAKER = register(new BlockBreaker());
    public static final Pump PUMP = register(new Pump());
    public static final OreCrusher ORE_CRUSHER = register(new OreCrusher());
    public static final OreWasher ORE_WASHER = register(new OreWasher());

    public static final EnergyCable ENERGY_CABLE = register(new EnergyCable());
    public static final ItemCable ITEM_CABLE = register(new ItemCable());
    public static final FluidPipe FLUID_CABLE = register(new FluidPipe());

    public static final InventoryInterface INVENTORY_INTERFACE = register(new InventoryInterface());

    public static final FluidTank FLUID_TANK = register(new FluidTank());

    public static final CoalGenerator COAL_GENERATOR = register(new CoalGenerator());
    public static final SolarGenerator SOLAR_GENERATOR = register(new SolarGenerator());
    public static final PowerBank POWER_BANK = register(new PowerBank());

    private static <T extends Structure> T register(T t) {
        structures.put(t.id, t);
        t.numericId = nextStructureId++;
        return t;
    }

    public static Structure getStructureById(String id) {
        return structures.get(id);
    }

    public static Structure getStructureByNumericId(int id) {
        for (Structure structure : structures.values()) {
            if (structure.numericId == id)
                return structure;
        }
        return null;
    }

}
