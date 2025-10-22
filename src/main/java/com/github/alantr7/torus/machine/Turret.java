package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.item.ItemCriteria;
import com.github.alantr7.torus.math.ByteArrayBuilder;
import com.github.alantr7.torus.math.Direction;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructureComponentDef;
import com.github.alantr7.torus.structure.builder.StructureConnectorDef;
import com.github.alantr7.torus.structure.component.Connector;
import com.github.alantr7.torus.structure.display.Model;
import com.github.alantr7.torus.structure.display.ModelPartItemDisplayRenderer;
import com.github.alantr7.torus.structure.display.ModelTemplate;
import com.github.alantr7.torus.world.BlockLocation;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class Turret extends Structure {

    static ModelTemplate MODEL_HEAD = new ModelTemplate("head");
    static {
        MODEL_HEAD.add(new ModelPartItemDisplayRenderer(Material.GRAY_CONCRETE, 0f, 1.375f, 0f, .375f, .375f, .3125f));
        MODEL_HEAD.add(new ModelPartItemDisplayRenderer(Material.GRAY_CONCRETE, 0f, 1.375f, 0f, .25f, .25f, .9375f));
        MODEL_HEAD.add(new ModelPartItemDisplayRenderer(Material.GRAY_CONCRETE, 0f, 1.375f, -.125f - .5f, .125f, .125f, .9375f));
        MODEL_HEAD.add(new ModelPartItemDisplayRenderer(Material.GRAY_CONCRETE, 0f, 1.125f, 0f, .1875f, .1875f, .1875f));
        MODEL_HEAD.add(new ModelPartItemDisplayRenderer(Material.CYAN_TERRACOTTA, 0f, 1.578125f, 0f, .25f, .0625f, .1875f));
        MODEL_HEAD.add(new ModelPartItemDisplayRenderer(Material.CYAN_TERRACOTTA, .3125f - .5f, 1.375f, 0f, .0625f, .125f, .1875f));
        MODEL_HEAD.add(new ModelPartItemDisplayRenderer(Material.GREEN_TERRACOTTA, .25f, 1.1875f, 0f, .1875f, .375f, .1875f));
        MODEL_HEAD.add(new ModelPartItemDisplayRenderer(Material.CHAIN, .25f, .8125f, 0f, .3125f, .5625f, .375f, 90, 0));
    }

    static ModelTemplate MODEL_BASE = new ModelTemplate();
    static {
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.GRAY_CONCRETE, 0f, 1f, 0f, .3125f, .0625f, .3125f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.GRAY_CONCRETE, 0f, 1f, .125f, .1875f, .125f, .1875f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.CYAN_TERRACOTTA, 0f, .75f, 0f, .125f, 1.3125f, .125f));

        // Cables
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.GRAY_CONCRETE, .458125f - .5f, .5625f, .125f, .0625f, .875f, .0625f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.LIGHT_GRAY_TERRACOTTA, .04875f, .75f, .125f, .0625f, .4375f, .0625f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.LIGHT_GRAY_TERRACOTTA, .04875f, .5f, .25f, .0625f, .0625f, .3125f));

        // Power connector
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.GRAY_CONCRETE, 0f, .0625f, 0f, .75f, .125f, .75f));

        // Item connector
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.GRAY_CONCRETE, 0f, .5f, .4375f, .5f, .5f, .125f));
    }

    public static ItemCriteria AMMO_CRITERIA = new ItemCriteria();
    static { AMMO_CRITERIA.ids.add("torus:steel_nugget"); }

    public Turret() {
        super("torus:turret", TurretInstance.class);
        registerNamedModelTemplate(MODEL_HEAD);
    }

    @Override
    protected void createBounds(ByteArrayBuilder builder) {
        builder.add(0, 0, 0);
        builder.add(0, 1, 0);
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction) {
        return new TurretInstance(location, new StructureBodyDef(new StructureComponentDef[]{
          new StructureComponentDef("base", new Vector3f(), MODEL_BASE),
          new StructureComponentDef("head", new Vector3f(), MODEL_HEAD),
          new StructureComponentDef("in_item", new Vector3f(), (Model) null, new StructureConnectorDef(
            Connector.Matter.ITEM, Connector.FlowDirection.IN, direction.getOpposite().mask()
          )),
          new StructureComponentDef("in_energy", new Vector3f(), (Model) null, new StructureConnectorDef(
            Connector.Matter.ENERGY, Connector.FlowDirection.IN, Direction.DOWN.mask()
          ))
        }), direction);
    }
}
