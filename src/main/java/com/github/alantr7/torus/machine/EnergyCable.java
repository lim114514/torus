package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.model.ModelTemplate;
import com.github.alantr7.torus.model.de_provider.DisplayEntitiesPartModelTemplate;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructureComponentDef;
import com.github.alantr7.torus.structure.component.Socket;
import com.github.alantr7.torus.model.de_provider.PartModelElementItemDisplayRenderer;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class EnergyCable extends Structure {

    static PartModelElementItemDisplayRenderer[] MODELS_ENERGY = {
      new PartModelElementItemDisplayRenderer(Material.GRAY_WOOL, new Vector3f(0, .5f, -0.25f), new Vector3f(0.2f, 0.2f, .5f), 0f, 0f),
      new PartModelElementItemDisplayRenderer(Material.GRAY_WOOL, new Vector3f(.25f, .5f, 0f), new Vector3f(.5f, 0.2f, 0.2f), 0f, 0f),
      new PartModelElementItemDisplayRenderer(Material.GRAY_WOOL, new Vector3f(0, .5f, .25f), new Vector3f(0.2f, 0.2f, .5f), 0f, 0f),
      new PartModelElementItemDisplayRenderer(Material.GRAY_WOOL, new Vector3f(-.25f, .5f, 0f), new Vector3f(.5f, 0.2f, 0.2f), 0f, 0f),
      new PartModelElementItemDisplayRenderer(Material.GRAY_WOOL, new Vector3f(0f, .75f, 0f), new Vector3f(.2f, 0.5f, 0.2f), 0f, 0f),
      new PartModelElementItemDisplayRenderer(Material.GRAY_WOOL, new Vector3f(0f, .25f, 0f), new Vector3f(.2f, 0.5f, 0.2f), 0f, 0f),
      new PartModelElementItemDisplayRenderer(Material.GRAY_WOOL, new Vector3f(0, .5f, 0), new Vector3f(.25f, .25f, .25f), 0f, 0f),
    };

    static PartModelElementItemDisplayRenderer[] MODELS_ITEM = {
      new PartModelElementItemDisplayRenderer(Material.LIGHT_GRAY_TERRACOTTA, new Vector3f(0, .5f, -0.25f), new Vector3f(0.2f, 0.2f, .5f), 0f, 0f),
      new PartModelElementItemDisplayRenderer(Material.LIGHT_GRAY_TERRACOTTA, new Vector3f(.25f, .5f, 0f), new Vector3f(.5f, 0.2f, 0.2f), 0f, 0f),
      new PartModelElementItemDisplayRenderer(Material.LIGHT_GRAY_TERRACOTTA, new Vector3f(0, .5f, .25f), new Vector3f(0.2f, 0.2f, .5f), 0f, 0f),
      new PartModelElementItemDisplayRenderer(Material.LIGHT_GRAY_TERRACOTTA, new Vector3f(-.25f, .5f, 0f), new Vector3f(.5f, 0.2f, 0.2f), 0f, 0f),
      new PartModelElementItemDisplayRenderer(Material.LIGHT_GRAY_TERRACOTTA, new Vector3f(0f, .75f, 0f), new Vector3f(.2f, 0.5f, 0.2f), 0f, 0f),
      new PartModelElementItemDisplayRenderer(Material.LIGHT_GRAY_TERRACOTTA, new Vector3f(0f, .25f, 0f), new Vector3f(.2f, 0.5f, 0.2f), 0f, 0f),
      new PartModelElementItemDisplayRenderer(Material.LIGHT_GRAY_TERRACOTTA, new Vector3f(0, .5f, 0), new Vector3f(.25f, .25f, .25f), 0f, 0f),
    };

    static PartModelElementItemDisplayRenderer[] MODELS_FLUID = {
      new PartModelElementItemDisplayRenderer(Material.LIGHT_BLUE_TERRACOTTA, new Vector3f(0, .5f, -0.25f), new Vector3f(0.2f, 0.2f, .5f), 0f, 0f),
      new PartModelElementItemDisplayRenderer(Material.LIGHT_BLUE_TERRACOTTA, new Vector3f(.25f, .5f, 0f), new Vector3f(.5f, 0.2f, 0.2f), 0f, 0f),
      new PartModelElementItemDisplayRenderer(Material.LIGHT_BLUE_TERRACOTTA, new Vector3f(0, .5f, .25f), new Vector3f(0.2f, 0.2f, .5f), 0f, 0f),
      new PartModelElementItemDisplayRenderer(Material.LIGHT_BLUE_TERRACOTTA, new Vector3f(-.25f, .5f, 0f), new Vector3f(.5f, 0.2f, 0.2f), 0f, 0f),
      new PartModelElementItemDisplayRenderer(Material.LIGHT_BLUE_TERRACOTTA, new Vector3f(0f, .75f, 0f), new Vector3f(.2f, 0.5f, 0.2f), 0f, 0f),
      new PartModelElementItemDisplayRenderer(Material.LIGHT_BLUE_TERRACOTTA, new Vector3f(0f, .25f, 0f), new Vector3f(.2f, 0.5f, 0.2f), 0f, 0f),
      new PartModelElementItemDisplayRenderer(Material.LIGHT_BLUE_TERRACOTTA, new Vector3f(0, .5f, 0), new Vector3f(.25f, .25f, .25f), 0f, 0f),
    };

    static PartModelElementItemDisplayRenderer[][] CABLE_MODELS = {
      MODELS_ITEM, MODELS_ENERGY, MODELS_FLUID
    };

    static ModelTemplate INITIAL_MODEL = new ModelTemplate(1);
    static {
        DisplayEntitiesPartModelTemplate part = new DisplayEntitiesPartModelTemplate("base");
        part.add(MODELS_ENERGY[0]);

        INITIAL_MODEL.add(part);
    }

    public EnergyCable() {
        super(TorusPlugin.DEFAULT_ADDON, "energy_cable", "Energy Cable", CableInstance.class);
        isHeavy = false;
    }

    @Override
    public ModelTemplate getModel() {
        return INITIAL_MODEL;
    }

    @Override
    public StructureInstance place(BlockLocation location, Direction direction) {
        CableInstance instance = (CableInstance) super.place(location, direction);
        instance.updateConnections();

        return instance;
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction) {
        StructureComponentDef base = new StructureComponentDef("base", new Vector3f());
        return new CableInstance(location, new StructureBodyDef(new StructureComponentDef[]{base}), Socket.Matter.ENERGY);
    }

}
