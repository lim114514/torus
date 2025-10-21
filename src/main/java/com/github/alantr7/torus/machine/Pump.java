package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.math.Direction;
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

public class Pump extends Structure {

    static ModelTemplate MODEL_BASE = new ModelTemplate();
    static {
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.END_ROD, new Vector3f(0, 0.5f, 0), new Vector3f(2f, 1f, 2f), 0f, 0f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.DROPPER, new Vector3f(0, 1.375f, 0), new Vector3f(.75f, .75f, .9375f), 0f, 90f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.CHAIN, new Vector3f(0.25f, 0.6875f, 0.1875f), new Vector3f(.5625f, .8125f,  1), 90f, 0f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.LIGHT_BLUE_TERRACOTTA, new Vector3f(0f, 1.875f, 0f), new Vector3f(.1875f, .1875f,  .1875f), 0f, 0f));
    }

    static ModelTemplate MODEL_FLUID_CONNECTOR = new ModelTemplate();
    static {
        MODEL_FLUID_CONNECTOR.add(new ModelPartItemDisplayRenderer(Material.GRAY_CONCRETE, new Vector3f(0, .9375f, 0), new Vector3f(.625f, .125f, .625f), 0f, 0f));
    }

    static ModelTemplate MODEL_ENERGY_CONNECTOR = new ModelTemplate();
    static {
        MODEL_ENERGY_CONNECTOR.add(new ModelPartItemDisplayRenderer(Material.GRAY_CONCRETE, new Vector3f(0, .5f, 0.4375f), new Vector3f(.625f, .625f, .125f), 0f, 0f));
    }

    public Pump() {
        super("torus:pump", PumpInstance.class);
    }

    @Override
    protected void createBounds(ByteArrayBuilder builder) {
        builder.add(0, 0, 0);
        builder.add(0, 1, 0);
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction) {
        return new PumpInstance(location, new StructureBodyDef(
          new StructureComponentDef[]{
            new StructureComponentDef("base", new Vector3f(), MODEL_BASE),
            new StructureComponentDef("power_connector", new Vector3f(0, 1, 0), MODEL_ENERGY_CONNECTOR.build(location.getBlock().getLocation().add(.5, 1, .5), direction), new StructureConnectorDef(
              Connector.Matter.ENERGY, Connector.FlowDirection.IN, direction.getOpposite().mask()
            )),
            new StructureComponentDef("fluid_connector", new Vector3f(0, 1, 0), MODEL_FLUID_CONNECTOR.build(location.getBlock().getLocation().add(.5, 1, .5), direction), new StructureConnectorDef(
              Connector.Matter.FLUID, Connector.FlowDirection.OUT, Direction.UP.mask()
            ))
          }
        ), direction);
    }

}
