package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.math.BlockLocation;
import com.github.alantr7.torus.math.Direction;
import com.github.alantr7.torus.math.IntArrayBuilder;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructureComponentDef;
import com.github.alantr7.torus.structure.builder.StructureConnectorDef;
import com.github.alantr7.torus.structure.component.Connector;
import com.github.alantr7.torus.structure.display.ItemDisplayModelTemplate;
import com.github.alantr7.torus.structure.display.Model;
import com.github.alantr7.torus.structure.display.ModelTemplate;
import org.bukkit.Material;
import org.bukkit.entity.ItemDisplay;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class SolarGenerator extends Structure {

    static ModelTemplate MODEL = new ModelTemplate();
    static {
        MODEL.add(new ItemDisplayModelTemplate(Material.GRAY_CONCRETE_POWDER, ItemDisplay.ItemDisplayTransform.GROUND, 0, new Vector3f(0, 0.125f, 0), new Vector3f(0.75f, 0.25f, 0.75f), 0f, 0f));
        MODEL.add(new ItemDisplayModelTemplate(Material.LIGHT_GRAY_CONCRETE, ItemDisplay.ItemDisplayTransform.GROUND, 0, new Vector3f(0, 1.0625f, 0), new Vector3f(0.125f, 1.625f, 0.125f), 0f, 0f));

        MODEL.add(new ItemDisplayModelTemplate(Material.BLACK_CONCRETE, ItemDisplay.ItemDisplayTransform.GROUND, 0, new Vector3f(0, 1.875f, 0), new Vector3f(2.4f, 0.062f, 2.4f), 0f, -20f));
        MODEL.add(new ItemDisplayModelTemplate(Material.LAPIS_BLOCK, ItemDisplay.ItemDisplayTransform.GROUND, 0, new Vector3f(0, 1.875f, 0), new Vector3f(2.25f, 0.0625f, 2.25f), 0f, -20f));

        System.out.println();
    }

    static ModelTemplate CONNECTOR_MODEL = new ModelTemplate();
    static {
        CONNECTOR_MODEL.add(new ItemDisplayModelTemplate(Material.GRAY_CONCRETE, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(0f, 0.5f, 0.4375f), new Vector3f(0.625f, 0.625f, 0.125f), 0f, 0f));
    }

    public SolarGenerator() {
        super("torus:solar_generator", SolarGeneratorInstance.class);
    }

    @Override
    public void createBounds(IntArrayBuilder builder) {
        builder.add(0, 0, 0);
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                builder.add(x, 1, z);
            }
        }
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction) {
        StructureComponentDef baseComponent = new StructureComponentDef(
          "base", new Vector3f(), MODEL.build(location.getBlock().getLocation().add(.5, 0, .5), direction)
        );

        Model connectorModel = CONNECTOR_MODEL.build(location.getBlock().getLocation().add(.5, 0, .5), direction);
        StructureComponentDef powerConnectorComponent = new StructureComponentDef(
          "power_connector", new Vector3f(), connectorModel
        );

        StructureConnectorDef connector = new StructureConnectorDef(
          "power_connector", Connector.Matter.ENERGY, Connector.FlowDirection.OUT, direction.getOpposite().mask()
        );

        StructureBodyDef bodyDef = new StructureBodyDef(new StructureComponentDef[]{ baseComponent, powerConnectorComponent }, new StructureConnectorDef[]{ connector });
        return new SolarGeneratorInstance(location, bodyDef, direction);
    }

}
