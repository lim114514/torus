package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.math.BlockLocation;
import com.github.alantr7.torus.math.Direction;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.data.Data;
import com.github.alantr7.torus.structure.display.ModelTemplate;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.Structures;
import com.github.alantr7.torus.structure.component.Connector;
import com.github.alantr7.torus.structure.inventory.BukkitStructureInventory;
import com.github.alantr7.torus.world.TorusWorld;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.ItemStack;

public class InventoryInterfaceInstance extends StructureInstance {

    public Connector.FlowDirection flowDirection;

    protected Data<Integer> flowDirectionData;

    protected Connector connector;

    public InventoryInterfaceInstance(BlockLocation location, StructureBodyDef bodyDef, Direction direction, Connector.FlowDirection flowDirection) {
        super(Structures.INVENTORY_INTERFACE, location, bodyDef, direction);
        this.flowDirection = flowDirection;
    }

    @Override
    protected void setup() {
        connector = getConnector("connector");
        flowDirectionData = dataContainer.persist("flow", Data.Type.INT, direction.ordinal());
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
                Connector connector = possibleConnection.getConnector(location.getRelative(direction), Connector.Matter.ITEM);
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
                model.add(EnergyCable.MODELS_ITEM[direction.ordinal()]);
            }
        }

        // Add a cable that goes into the connected structure (only if there are other cables around)
        if (connector.getConnections() != 0) {
            model.add(EnergyCable.MODELS_ITEM[direction.ordinal()]);
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
