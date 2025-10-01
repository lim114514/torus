package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.math.Direction;
import com.github.alantr7.torus.math.IntArrayBuilder;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructureComponentDef;
import com.github.alantr7.torus.structure.builder.StructureConnectorDef;
import com.github.alantr7.torus.structure.component.Connector;
import com.github.alantr7.torus.structure.display.ItemDisplayModelTemplate;
import com.github.alantr7.torus.structure.display.ModelTemplate;
import org.bukkit.Material;
import org.bukkit.entity.ItemDisplay;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class Pump extends Structure {

    static ModelTemplate MODEL_BASE = new ModelTemplate();
    static {
        MODEL_BASE.add(new ItemDisplayModelTemplate(Material.END_ROD, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(0, 0.5f, 0), new Vector3f(2f, 1f, 2f), 0f, 0f));
        MODEL_BASE.add(new ItemDisplayModelTemplate(Material.DROPPER, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(0, 1.375f, 0), new Vector3f(.75f, .75f, .9375f), 0f, 90f));
        MODEL_BASE.add(new ItemDisplayModelTemplate(Material.CHAIN, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(0.25f, 0.6875f, 0.1875f), new Vector3f(.5625f, .8125f,  1), 90f, 0f));
        MODEL_BASE.add(new ItemDisplayModelTemplate(Material.LIGHT_BLUE_TERRACOTTA, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(0f, 1.875f, 0f), new Vector3f(.1875f, .1875f,  .1875f), 0f, 0f));
    }

    static ModelTemplate MODEL_FLUID_CONNECTOR = new ModelTemplate();
    static {
        MODEL_FLUID_CONNECTOR.add(new ItemDisplayModelTemplate(Material.GRAY_CONCRETE, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(0, .9375f, 0), new Vector3f(.625f, .125f, .625f), 0f, 0f));
    }

    static ModelTemplate MODEL_ENERGY_CONNECTOR = new ModelTemplate();
    static {
        MODEL_ENERGY_CONNECTOR.add(new ItemDisplayModelTemplate(Material.GRAY_CONCRETE, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(0, .5f, 0.4375f), new Vector3f(.625f, .625f, .125f), 0f, 0f));
    }

    public Pump() {
        super("torus:pump", PumpInstance.class);
    }

    @Override
    protected void createBounds(IntArrayBuilder builder) {
        builder.add(0, 0, 0);
        builder.add(0, 1, 0);
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction) {
        StructureComponentDef baseComponentDef = new StructureComponentDef("base", new Vector3f(), MODEL_BASE.build(location.getBlock().getLocation().add(.5, 0, .5), direction));
        StructureComponentDef fluidConnectorComponentDef = new StructureComponentDef("fluid_connector", new Vector3f(0, 1, 0), MODEL_FLUID_CONNECTOR.build(location.getBlock().getLocation().add(.5, 1, .5), direction));
        StructureComponentDef energyConnectorComponentDef = new StructureComponentDef("power_connector", new Vector3f(0, 1, 0), MODEL_ENERGY_CONNECTOR.build(location.getBlock().getLocation().add(.5, 1, .5), direction));

        StructureConnectorDef fluidConnector = new StructureConnectorDef("fluid_connector", Connector.Matter.FLUID, Connector.FlowDirection.OUT, Direction.UP.mask());
        StructureConnectorDef energyConnector = new StructureConnectorDef("power_connector", Connector.Matter.ENERGY, Connector.FlowDirection.IN, direction.getOpposite().mask());

        StructureBodyDef bodyDef = new StructureBodyDef(
          new StructureComponentDef[]{baseComponentDef, fluidConnectorComponentDef, energyConnectorComponentDef},
          new StructureConnectorDef[]{fluidConnector, energyConnector}
        );
        return new PumpInstance(location, bodyDef, direction);
    }

}
