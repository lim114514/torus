package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.math.Direction;
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

public class EnergyCable extends Structure {

    static ModelPartItemDisplayRenderer[] MODELS_ENERGY = {
      new ModelPartItemDisplayRenderer(Material.GRAY_WOOL, new Vector3f(0, .5f, -0.25f), new Vector3f(0.2f, 0.2f, .5f), 0f, 0f),
      new ModelPartItemDisplayRenderer(Material.GRAY_WOOL, new Vector3f(.25f, .5f, 0f), new Vector3f(.5f, 0.2f, 0.2f), 0f, 0f),
      new ModelPartItemDisplayRenderer(Material.GRAY_WOOL, new Vector3f(0, .5f, .25f), new Vector3f(0.2f, 0.2f, .5f), 0f, 0f),
      new ModelPartItemDisplayRenderer(Material.GRAY_WOOL, new Vector3f(-.25f, .5f, 0f), new Vector3f(.5f, 0.2f, 0.2f), 0f, 0f),
      new ModelPartItemDisplayRenderer(Material.GRAY_WOOL, new Vector3f(0f, .75f, 0f), new Vector3f(.2f, 0.5f, 0.2f), 0f, 0f),
      new ModelPartItemDisplayRenderer(Material.GRAY_WOOL, new Vector3f(0f, .25f, 0f), new Vector3f(.2f, 0.5f, 0.2f), 0f, 0f),
      new ModelPartItemDisplayRenderer(Material.GRAY_WOOL, new Vector3f(0, .5f, 0), new Vector3f(.25f, .25f, .25f), 0f, 0f),
    };

    static ModelPartItemDisplayRenderer[] MODELS_ITEM = {
      new ModelPartItemDisplayRenderer(Material.LIGHT_GRAY_TERRACOTTA, new Vector3f(0, .5f, -0.25f), new Vector3f(0.2f, 0.2f, .5f), 0f, 0f),
      new ModelPartItemDisplayRenderer(Material.LIGHT_GRAY_TERRACOTTA, new Vector3f(.25f, .5f, 0f), new Vector3f(.5f, 0.2f, 0.2f), 0f, 0f),
      new ModelPartItemDisplayRenderer(Material.LIGHT_GRAY_TERRACOTTA, new Vector3f(0, .5f, .25f), new Vector3f(0.2f, 0.2f, .5f), 0f, 0f),
      new ModelPartItemDisplayRenderer(Material.LIGHT_GRAY_TERRACOTTA, new Vector3f(-.25f, .5f, 0f), new Vector3f(.5f, 0.2f, 0.2f), 0f, 0f),
      new ModelPartItemDisplayRenderer(Material.LIGHT_GRAY_TERRACOTTA, new Vector3f(0f, .75f, 0f), new Vector3f(.2f, 0.5f, 0.2f), 0f, 0f),
      new ModelPartItemDisplayRenderer(Material.LIGHT_GRAY_TERRACOTTA, new Vector3f(0f, .25f, 0f), new Vector3f(.2f, 0.5f, 0.2f), 0f, 0f),
      new ModelPartItemDisplayRenderer(Material.LIGHT_GRAY_TERRACOTTA, new Vector3f(0, .5f, 0), new Vector3f(.25f, .25f, .25f), 0f, 0f),
    };

    static ModelPartItemDisplayRenderer[] MODELS_FLUID = {
      new ModelPartItemDisplayRenderer(Material.LIGHT_BLUE_TERRACOTTA, new Vector3f(0, .5f, -0.25f), new Vector3f(0.2f, 0.2f, .5f), 0f, 0f),
      new ModelPartItemDisplayRenderer(Material.LIGHT_BLUE_TERRACOTTA, new Vector3f(.25f, .5f, 0f), new Vector3f(.5f, 0.2f, 0.2f), 0f, 0f),
      new ModelPartItemDisplayRenderer(Material.LIGHT_BLUE_TERRACOTTA, new Vector3f(0, .5f, .25f), new Vector3f(0.2f, 0.2f, .5f), 0f, 0f),
      new ModelPartItemDisplayRenderer(Material.LIGHT_BLUE_TERRACOTTA, new Vector3f(-.25f, .5f, 0f), new Vector3f(.5f, 0.2f, 0.2f), 0f, 0f),
      new ModelPartItemDisplayRenderer(Material.LIGHT_BLUE_TERRACOTTA, new Vector3f(0f, .75f, 0f), new Vector3f(.2f, 0.5f, 0.2f), 0f, 0f),
      new ModelPartItemDisplayRenderer(Material.LIGHT_BLUE_TERRACOTTA, new Vector3f(0f, .25f, 0f), new Vector3f(.2f, 0.5f, 0.2f), 0f, 0f),
      new ModelPartItemDisplayRenderer(Material.LIGHT_BLUE_TERRACOTTA, new Vector3f(0, .5f, 0), new Vector3f(.25f, .25f, .25f), 0f, 0f),
    };

    static ModelPartItemDisplayRenderer[][] CABLE_MODELS = {
      MODELS_ITEM, MODELS_ENERGY, MODELS_FLUID
    };

    public EnergyCable() {
        super("torus:energy_cable", CableInstance.class);
        isHeavy = false;
    }

    @Override
    public StructureInstance place(BlockLocation location, Direction direction) {
        CableInstance instance = (CableInstance) super.place(location, direction);
        instance.updateConnections();

        return instance;
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction) {
        ModelTemplate modelDisconnected = new ModelTemplate();
        modelDisconnected.add(MODELS_ENERGY[6]);

        StructureComponentDef base = new StructureComponentDef("base", new Vector3f(), modelDisconnected.build(location.getBlock().getLocation().add(.5, 0, .5), direction));
        return new CableInstance(location, new StructureBodyDef(new StructureComponentDef[]{base}, new StructureConnectorDef[0]), Connector.Matter.ENERGY);
    }

}
