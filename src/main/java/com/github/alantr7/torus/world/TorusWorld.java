package com.github.alantr7.torus.world;

import com.github.alantr7.torus.config.MainConfig;
import com.github.alantr7.torus.network.NetworkManager;
import com.github.alantr7.torus.structure.Status;
import com.github.alantr7.torus.utils.EventUtils;
import com.github.alantr7.torus.log.Category;
import com.github.alantr7.torus.log.TorusLogger;
import com.github.alantr7.torus.model.PartModel;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.component.Socket;
import lombok.Getter;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TorusWorld {

    @Getter
    private final World bukkit;

    public final File torusDirectory;

    public final File torusRegionsDirectory;

    protected final Map<Vector2i, TorusRegion> regions = new HashMap<>();

    @Getter
    private int ticks;

    public final NetworkManager networkManager = new NetworkManager(this);

    public TorusWorld(World bukkit) {
        this.bukkit = bukkit;
        this.torusDirectory = new File(bukkit.getWorldFolder(), "torus");
        this.torusDirectory.mkdirs();
        this.torusRegionsDirectory = new File(this.torusDirectory, "region");
        this.torusRegionsDirectory.mkdirs();
    }

    static final Set<Material> MINECRAFT_BLOCK_CONTAINER_TYPES = Set.of(
      Material.CHEST, Material.TRAPPED_CHEST, Material.DROPPER, Material.DISPENSER, Material.HOPPER, Material.SHULKER_BOX
    );
    public static boolean isItemContainer(BlockLocation location) {
        Material material = location.getBlock().getType();
        if (MINECRAFT_BLOCK_CONTAINER_TYPES.contains(material))
            return true;

        if (material.name().endsWith("_SHULKER_BOX"))
            return true;

        return false;
    }

    @Nullable
    TorusRegion getRegion(BlockLocation location) {
        return regions.get(new Vector2i(location.regionX, location.regionZ));
    }

    TorusRegion getRegionAt(int x, int z) {
        return regions.get(new Vector2i(x, z));
    }

    @NotNull
    TorusRegion getRegionOrLoad(BlockLocation location) {
        return regions.computeIfAbsent(new Vector2i(location.regionX, location.regionZ), v -> {
            TorusRegion region = new TorusRegion(this, v.x, v.y);
            try {
                region.load();
            } catch (Exception e) {
                e.printStackTrace();
                TorusLogger.error(Category.WORLD, e.getMessage());
            }

            return region;
        });
    }

    @Nullable
    public TorusChunk getChunk(BlockLocation location) {
        return getChunkAt(location.x >> 4, location.z >> 4);
    }

    @Nullable
    public TorusChunk getChunkAt(int x, int z) {
        TorusRegion region = getRegionAt(x >> 5, z >> 5);
        return region != null ? region.chunks.get(new Vector2i(x, z)) : null;
    }

    @NotNull
    TorusChunk getChunkOrLoad(BlockLocation location) {
        return getRegionOrLoad(location).getOrLoadChunk(location.x >> 4, location.z >> 4);
    }

    protected void handleChunkLoad(Chunk chunk) {
        TorusChunk torusChunk = getChunkOrLoad(new BlockLocation(this, chunk.getX() << 4, 0, chunk.getZ() << 4));
        for (StructureInstance structure : torusChunk.structures.values()) {
            structure.handleLoad();
        }
    }

    protected void handleChunkUnload(Chunk chunk) {
        TorusRegion region = getRegion(new BlockLocation(this, chunk.getX() << 4, 0, chunk.getZ() << 4));
        if (region == null)
            return;

        TorusChunk torusChunk = region.chunks.remove(new Vector2i(chunk.getX(), chunk.getZ()));
        if (torusChunk != null) {
            // TODO: Determine if chunk should unload or become virtual
            torusChunk.structures.values().forEach(StructureInstance::handleUnload);
            if (torusChunk.isUnsaved) {
                try {
                    region.save();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (region.chunks.isEmpty()) {
                regions.remove(new Vector2i(region.x, region.z));
            }
        }
    }

    public StructureInstance getStructure(BlockLocation location) {
        TorusChunk chunk = getChunk(location);
        if (chunk == null)
            return null;

        BlockLocation machineLocation = chunk.occupations.get(location);
        if (machineLocation == null)
            return null;

        if (machineLocation.x >> 4 == location.x >> 4 && machineLocation.z >> 4 == location.z >> 4)
            return chunk.structures.get(machineLocation);

        TorusChunk machineChunk = getChunk(machineLocation);
        if (machineChunk == null)
            return null;

        return machineChunk.structures.get(machineLocation);
    }

    void tick() {
        regions.values().forEach(region -> {
            region.chunks.values()
              .forEach(chunk -> chunk.structures.values()
                .forEach(s -> {
                if (s.isCorrupted || s.isUnloaded())
                    return;

                try {
                    s.tick(chunk.status == Status.VIRTUAL);
                    for (PartModel part : s.model.parts.values()) {
                        if (part.getAnimation() != null) {
                            part.getAnimation().tick();
                        }
                    }
                } catch (Exception e) {
                    TorusLogger.error(Category.STRUCTURES, "Encountered an error whilst ticking a structure - marked it as corrupted.");
                    e.printStackTrace();
                    s.corrupt();
                }
            }));
        });
        networkManager.tick();
        ticks++;
    }

    public void placeStructure(@NotNull StructureInstance instance) {
        TorusChunk chunk = getChunkOrLoad(instance.location);
        chunk.structures.put(instance.location, instance);
        chunk.isUnsaved = true;

        // Place bounds
        byte[] bounds = instance.getBounds();
        for (int i = 0; i < bounds.length; i += 3) {
            BlockLocation relative = instance.location.getRelative(bounds[i], bounds[i+1], bounds[i+2]);
            relative.getBlock().setType(Material.BARRIER);

            TorusChunk occupationChunk = getChunkOrLoad(relative);
            occupationChunk.occupations.put(relative, instance.location);
            occupationChunk.isUnsaved = true;
        }

        // Connect to neighboring structures
        for (Socket socket : instance.getSockets()) {
            for (Direction direction : socket.getValidConnectionsDirections()) {
                BlockLocation relativeLoc = socket.getComponent().absoluteLocation.getRelative(direction);
                StructureInstance neighbor = relativeLoc.getStructure();
                if (neighbor == null)
                    continue;

                Socket neighborSocket = neighbor.getSocket(socket.getComponent().absoluteLocation, socket.medium);
                if (neighborSocket == null)
                    continue;

                if (!neighborSocket.isConnectableFrom(direction.getOpposite()))
                    continue;

                if (!EventUtils.callStructuresConnectEvent(socket, neighborSocket, direction, direction.getOpposite()))
                    continue;

                socket.setConnected(direction, true);
                instance.onSocketConnect(socket, neighborSocket, direction);

                neighborSocket.setConnected(direction.getOpposite(), true);
                neighbor.onSocketConnect(neighborSocket, socket, direction.getOpposite());
            }

            networkManager.queueLoaded(socket);
        }
    }

    public void removeStructure(@NotNull StructureInstance instance) {
        TorusChunk chunk0 = getChunkOrLoad(instance.location);
        chunk0.structures.remove(instance.location);

        // Remove bounds
        byte[] bounds = instance.getBounds();
        for (int i = 0; i < bounds.length; i += 3) {
            BlockLocation relative = instance.location.getRelative(bounds[i], bounds[i+1], bounds[i+2]);
            relative.getBlock().setType(Material.AIR);

            TorusChunk chunk = getChunkOrLoad(relative);
            if (chunk.occupations.remove(relative) != null) {
                chunk.isUnsaved = true;
            }
        }

        // Disconnect from neighboring structures
        for (Socket socket : instance.getSockets()) {
            for (Direction direction : socket.getValidConnectionsDirections()) {
                if (!socket.isConnected(direction))
                    continue;

                BlockLocation relativeLoc = socket.getComponent().absoluteLocation.getRelative(direction);
                StructureInstance neighbor = relativeLoc.getStructure();
                if (neighbor == null)
                    continue;

                Socket neighborSocket = neighbor.getSocket(socket.getComponent().absoluteLocation, socket.medium);
                if (neighborSocket == null)
                    continue;

                if (!neighborSocket.isConnected(direction.getOpposite()))
                    continue;

                EventUtils.callStructuresDisconnectEvent(socket, neighborSocket, direction, direction.getOpposite());

                neighborSocket.setConnected(direction.getOpposite(), false);
                neighbor.onSocketDisconnect(neighborSocket, socket, direction.getOpposite());

                networkManager.queueLoaded(neighborSocket);
            }
        }

        // Remove models
        instance.handleUnload();

        // Run destroy callbacks
        instance.onRemove();
    }

    void load() {
    }

    public void save() {
        long start = System.currentTimeMillis();
        regions.forEach((loc, region) -> {
            try {
                region.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        if (MainConfig.LOGS_WORLD_SAVE) {
            TorusLogger.info(Category.WORLD, "Saved " + bukkit.getName() + " in " + (System.currentTimeMillis() - start) + "ms.");
        }
    }

}
