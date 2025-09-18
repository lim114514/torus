package com.github.alantr7.torus.math;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.world.TorusWorld;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class BlockLocation {

    public TorusWorld world;

    public int x, y, z;

    public BlockLocation(TorusWorld world, int x, int y, int z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BlockLocation(Location location) {
        this.world = TorusPlugin.getInstance().getWorldManager().getWorld(location.getWorld());
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
    }

    public BlockLocation getRelative(BlockLocation location) {
        return getRelative(location.x, location.y, location.z);
    }

    public BlockLocation getRelative(Direction direction) {
        return getRelative(direction.modX, direction.modY, direction.modZ);
    }

    public BlockLocation getRelative(int x, int y, int z) {
        return new BlockLocation(world, this.x + x, this.y + y, this.z + z);
    }

    public Block getBlock() {
        return world.getBukkit().getBlockAt(x, y, z);
    }

    public @Nullable StructureInstance getStructure() {
        return world.getStructure(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BlockLocation that = (BlockLocation) o;
        return x == that.x && y == that.y && z == that.z && Objects.equals(world, that.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, x, y, z);
    }

    @Override
    public String toString() {
        return "{" +
          "x=" + x +
          ", y=" + y +
          ", z=" + z +
          '}';

    }
}
