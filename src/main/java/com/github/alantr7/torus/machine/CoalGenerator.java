package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.item.ItemCriteria;
import com.github.alantr7.torus.utils.ByteArrayBuilder;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructureComponentDef;
import com.github.alantr7.torus.structure.builder.StructureSocketDef;
import com.github.alantr7.torus.structure.socket.Socket;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Pitch;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class CoalGenerator extends Structure {

    public static int ENERGY_PRODUCTION = 300;

    public static int ENERGY_CAPACITY = 18_000;

    public static int ENERGY_MAXIMUM_OUTPUT = 500;

    public static final ItemCriteria INPUT_CRITERIA = new ItemCriteria();
    static {
        INPUT_CRITERIA.materials.add(Material.COAL);
        INPUT_CRITERIA.materials.add(Material.CHARCOAL);
    }

    public CoalGenerator() {
        super(TorusPlugin.DEFAULT_ADDON, "coal_generator", "Coal Generator", CoalGeneratorInstance.class);
        portableData.add("energy");
        hologramOffset = new float[] { 0f, 0f, 1f };
    }

    @Override
    public void createBounds(ByteArrayBuilder builder) {
        for (int z = 0; z <= 2; z++) {
            builder.add(0, 0, z);
        }
        builder.add(1, 0, 0);
        builder.add(1, 1, 0);
        builder.add(1, 2, 0);
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction, Pitch pitch) {
        return new CoalGeneratorInstance(location, new StructureBodyDef(new StructureComponentDef[]{
          new StructureComponentDef("base", new Vector3f()),
          new StructureComponentDef(
            "item_connector", new Vector3f(0, 0, 2), new StructureSocketDef(Socket.Medium.ITEM, Socket.FlowDirection.IN, direction.getOpposite().mask())
          ),
          new StructureComponentDef(
            "power_connector", new Vector3f(0, 0, 0), new StructureSocketDef(Socket.Medium.ENERGY, Socket.FlowDirection.OUT, direction.mask())
          )
        }), direction);
    }

    @Override
    protected void loadConfig() {
        super.loadConfig();
        ENERGY_PRODUCTION = config.getInt("energy_settings.production", ENERGY_PRODUCTION);
        ENERGY_CAPACITY = config.getInt("energy_settings.capacity", ENERGY_CAPACITY);
        ENERGY_MAXIMUM_OUTPUT = config.getInt("energy_settings.maximum_output", ENERGY_MAXIMUM_OUTPUT);
    }

}
