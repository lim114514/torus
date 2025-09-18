package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.math.BlockLocation;
import com.github.alantr7.torus.math.ConnectorLocation;
import com.github.alantr7.torus.math.Direction;
import com.github.alantr7.torus.math.MathUtils;
import com.github.alantr7.torus.structure.display.ItemDisplayModelTemplate;
import com.github.alantr7.torus.structure.display.Model;
import com.github.alantr7.torus.structure.display.ModelTemplate;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.Structures;
import com.github.alantr7.torus.structure.component.Connector;
import com.github.alantr7.torus.structure.component.StructureComponent;
import com.github.alantr7.torus.structure.inventory.BukkitStructureInventory;
import com.github.alantr7.torus.world.TorusWorld;
import org.bukkit.Material;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.joml.Vector3f;

public class InventoryInterfaceInstance extends StructureInstance {

    static ModelTemplate CONNECTOR_MODEL = new ModelTemplate();
    static {
        CONNECTOR_MODEL.add(new ItemDisplayModelTemplate(Material.GRAY_CONCRETE, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(0f, 0.5f, -0.5f + 0.0625f), new Vector3f(0.625f, 0.625f, 0.125f), 0f, 0f));
    }

    public Connector.FlowDirection flowDirection;

    protected Connector connector;

    public InventoryInterfaceInstance(BlockLocation location, Direction direction, Connector.FlowDirection flowDirection) {
        super(Structures.INVENTORY_INTERFACE, location, direction);
        this.flowDirection = flowDirection;
    }

    @Override
    public void create() {
        Model connectorModel = CONNECTOR_MODEL.build(location.getBlock().getLocation().add(.5, 0, .5), direction);
        components.put("connector", new StructureComponent(this, new BlockLocation(location.world, 0, 0, 0), connectorModel));

        connector = new Connector(components.get("connector"), MathUtils.setFlag(0b111111, direction.mask(), false), flowDirection, Connector.Matter.ITEM);
        connectors.put(new ConnectorLocation(location.getRelative(0, 0, 0), Connector.Matter.ITEM), connector);

        components.put("cable", new StructureComponent(this, new BlockLocation(location.world, 0, 0, 0), ModelTemplate.EMPTY.build(location.getBlock().getLocation().add(.5, 0, .5), direction)));

        updateConnections();
    }

    public void updateConnections() {
        if (TorusWorld.isItemContainer(location.getRelative(direction))) {
            connector.linkedInventory = new BukkitStructureInventory(((BlockInventoryHolder) location.getRelative(direction).getBlock().getState()).getInventory());
        }

        boolean shouldUpdateModel = false;
        for (Direction direction : Direction.values()) {
            if (!connector.isConnectableFrom(direction)) {
                continue;
            }

            StructureInstance possibleConnection = location.getRelative(direction).getStructure();
            boolean hasConnected = false;

            // Check if this cable connects to a connector
            if (possibleConnection != null) {
                Connector connector = possibleConnection.getConnectors().get(new ConnectorLocation(location.getRelative(direction), Connector.Matter.ITEM));
                if (connector != null && connector.isConnectableFrom(direction.getOpposite())) {
                    hasConnected = true;
                    shouldUpdateModel = true;

                    connector.setConnected(direction.getOpposite(), true);
                }
            }

            // Check if this cable connects to another cable
            if (!hasConnected && possibleConnection instanceof CableInstance cable && cable.type == Connector.Matter.ITEM) {
                hasConnected = true;
                shouldUpdateModel = true;

                cable.setConnected(direction.getOpposite(), true);
                cable.updateModel();
            }

            if (hasConnected != connector.isConnected(direction))
                shouldUpdateModel = true;

            connector.setConnected(direction, hasConnected);
        }

        if (shouldUpdateModel)
            updateModel();
    }

    public void updateModel() {
        components.get("cable").getModel().remove();
        ModelTemplate model = new ModelTemplate();

        for (Direction direction : Direction.values()) {
            if (connector.isConnected(direction)) {
                model.add(CableInstance.MODELS_ITEM[direction.ordinal()]);
            }
        }

        // Add a cable that goes into the connected structure (only if there are other cables around)
        if (connector.getConnections() != 0) {
            model.add(CableInstance.MODELS_ITEM[direction.ordinal()]);
        }

        components.get("cable").setModel(model.build(location.getBlock().getLocation().add(.5, 0, .5), direction));
    }

    @Override
    public void tick() {
        if (flowDirection != Connector.FlowDirection.IN && flowDirection != Connector.FlowDirection.ALL)
            return;

        connector.updateConnections();
        for (Connector.Connection conn : connector.getConnectedStructures()) {
            if (conn.connector.getFlowDirection() != Connector.FlowDirection.OUT && conn.connector.getFlowDirection() != Connector.FlowDirection.ALL)
                continue;

            if (conn.connector.linkedInventory != null) {
                ItemStack[] items = conn.connector.linkedInventory.getItems();
                for (int i = 0; i < items.length; i++) {
                    ItemStack item = items[i];
                    if (item == null) {
                        continue;
                    }
                    connector.linkedInventory.addItem(item.clone());
                    item.setAmount(0);

                    conn.connector.linkedInventory.getItems()[i] = null;
                }
            }
        }
    }

}
