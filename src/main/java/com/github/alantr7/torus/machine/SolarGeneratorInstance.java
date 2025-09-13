package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.math.BlockLocation;
import com.github.alantr7.torus.math.ConnectorLocation;
import com.github.alantr7.torus.math.Direction;
import com.github.alantr7.torus.model.engine.display.ItemDisplayModelTemplate;
import com.github.alantr7.torus.model.engine.display.Model;
import com.github.alantr7.torus.model.engine.display.ModelTemplate;
import com.github.alantr7.torus.structure.EnergyContainer;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.Structures;
import com.github.alantr7.torus.structure.component.Connector;
import com.github.alantr7.torus.structure.component.StructureComponent;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.ItemDisplay;
import org.joml.Vector3f;

public class SolarGeneratorInstance extends StructureInstance implements EnergyContainer {

    @Getter @Setter
    double energyCapacity = 2000;

    @Getter @Setter
    double storedEnergy;

    static ModelTemplate MODEL = new ModelTemplate();
    static {
        MODEL.add(new ItemDisplayModelTemplate(Material.GRAY_CONCRETE_POWDER, ItemDisplay.ItemDisplayTransform.GROUND, 0, new Vector3f(0, 0.125f, 0), new Vector3f(0.75f, 0.25f, 0.75f), 0f, 0f));
        MODEL.add(new ItemDisplayModelTemplate(Material.LIGHT_GRAY_CONCRETE, ItemDisplay.ItemDisplayTransform.GROUND, 0, new Vector3f(0, 1.0625f, 0), new Vector3f(0.125f, 1.625f, 0.125f), 0f, 0f));

        MODEL.add(new ItemDisplayModelTemplate(Material.BLACK_CONCRETE, ItemDisplay.ItemDisplayTransform.GROUND, 0, new Vector3f(0, 1.875f, 0), new Vector3f(2.4f, 0.062f, 2.4f), 0f, 0f));
        MODEL.add(new ItemDisplayModelTemplate(Material.LAPIS_BLOCK, ItemDisplay.ItemDisplayTransform.GROUND, 0, new Vector3f(0, 1.875f, 0), new Vector3f(2.25f, 0.0625f, 2.25f), 0f, 0f));
    }

    static ModelTemplate CONNECTOR_MODEL = new ModelTemplate();
    static {
        CONNECTOR_MODEL.add(new ItemDisplayModelTemplate(Material.GRAY_CONCRETE, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(0f, 0.5f, 0.4375f), new Vector3f(0.625f, 0.625f, 0.125f), 0f, 0f));
    }

    public SolarGeneratorInstance(BlockLocation location, Direction direction) {
        super(Structures.SOLAR_GENERATOR, location, direction);
    }

    @Override
    public void create() {
        components.put("base", new StructureComponent(this, location, MODEL.build(location.getBlock().getLocation().add(.5, 0, .5), direction)));

        Model connectorModel = CONNECTOR_MODEL.build(location.getBlock().getLocation().add(.5, 0, .5), direction);
        components.put("power_connector", new StructureComponent(this, new BlockLocation(location.world, 0, 0, 0), connectorModel));

        Connector connector = new Connector(components.get("power_connector"), direction.getOpposite().mask(), Connector.FlowDirection.OUT, Connector.Matter.ENERGY);
        connectors.put(new ConnectorLocation(location.getRelative(0, 0, 0), Connector.Matter.ENERGY), connector);
    }

    @Override
    public void tick() {
        if (storedEnergy < energyCapacity) {
            storedEnergy += 35;
        }
    }

}
