package com.github.alantr7.torus.machine;

import com.github.alantr7.bytils.buffer.ByteArrayReader;
import com.github.alantr7.bytils.buffer.ByteArrayWriter;
import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.exception.SetupException;
import com.github.alantr7.torus.item.TorusItem;
import com.github.alantr7.torus.player.TorusPlayer;
import com.github.alantr7.torus.structure.Conductor;
import com.github.alantr7.torus.structure.LoadContext;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.Structures;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.component.Socket;
import com.github.alantr7.torus.structure.data.Data;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Slime;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class WireConnectorInstance extends StructureInstance implements Conductor {

    private final Map<BlockLocation, WireConnection> connections = new HashMap<>();

    private final Data<byte[]> connectionsRaw = dataContainer.persist("connections", Data.Type.BYTE_ARRAY, new byte[0]);

    private final Data<Byte> typeRaw = dataContainer.persist("type", Data.Type.BYTE, (byte) 0);

    public Slime connectionCandidate;

    public enum Type {
        RELAY, CONNECTOR;
    }

    WireConnectorInstance(LoadContext context) {
        super(context);
    }

    public WireConnectorInstance(Type type, BlockLocation location, StructureBodyDef bodyDef, Direction direction) {
        super(type == Type.CONNECTOR ? Structures.WIRE_CONNECTOR : Structures.WIRE_RELAY, location, bodyDef, direction);
        this.typeRaw.update((byte) type.ordinal());
    }

    @Override
    public void tick() {

    }

    @Override
    protected void setup() throws SetupException {
        loadConnections();
    }

    @Override
    public void destroy() {
        connections.forEach((location, conn) -> {
            WireConnectorInstance remote = (WireConnectorInstance) conn.location.getStructure();
            if (remote != null) {
                WireConnection remoteConnection = remote.connections.remove(this.location);
                if (remoteConnection != null) {
                    remoteConnection.slime.remove();
                    remote.saveConnections();
                }
            }
        });
    }

    @Override
    public void handleModelInit() {
        connectionCandidate = spawnSlime();
        connections.forEach((loc, conn) -> {
            if (conn.slime == null) {
                conn.slime = createNewConnection(loc, false);
            }
            if (loc.getStructure() instanceof WireConnectorInstance wire2) {
                WireConnection remote = wire2.connections.get(location);
                if (remote != null) {
                    if (remote.slime == null) {
                        remote.slime = wire2.createNewConnection(location, false);
                    }
                    conn.slime.setLeashHolder(remote.slime);
                }
            }
        });
    }

    @Override
    public void handleModelDestroy() {
        super.handleModelDestroy();
        connections.forEach((loc, conn) -> {
            conn.slime.setLeashHolder(null);
            conn.slime.remove();
        });
        connectionCandidate.remove();
    }

    @Override
    public void handlePlayerInteraction(PlayerInteractEvent event, BlockLocation location) {
        if (!TorusItem.is(event.getPlayer().getInventory().getItemInMainHand(), "torus:copper_wire")) {
            return;
        }

        // Establish connection between two connectors
        TorusPlayer player = TorusPlayer.get(event.getPlayer());
        if (player.pendingWireConnection != null) {
            WireConnectorInstance origin = player.pendingWireConnection;
            if (origin == this) {
                connectionCandidate.setLeashHolder(null);
            } else if (origin.connections.containsKey(location)) {
                origin.connectionCandidate.setLeashHolder(null);
            } else {
                if (origin.location.getDistanceTo(location) > 10) {
                    event.getPlayer().sendMessage(ChatColor.RED + "Cable can not stretch that far.");
                    return;
                }

                origin.connectionCandidate.setLeashHolder(null);
                createNewConnection(origin.location, true).setLeashHolder(origin.createNewConnection(location, true));

                if (event.getPlayer().getGameMode() == GameMode.SURVIVAL || event.getPlayer().getGameMode() == GameMode.ADVENTURE) {
                    event.getPlayer().getInventory().getItemInMainHand().setAmount(event.getPlayer().getInventory().getItemInMainHand().getAmount() - 1);
                }
            }

            player.pendingWireConnection = null;
        } else {
            player.pendingWireConnection = this;
            connectionCandidate.setLeashHolder(event.getPlayer());
        }
    }

    @Override
    public Collection<BlockLocation> getConnectedNodes() {
        if (getType() == Type.RELAY)
            return connections.keySet();

        List<BlockLocation> nodes = new ArrayList<>(connections.keySet());
        nodes.addAll(getSocket("base").getConnectedNodes());

        return nodes;
    }

    public void updateConnections() {
        for (Direction direction : Direction.values()) {
            updateConnection(direction);
        }
    }

    public void updateConnection(Direction direction) {
        StructureInstance possibleConnection = location.getRelative(direction).getStructure();
        boolean hasConnected = false;

        // Check if this cable connects to a connector
        if (possibleConnection != null) {
            Socket socket = possibleConnection.getSocket(location, Socket.Matter.ENERGY);
            if (socket != null && socket.isConnectableFrom(direction.getOpposite())) {
                hasConnected = true;
                socket.setConnected(direction.getOpposite(), true);
                possibleConnection.save();
            }
        }

        // Check if this cable connects to another cable
        if (!hasConnected && possibleConnection instanceof CableInstance cable && cable.getType() == Socket.Matter.ENERGY) {
            hasConnected = true;
            cable.getSocket("base").setConnected(direction.getOpposite(), true);
            cable.updateModel();
        }

        getSocket("base").setConnected(direction, hasConnected);
    }

    private Slime spawnSlime() {
        Slime slime1 = location.world.getBukkit().spawn(location.toBukkitCentered().add(0, .2, 0), Slime.class, CreatureSpawnEvent.SpawnReason.CUSTOM, false, slime -> {
            slime.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION, 0, false, false));
            slime.setSize(1);
            slime.setAI(false);
            slime.setPersistent(false);
            slime.setInvulnerable(true);
            slime.setRemoveWhenFarAway(false);
            slime.getAttribute(Attribute.SCALE).setBaseValue(0.01d); // scales down the entity to prevent initial flicker
            slime.addScoreboardTag("torus_entity");
            slime.getPersistentDataContainer().set(new NamespacedKey(TorusPlugin.getInstance(), "connector_position"), PersistentDataType.LIST.integers(), List.of(location.x, location.y, location.z));
        });
        Bukkit.getScheduler().runTaskLater(TorusPlugin.getInstance(), () -> slime1.getAttribute(Attribute.SCALE).setBaseValue(1d), 20L);
        return slime1;
    }

    public Slime createNewConnection(BlockLocation origin, boolean save) {
        Slime slime = connectionCandidate;
        connections.put(origin, new WireConnection(origin, slime));
        if (save) {
            saveConnections();
        }

        connectionCandidate = spawnSlime();
        return slime;
    }

    private void loadConnections() {
        if (connectionsRaw.get().length == 0)
            return;

        ByteArrayReader reader = new ByteArrayReader(connectionsRaw.get());
        int count = reader.readU1();
        for (int i = 0; i < count; i++) {
            BlockLocation loc = location.getRelative(reader.readU1(), reader.readU1(), reader.readU1());
            connections.put(loc, new WireConnection(loc, null));
        }

    }

    private void saveConnections() {
        ByteArrayWriter writer = new ByteArrayWriter();
        writer.writeU1(connections.size());

        connections.forEach((loc, conn) -> {
            writer.writeU1(loc.x - location.x);
            writer.writeU1(loc.y - location.y);
            writer.writeU1(loc.z - location.z);
        });

        connectionsRaw.update(writer.getBuffer());
        save();
    }

    public Type getType() {
        return Type.values()[typeRaw.get()];
    }

    static class WireConnection {
        BlockLocation location;
        Slime slime;

        WireConnection(BlockLocation location, Slime slime) {
            this.location = location;
            this.slime = slime;
        }
    }

}
