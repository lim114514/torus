package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.math.BlockLocation;
import com.github.alantr7.torus.math.ConnectorLocation;
import com.github.alantr7.torus.math.Direction;
import com.github.alantr7.torus.model.engine.display.ItemDisplayModelTemplate;
import com.github.alantr7.torus.model.engine.display.ModelTemplate;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.Structures;
import com.github.alantr7.torus.structure.component.Connectable;
import com.github.alantr7.torus.structure.component.Connector;
import com.github.alantr7.torus.structure.component.StructureComponent;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.ItemDisplay;
import org.joml.Vector3f;

public class CableInstance extends StructureInstance implements Connectable {

    @Getter @Setter
    protected int connections;

    @Getter
    Connector.Matter type;

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

    boolean shouldUpdateModel;

    public CableInstance(BlockLocation location, Connector.Matter type) {
        super(type == Connector.Matter.ENERGY ? Structures.ENERGY_CABLE : Structures.ITEM_CABLE, location, Direction.NORTH);
        this.type = type;
    }

    @Override
    public void create() {
        ModelTemplate modelDisconnected = new ModelTemplate();
        modelDisconnected.add(type == Connector.Matter.ENERGY ? MODELS_ENERGY[6] : MODELS_ITEM[6]);

        StructureComponent base = new StructureComponent(this, new BlockLocation(location.world, 0, 0, 0), modelDisconnected.build(location.getBlock().getLocation().add(.5, 0, .5), direction));
        components.put("base", base);

        updateConnections();
    }

    public void updateConnections() {
        updateConnection(Direction.NORTH);
        updateConnection(Direction.SOUTH);
        updateConnection(Direction.EAST);
        updateConnection(Direction.WEST);
        updateConnection(Direction.UP);
        updateConnection(Direction.DOWN);

        if (shouldUpdateModel) {
            updateModel();
            shouldUpdateModel = false;
        }
    }

    public void updateConnection(Direction direction) {
        StructureInstance possibleConnection = location.getRelative(direction).getStructure();
        boolean hasConnected = false;

        // Check if this cable connects to a connector
        if (possibleConnection != null) {
            Connector connector = possibleConnection.getConnectors().get(new ConnectorLocation(location.getRelative(direction), type));
            if (connector != null && connector.isConnectableFrom(direction.getOpposite())) {
                hasConnected = true;
                shouldUpdateModel = true;

                connector.setConnected(direction.getOpposite(), true);
            }
        }

        // Check if this cable connects to another cable
        if (!hasConnected && possibleConnection instanceof CableInstance cable && cable.type == type) {
            hasConnected = true;
            shouldUpdateModel = true;

            cable.setConnected(direction.getOpposite(), true);
            cable.updateModel();
        }

        if (hasConnected != isConnected(direction))
            shouldUpdateModel = true;

        setConnected(direction, hasConnected);
    }

    public void updateModel() {
        components.get("base").getModel().remove();
        ModelTemplate model = new ModelTemplate();
        if (connections == 0) {
            model.add(type == Connector.Matter.ENERGY ? MODELS_ENERGY[6] : MODELS_ITEM[6]);
        }

        for (Direction direction : Direction.values()) {
            if (isConnected(direction)) {
                model.add(type == Connector.Matter.ENERGY ? MODELS_ENERGY[direction.ordinal()] : MODELS_ITEM[direction.ordinal()]);
            }
        }

        components.get("base").setModel(model.build(location.getBlock().getLocation().add(.5, 0, .5), direction));
    }

    @Override
    public void tick() {
    }

    @Override
    public int getAllowedConnections() {
        return Direction.NORTH.mask() | Direction.SOUTH.mask() | Direction.EAST.mask() | Direction.WEST.mask() | Direction.UP.mask() | Direction.DOWN.mask();
    }

}
