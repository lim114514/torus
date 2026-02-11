package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.item.ItemCriteria;
import com.github.alantr7.torus.structure.property.Property;
import com.github.alantr7.torus.structure.property.PropertyType;
import com.github.alantr7.torus.utils.ByteArrayBuilder;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructurePartDef;
import com.github.alantr7.torus.structure.builder.StructureSocketDef;
import com.github.alantr7.torus.structure.socket.Socket;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Pitch;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class CoalGenerator extends Structure {

    public static final ItemCriteria INPUT_CRITERIA = new ItemCriteria();
    static {
        INPUT_CRITERIA.materials.add(Material.COAL);
        INPUT_CRITERIA.materials.add(Material.CHARCOAL);
    }

    public CoalGenerator() {
        super(TorusPlugin.DEFAULT_ADDON, "coal_generator", "Coal Generator", CoalGeneratorInstance.class);
        portableData.add("energy");
        hologramOffset = new float[] { 0f, 0f, 1f };
        hologramTranslation = new float[] { 1.2f, 0, 0 };
        registerProperty(new Property<>("energy_settings.production", PropertyType.INT, 300));
        registerProperty(new Property<>("energy_settings.capacity", PropertyType.INT, 18_000));
        registerProperty(new Property<>("energy_settings.maximum_output", PropertyType.INT, 500));
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
        return new CoalGeneratorInstance(location, new StructureBodyDef(new StructurePartDef[]{
          new StructurePartDef("base", new Vector3f()),
          new StructurePartDef(
            "item_connector", new Vector3f(0, 0, 2), new StructureSocketDef(Socket.Medium.ITEM, Socket.FlowDirection.IN, direction.getOpposite().mask())
          ),
          new StructurePartDef(
            "power_connector", new Vector3f(0, 0, 0), new StructureSocketDef(Socket.Medium.ENERGY, Socket.FlowDirection.OUT, direction.mask())
          )
        }), direction);
    }

}
