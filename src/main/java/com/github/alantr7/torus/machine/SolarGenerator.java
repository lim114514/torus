package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.math.ByteArrayBuilder;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructureComponentDef;
import com.github.alantr7.torus.structure.builder.StructureConnectorDef;
import com.github.alantr7.torus.structure.component.Connector;
import com.github.alantr7.torus.structure.display.ModelPartItemDisplayRenderer;
import com.github.alantr7.torus.structure.display.ModelTemplate;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class SolarGenerator extends Structure {

    static ModelTemplate MODEL = new ModelTemplate();
    static {
        MODEL.add(new ModelPartItemDisplayRenderer(Material.GRAY_CONCRETE_POWDER, new Vector3f(0, 0.125f, 0), new Vector3f(0.75f, 0.25f, 0.75f), 0f, 0f));
        MODEL.add(new ModelPartItemDisplayRenderer(Material.LIGHT_GRAY_CONCRETE, new Vector3f(0, 1.0625f, 0), new Vector3f(0.125f, 1.625f, 0.125f), 0f, 0f));

        MODEL.add(new ModelPartItemDisplayRenderer(Material.BLACK_CONCRETE, new Vector3f(0, 1.875f, 0), new Vector3f(2.4f, 0.062f, 2.4f), 0f, -20f));
        MODEL.add(new ModelPartItemDisplayRenderer(Material.LAPIS_BLOCK, new Vector3f(0, 1.875f, 0), new Vector3f(2.25f, 0.0625f, 2.25f), 0f, -20f));

        System.out.println();
    }

    static ModelTemplate CONNECTOR_MODEL = new ModelTemplate();
    static {
        CONNECTOR_MODEL.add(new ModelPartItemDisplayRenderer(Material.GRAY_CONCRETE, new Vector3f(0f, 0.5f, 0.4375f), new Vector3f(0.625f, 0.625f, 0.125f), 0f, 0f));
    }

    public SolarGenerator() {
        super("torus:solar_generator", SolarGeneratorInstance.class);
    }

    @Override
    public void createBounds(ByteArrayBuilder builder) {
        builder.add(0, 0, 0);
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                builder.add(x, 1, z);
            }
        }
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction) {
        return new SolarGeneratorInstance(location, new StructureBodyDef(new StructureComponentDef[]{
          new StructureComponentDef("base", new Vector3f(), MODEL),
          new StructureComponentDef("power_connector", new Vector3f(), CONNECTOR_MODEL, new StructureConnectorDef(
            Connector.Matter.ENERGY, Connector.FlowDirection.OUT, direction.getOpposite().mask()
          ))
        }), direction);
    }

}
