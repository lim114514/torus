package com.github.alantr7.torus.world;

import com.github.alantr7.torus.config.MainConfig;
import com.github.alantr7.torus.log.Category;
import com.github.alantr7.torus.log.TorusLogger;
import com.github.alantr7.torus.machine.CableInstance;
import com.github.alantr7.torus.machine.PhysicalConnectorInstance;
import com.github.alantr7.torus.math.Direction;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.component.Connector;
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

    public final File directory;

    public final File regionsDirectory;

    protected final Map<Vector2i, TorusRegion> regions = new HashMap<>();

    @Getter
    private int ticks;

    public TorusWorld(World bukkit) {
        this.bukkit = bukkit;
        this.directory = new File(bukkit.getWorldFolder(), "torus");
        this.directory.mkdirs();
        this.regionsDirectory = new File(this.directory, "regions");
        this.regionsDirectory.mkdirs();
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
        getChunkOrLoad(new BlockLocation(this, chunk.getX() << 4, 0, chunk.getZ() << 4));
    }

    protected void handleChunkUnload(Chunk chunk) {
        TorusRegion region = getRegion(new BlockLocation(this, chunk.getX() << 4, 0, chunk.getZ() << 4));
        if (region == null)
            return;

        TorusChunk torusChunk = region.chunks.remove(new Vector2i(chunk.getX(), chunk.getZ()));
        if (torusChunk != null) {
            if (torusChunk.isDirty) {
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

    public void tick() {
        regions.values().forEach(region -> {
            region.chunks.values()
              .forEach(chunk -> chunk.structures.values()
                .forEach(s -> {
                if (s.isCorrupted || !s.isFullyLoaded())
                    return;

                try {
                    s.tick();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }));
        });
        ticks++;
    }

    public void placeStructure(StructureInstance instance) {
        TorusChunk chunk = getChunkOrLoad(instance.location);
        chunk.structures.put(instance.location, instance);
        chunk.isDirty = true;

        // Place bounds
        byte[] bounds = instance.getBounds();
        for (int i = 0; i < bounds.length; i += 3) {
            BlockLocation relative = instance.location.getRelative(bounds[i], bounds[i+1], bounds[i+2]);
            relative.getBlock().setType(Material.BARRIER);

            TorusChunk occupationChunk = getChunkOrLoad(relative);
            occupationChunk.occupations.put(relative, instance.location);
            occupationChunk.isDirty = true;
        }

        // Update all cables if I'm cable
        if (instance instanceof CableInstance cable) {
            for (Direction direction : Direction.values()) {
                if (cable.isConnectableFrom(direction)) {
                    // TODO: Replace with an interface like "Updatable" and call its updateConnections() instead
                    if (instance.location.getRelative(direction).getStructure() instanceof CableInstance cable1) {
                        if (cable.getType() == cable1.getType()) {
                            cable1.updateConnections();
                        }
                    }
                    else if (instance.location.getRelative(direction).getStructure() instanceof PhysicalConnectorInstance iii) {
                        if (cable.getType() == Connector.Matter.ITEM) {
                            iii.updateConnections();
                        }
                    }
                }
            }
        }

        // Update all cables around it
        instance.getConnectors().forEach((connector) -> {
            for (Direction direction : Direction.values()) {
                if (connector.isConnectableFrom(direction)) {
                    if (connector.getComponent().absoluteLocation.getRelative(direction).getStructure() instanceof CableInstance cable) {
                        cable.updateConnections();
                    }
                }
            }
        });

        if (instance instanceof PhysicalConnectorInstance iii) {
            iii.updateConnections();
            iii.updateModel();
        }
    }

    public void removeStructure(StructureInstance instance) {
        TorusChunk chunk0 = getChunkOrLoad(instance.location);
        chunk0.structures.remove(instance.location);

        // Remove bounds
        byte[] bounds = instance.getBounds();
        for (int i = 0; i < bounds.length; i += 3) {
            BlockLocation relative = instance.location.getRelative(bounds[i], bounds[i+1], bounds[i+2]);
            relative.getBlock().setType(Material.AIR);

            TorusChunk chunk = getChunkOrLoad(relative);
            if (chunk.occupations.remove(relative) != null) {
                chunk.isDirty = true;
            }
        }

        // Update all cables if I'm cable
        if (instance instanceof CableInstance cable) {
            for (Direction direction : Direction.values()) {
                if (cable.isConnectableFrom(direction)) {
                    StructureInstance neighbor = instance.location.getRelative(direction).getStructure();
                    if (neighbor == null)
                        continue;

                    if (neighbor instanceof CableInstance cable1) {
                        if (cable.getType() == cable1.getType()) {
                            cable1.updateConnections();
                        }
                    } else if (instance.location.getRelative(direction).getStructure() instanceof PhysicalConnectorInstance iii) {
                        if (cable.getType() == Connector.Matter.ITEM) {
                            iii.updateConnections();
                        }
                    } else if (cable.isConnected(direction)) {
                        Connector connector = neighbor.getConnector(instance.location.getRelative(direction), cable.getType());
                        if (connector != null) {
                            connector.setConnected(direction.getOpposite(), false);
                        }
                        neighbor.save();
                    }
                }
            }
        }

        // Update all cables around connectors
        else instance.getConnectors().forEach((connector) -> {
            for (Direction direction : Direction.values()) {
                if (connector.isConnectableFrom(direction)) {
                    if (connector.getComponent().absoluteLocation.getRelative(direction).getStructure() instanceof CableInstance cable) {
                        cable.updateConnections();
                    }
                }
            }
        });

        // Remove models
        instance.getComponents().forEach((component) -> {
            if (component.getModel() != null)
                component.getModel().remove();
        });
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
