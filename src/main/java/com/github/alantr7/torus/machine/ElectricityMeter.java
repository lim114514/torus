package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.model.ModelTemplate;
import com.github.alantr7.torus.model.PartModelElementItemDisplayRenderer;
import com.github.alantr7.torus.model.PartModelTemplate;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructureComponentDef;
import com.github.alantr7.torus.structure.builder.StructureConnectorDef;
import com.github.alantr7.torus.structure.component.Connector;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class ElectricityMeter extends Structure {

    public static final PartModelTemplate BASE_MODEL = new PartModelTemplate("base");
    static {
        BASE_MODEL.add(new PartModelElementItemDisplayRenderer(Material.LIGHT_GRAY_CONCRETE, new Vector3f(0, .5f, .0625f), new Vector3f(.875f, .875f, .375f), new Vector3f()));
        BASE_MODEL.add(new PartModelElementItemDisplayRenderer(Material.LIGHT_GRAY_CONCRETE, new Vector3f(.3125f, .5f, .3125f - .5f), new Vector3f(.125f, .875f, .125f), new Vector3f()));
        BASE_MODEL.add(new PartModelElementItemDisplayRenderer(Material.LIGHT_GRAY_CONCRETE, new Vector3f(.1875f - .5f, .5f, .3125f - .5f), new Vector3f(.125f, .875f, .125f), new Vector3f()));
        BASE_MODEL.add(new PartModelElementItemDisplayRenderer(Material.LIGHT_GRAY_CONCRETE, new Vector3f(0, .875f, .3125f - .5f), new Vector3f(.5f, .125f, .125f), new Vector3f()));
        BASE_MODEL.add(new PartModelElementItemDisplayRenderer(Material.LIGHT_GRAY_CONCRETE, new Vector3f(0, .25f, .3125f - .5f), new Vector3f(.5f, .375f, .125f), new Vector3f()));
        BASE_MODEL.add(new PartModelElementItemDisplayRenderer(Material.CREEPER_BANNER_PATTERN, new Vector3f(0, .25f, -.25f), new Vector3f(.375f, .375f, 1f), new Vector3f()));
        BASE_MODEL.add(new PartModelElementItemDisplayRenderer(Material.GLASS, new Vector3f(0, .625f, -.25f), new Vector3f(.5f, .375f, .0011f), new Vector3f()));
        BASE_MODEL.add(new PartModelElementItemDisplayRenderer(Material.WHITE_CONCRETE, new Vector3f(0, .5f, .366875f - .5f), new Vector3f(.5625f, .6875f, .125f), new Vector3f()));
        BASE_MODEL.add(new PartModelElementItemDisplayRenderer(Material.GRAY_CONCRETE, new Vector3f(.1f, .71875f, .415f - .5f), new Vector3f(.1875f, .0415f, .25f), new Vector3f()));
        BASE_MODEL.add(new PartModelElementItemDisplayRenderer(Material.GRAY_CONCRETE, new Vector3f(.1f, .65875f, .415f - .5f), new Vector3f(.1875f, .0415f, .25f), new Vector3f()));

        BASE_MODEL.add(new PartModelElementItemDisplayRenderer(Material.GRAY_CONCRETE, new Vector3f(0, .9375f, 0), new Vector3f(.375f, .125f, .25f), new Vector3f()));
        BASE_MODEL.add(new PartModelElementItemDisplayRenderer(Material.GRAY_CONCRETE, new Vector3f(0, .0625f, 0), new Vector3f(.375f, .125f, .25f), new Vector3f()));
    }

    public static final ModelTemplate MODEL = new ModelTemplate();
    static {
        MODEL.add(BASE_MODEL);
    }

    public ElectricityMeter() {
        super("torus:electricity_meter", "Electricity Meter", ElectricityMeterInstance.class);
        isHeavy = false;
        itemDropDataWhitelist.add("total");
    }

    @Override
    public ModelTemplate getInitialModel() {
        return MODEL;
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction) {
        return new ElectricityMeterInstance(location, new StructureBodyDef(new StructureComponentDef[]{
          new StructureComponentDef("base", new Vector3f()),
          new StructureComponentDef("in_energy", new Vector3f(), new StructureConnectorDef(Connector.Matter.ENERGY, Connector.FlowDirection.IN, Direction.UP.mask())),
          new StructureComponentDef("out_energy", new Vector3f(), new StructureConnectorDef(Connector.Matter.ENERGY, Connector.FlowDirection.OUT, Direction.DOWN.mask())),
        }), direction);
    }

}
