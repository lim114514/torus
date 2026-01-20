package com.github.alantr7.torus.machine;

import com.github.alantr7.bytils.buffer.ByteArrayWriter;
import com.github.alantr7.torus.gui.structure.InventoryInterfaceFilterEditGUI;
import com.github.alantr7.torus.item.ItemCriteria;
import com.github.alantr7.torus.item.ItemReference;
import com.github.alantr7.torus.item.TorusItem;
import com.github.alantr7.torus.structure.Inspectable;
import com.github.alantr7.torus.structure.inspection.InspectableData;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.structure.LoadContext;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.data.Data;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.Structures;
import com.github.alantr7.torus.structure.component.Socket;
import com.github.alantr7.torus.structure.inventory.BukkitStructureInventory;
import com.github.alantr7.torus.world.Pitch;
import com.github.alantr7.torus.world.TorusWorld;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.BlockInventoryHolder;

import java.nio.charset.StandardCharsets;

import static com.github.alantr7.torus.machine.PhysicalConnector.*;

public class PhysicalConnectorInstance extends StructureInstance implements Inspectable {

    protected Data<Integer> flowDirectionData = dataContainer.persist("flow", Data.Type.INT, 0);

    protected Data<byte[]> filterInternal = dataContainer.persist("filter", Data.Type.BYTE_ARRAY, new byte[0]);

    protected Socket socket;

    protected ItemCriteria inputCriteria = null;

    public PhysicalConnectorInstance(BlockLocation location, StructureBodyDef bodyDef, Direction direction, Pitch pitch, Socket.FlowDirection flowDirection) {
        super(Structures.CONNECTOR, location, bodyDef, direction, pitch);
        flowDirectionData.update(flowDirection.ordinal());
        save();
    }

    PhysicalConnectorInstance(LoadContext context) {
        super(context);
    }

    @Override
    protected void setup() {
        socket = getSocket("connector");
        updateCriteria(getFilter());

        for (Direction direction : Direction.values()) {
            if (socket.isConnected(direction)) {
                state.set(getStateFromDirection(direction.relativeTo(this.direction)), true, false);
            }
        }

        if (socket.getConnections() != 0) {
            state.set(STATE_BACK, true, false);
        }
    }

    @Override
    public InspectableData setupInspectableData() {
        return new InspectableData((byte) 1)
          .property("Flow", () -> getFlowDirection().name());
    }

    @Override
    public void tick(boolean isVirtual) {
        if (TorusWorld.isItemContainer(location.getRelative(direction.getOpposite()))) {
            socket.linkedInventory = new BukkitStructureInventory(((BlockInventoryHolder) location.getRelative(direction.getOpposite()).getBlock().getState()).getInventory());
        } else {
            return;
        }

        if (getFlowDirection() != Socket.FlowDirection.IN && getFlowDirection() != Socket.FlowDirection.ALL)
            return;

        socket.updateNetwork();
        socket.consumeItems(inputCriteria, 4, false).forEach(socket.linkedInventory::addItem);
    }

    @Override
    public void onSocketConnect(Socket socket, Socket neighbor, Direction direction) {
        state.set(getStateFromDirection(direction.relativeTo(this.direction, pitch)), true);
        state.set(STATE_BACK, true);
    }

    @Override
    public void onSocketDisconnect(Socket socket, Socket neighbor, Direction direction) {
        state.set(getStateFromDirection(direction.relativeTo(this.direction, pitch)), false);
        if (socket.getConnections() == 0) {
            state.set(STATE_BACK, false);
        }
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
        TorusItem item = TorusItem.getByItemStack(event.getPlayer().getInventory().getItemInMainHand());
        if (item != null && item.namespacedId.equals("torus:screwdriver")) {
            flowDirectionData.update(getFlowDirection() == Socket.FlowDirection.IN ? Socket.FlowDirection.OUT.ordinal() : Socket.FlowDirection.IN.ordinal());
            socket.setFlowDirection(getFlowDirection());

            event.getPlayer().sendMessage("Flow direction changed to: " + socket.getFlowDirection());
            return;
        }

        new InventoryInterfaceFilterEditGUI(event.getPlayer(), this).open();
    }

    public Socket.FlowDirection getFlowDirection() {
        return Socket.FlowDirection.values()[flowDirectionData.get()];
    }

}
