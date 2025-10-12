package com.github.alantr7.torus.machine;

import com.github.alantr7.bytils.buffer.ByteArrayWriter;
import com.github.alantr7.torus.gui.InventoryInterfaceFilterEditGUI;
import com.github.alantr7.torus.item.ItemCriteria;
import com.github.alantr7.torus.item.ItemReference;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.math.Direction;
import com.github.alantr7.torus.structure.LoadContext;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.data.Data;
import com.github.alantr7.torus.structure.display.ModelTemplate;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.Structures;
import com.github.alantr7.torus.structure.component.Connector;
import com.github.alantr7.torus.structure.inventory.BukkitStructureInventory;
import com.github.alantr7.torus.world.TorusWorld;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.BlockInventoryHolder;

import java.nio.charset.StandardCharsets;

public class PhysicalConnectorInstance extends StructureInstance {

    protected Data<Integer> flowDirectionData = dataContainer.persist("flow", Data.Type.INT, 0);

    protected Data<byte[]> filterInternal = dataContainer.persist("filter", Data.Type.BYTE_ARRAY, new byte[0]);

    protected Connector connector;

    protected ItemCriteria inputCriteria = null;

    public PhysicalConnectorInstance(BlockLocation location, StructureBodyDef bodyDef, Direction direction, Connector.FlowDirection flowDirection) {
        super(Structures.CONNECTOR, location, bodyDef, direction);
        flowDirectionData.update(flowDirection.ordinal());
        save();
    }

    PhysicalConnectorInstance(LoadContext context) {
        super(context);
    }

    @Override
    protected void setup() {
        connector = getConnector("connector");
        updateCriteria(getFilter());
    }

    public void updateConnections() {
        if (TorusWorld.isItemContainer(location.getRelative(direction.getOpposite()))) {
            connector.linkedInventory = new BukkitStructureInventory(((BlockInventoryHolder) location.getRelative(direction.getOpposite()).getBlock().getState()).getInventory());
        }

        boolean shouldUpdateModel = false;
        for (Direction direction : Direction.values()) {
            if (!connector.isConnectableFrom(direction)) {
                continue;
            }

            StructureInstance possibleConnection = location.getRelative(direction).getStructure();
            boolean hasConnected = false;

            // Check if this interface connects to a connector
            if (possibleConnection != null) {
                Connector connector = possibleConnection.getConnector(location.getRelative(direction), Connector.Matter.ITEM);
                if (connector != null && connector.isConnectableFrom(direction.getOpposite())) {
                    hasConnected = true;
                    shouldUpdateModel = true;

                    connector.setConnected(direction.getOpposite(), true);
                    possibleConnection.save();
                }
            }

            // Check if this interface connects to another cable
            if (!hasConnected && possibleConnection instanceof CableInstance cable && cable.getType() == Connector.Matter.ITEM) {
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
            model.add(EnergyCable.MODELS_ITEM[direction.getOpposite().ordinal()]);
        }

        components.get("cable").setModel(model.build(location.getBlock().getLocation().add(.5, 0, .5), Direction.NORTH));
        save();
    }

    @Override
    public void tick() {
        if (getFlowDirection() != Connector.FlowDirection.IN && getFlowDirection() != Connector.FlowDirection.ALL)
            return;

        if (TorusWorld.isItemContainer(location.getRelative(direction.getOpposite()))) {
            connector.linkedInventory = new BukkitStructureInventory(((BlockInventoryHolder) location.getRelative(direction.getOpposite()).getBlock().getState()).getInventory());
        } else {
            return;
        }

        connector.updateNetwork();
        connector.consumeItems(inputCriteria, 4, false).forEach(connector.linkedInventory::addItem);
    }

    public ItemReference[] getFilter() {
        ItemReference[] references = new ItemReference[9];

        byte[] items = filterInternal.get();
        int idx = 0;
        for (int i = 0; i < items.length;) {
            int len = items[i];
            String namespacedId = new String(items, i + 1, len, StandardCharsets.UTF_8);
            String[] sep = namespacedId.split(":");

            i += len + 1;
            references[idx++] = new ItemReference(sep[0], sep[1]);
        }

        return references;
    }

    public void setFilter(ItemReference[] filter) {
        ByteArrayWriter writer = new ByteArrayWriter();
        for (ItemReference itemReference : filter) {
            if (itemReference == null)
                continue;

            String namespacedId = itemReference.providerId + ":" + itemReference.itemId;
            writer.writeU1(namespacedId.length());
            writer.writeBytes(namespacedId.getBytes(StandardCharsets.UTF_8));
        }
        this.filterInternal.update(writer.getBuffer());
        this.updateCriteria(filter);
        save();
    }

    public void updateCriteria(ItemReference[] filter) {
        ItemCriteria criteria = new ItemCriteria();
        int filteredItems = 0;
        for (ItemReference ref : filter) {
            if (ref != null) {
                if (ref.providerId.equals("minecraft"))
                    criteria.materials.add(Material.valueOf(ref.itemId));
                else
                    criteria.ids.add("torus:" + ref.itemId.toLowerCase());
                filteredItems++;
            }
        }
        inputCriteria = filteredItems != 0 ? criteria : null;
    }

    @Override
    public void handlePlayerInteraction(PlayerInteractEvent event, BlockLocation location) {
        new InventoryInterfaceFilterEditGUI(event.getPlayer(), this).open();
    }

    public Connector.FlowDirection getFlowDirection() {
        return Connector.FlowDirection.values()[flowDirectionData.get()];
    }

}
