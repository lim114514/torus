package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.structure.StructureFlag;
import com.github.alantr7.torus.structure.property.Property;
import com.github.alantr7.torus.structure.property.PropertyType;
import com.github.alantr7.torus.utils.ByteArrayBuilder;
import com.github.alantr7.torus.model.ModelTemplate;
import com.github.alantr7.torus.model.de_provider.DisplayEntitiesPartModelTemplate;
import com.github.alantr7.torus.model.de_provider.PartModelElementItemDisplayRenderer;
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
import org.joml.Vector3i;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.github.alantr7.torus.lang.Localization.translatable;

public class Quarry extends Structure {

    public static final Set<Material> BLOCK_BLACKLIST = new HashSet<>();
    static {
        BLOCK_BLACKLIST.add(Material.BEDROCK);
    }

    static ModelTemplate MODEL_FEED = new ModelTemplate(1);
    static {
        DisplayEntitiesPartModelTemplate part = new DisplayEntitiesPartModelTemplate(
          "feed", new Vector3f(.5f, 0, .5f), 10, Collections.emptyMap()
        );
        part.add(new PartModelElementItemDisplayRenderer(
          Material.LIGHT_GRAY_TERRACOTTA,
          new Vector3f(0, 2.125f, 0),
          new Vector3f(0.1875f, 3.25f, 0.1875f),
          0, 0
        ));
        MODEL_FEED.add(part);
    }

    public Quarry() {
        super(TorusPlugin.DEFAULT_ADDON, "quarry", translatable("structure.quarry.name"), QuarryInstance.class);
        setFlags(StructureFlag.COLLIDABLE | StructureFlag.TICKABLE | StructureFlag.HEAVY);
        setPortableData("energy");
        setOffset(new Vector3i(0, 0, -6));
        setHologramOffset(new Vector3f(0, 0, -6f));
        registerProperty(new Property<>("energy_settings.consumption_on_mine", PropertyType.INT, 150));
        registerProperty(new Property<>("energy_settings.consumption_on_move", PropertyType.INT, 50));
        registerProperty(new Property<>("energy_settings.maximum_input", PropertyType.INT, 350));
        registerProperty(new Property<>("energy_settings.capacity", PropertyType.INT, 3000));
        registerProperty(new Property<>("special_settings.maximum_depth", PropertyType.INT, 64));
    }

    @Override
    protected void createBounds(ByteArrayBuilder builder) {
        int length = 5;
        for (int x = 0; x <= length; x++) {
            builder.add(x, 0, length);
            builder.add(x, 0, -length);
            builder.add(-x, 0, length);
            builder.add(-x, 0, -length);

            builder.add(length, 0, x);
            builder.add(-length, 0, x);
            builder.add(length, 0, -x);
            builder.add(-length, 0, -x);

            builder.add(x, 4, length);
            builder.add(x, 4, -length);
            builder.add(-x, 4, length);
            builder.add(-x, 4, -length);

            builder.add(length, 4, x);
            builder.add(-length, 4, x);
            builder.add(length, 4, -x);
            builder.add(-length, 4, -x);
        }

        for (int i = 1; i <= 3; i++) {
            builder.add(length, i, length);
            builder.add(length, i, -length);
            builder.add(-length, i, -length);
            builder.add(-length, i, length);
        }

        // Controller
        builder.add(0, 0, -length - 1);
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction, Pitch pitch) {
        return new QuarryInstance(this, location, new StructureBodyDef(
          new StructurePartDef[]{
            new StructurePartDef("base", new Vector3f()),
            new StructurePartDef("in_energy", new Vector3f(0, 0, -6), new StructureSocketDef(
              Socket.Medium.ENERGY, Socket.FlowDirection.IN, direction.getLeft().mask()
            )),
            new StructurePartDef("out_item", new Vector3f(0, 0, -6), new StructureSocketDef(
              Socket.Medium.ITEM, Socket.FlowDirection.OUT, Direction.UP.mask()
            )),
            new StructurePartDef("head", new Vector3f()),
            new StructurePartDef("feed", new Vector3f()),
            new StructurePartDef("drill_bit", new Vector3f()),
            new StructurePartDef("gantry_x", new Vector3f()),
            new StructurePartDef("gantry_z", new Vector3f()),
          }
        ), direction);
    }

}
