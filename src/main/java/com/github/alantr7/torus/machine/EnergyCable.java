package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.math.BlockLocation;
import com.github.alantr7.torus.math.Direction;
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

public class EnergyCable extends Structure {

    static ItemDisplayModelTemplate[] MODELS_ENERGY = {
      new ItemDisplayModelTemplate(Material.GRAY_WOOL, ItemDisplay.ItemDisplayTransform.GROUND, 0, new Vector3f(0, .5f, -0.25f), new Vector3f(0.2f, 0.2f, .5f), 0f, 0f),
      new ItemDisplayModelTemplate(Material.GRAY_WOOL, ItemDisplay.ItemDisplayTransform.GROUND, 0, new Vector3f(.25f, .5f, 0f), new Vector3f(.5f, 0.2f, 0.2f), 0f, 0f),
      new ItemDisplayModelTemplate(Material.GRAY_WOOL, ItemDisplay.ItemDisplayTransform.GROUND, 0, new Vector3f(0, .5f, .25f), new Vector3f(0.2f, 0.2f, .5f), 0f, 0f),
      new ItemDisplayModelTemplate(Material.GRAY_WOOL, ItemDisplay.ItemDisplayTransform.GROUND, 0, new Vector3f(-.25f, .5f, 0f), new Vector3f(.5f, 0.2f, 0.2f), 0f, 0f),
      new ItemDisplayModelTemplate(Material.GRAY_WOOL, ItemDisplay.ItemDisplayTransform.GROUND, 0, new Vector3f(0f, .75f, 0f), new Vector3f(.2f, 0.5f, 0.2f), 0f, 0f),
      new ItemDisplayModelTemplate(Material.GRAY_WOOL, ItemDisplay.ItemDisplayTransform.GROUND, 0, new Vector3f(0f, .25f, 0f), new Vector3f(.2f, 0.5f, 0.2f), 0f, 0f),
      new ItemDisplayModelTemplate(Material.GRAY_WOOL, ItemDisplay.ItemDisplayTransform.GROUND, 0, new Vector3f(0, .5f, 0), new Vector3f(.25f, .25f, .25f), 0f, 0f),
    };

    static ItemDisplayModelTemplate[] MODELS_ITEM = {
      new ItemDisplayModelTemplate(Material.LIGHT_BLUE_TERRACOTTA, ItemDisplay.ItemDisplayTransform.GROUND, 0, new Vector3f(0, .5f, -0.25f), new Vector3f(0.2f, 0.2f, .5f), 0f, 0f),
      new ItemDisplayModelTemplate(Material.LIGHT_BLUE_TERRACOTTA, ItemDisplay.ItemDisplayTransform.GROUND, 0, new Vector3f(.25f, .5f, 0f), new Vector3f(.5f, 0.2f, 0.2f), 0f, 0f),
      new ItemDisplayModelTemplate(Material.LIGHT_BLUE_TERRACOTTA, ItemDisplay.ItemDisplayTransform.GROUND, 0, new Vector3f(0, .5f, .25f), new Vector3f(0.2f, 0.2f, .5f), 0f, 0f),
      new ItemDisplayModelTemplate(Material.LIGHT_BLUE_TERRACOTTA, ItemDisplay.ItemDisplayTransform.GROUND, 0, new Vector3f(-.25f, .5f, 0f), new Vector3f(.5f, 0.2f, 0.2f), 0f, 0f),
      new ItemDisplayModelTemplate(Material.LIGHT_BLUE_TERRACOTTA, ItemDisplay.ItemDisplayTransform.GROUND, 0, new Vector3f(0f, .75f, 0f), new Vector3f(.2f, 0.5f, 0.2f), 0f, 0f),
      new ItemDisplayModelTemplate(Material.LIGHT_BLUE_TERRACOTTA, ItemDisplay.ItemDisplayTransform.GROUND, 0, new Vector3f(0f, .25f, 0f), new Vector3f(.2f, 0.5f, 0.2f), 0f, 0f),
      new ItemDisplayModelTemplate(Material.LIGHT_BLUE_TERRACOTTA, ItemDisplay.ItemDisplayTransform.GROUND, 0, new Vector3f(0, .5f, 0), new Vector3f(.25f, .25f, .25f), 0f, 0f),
    };

    public EnergyCable() {
        super("torus:energy_cable", CableInstance.class);
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
