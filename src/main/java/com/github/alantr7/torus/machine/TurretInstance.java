package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.exception.SetupException;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.structure.EnergyContainer;
import com.github.alantr7.torus.structure.LoadContext;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.Structures;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.component.Socket;
import com.github.alantr7.torus.structure.component.StructureComponent;
import com.github.alantr7.torus.structure.data.Data;
import com.github.alantr7.torus.world.BlockLocation;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;

import java.util.Collection;

public class TurretInstance extends StructureInstance implements EnergyContainer {

    protected StructureComponent head;

    protected Socket inEnergy;

    protected Socket inItem;

    @Getter
    protected Data<Integer> storedEnergy = dataContainer.persist("energy", Data.Type.INT, 0);

    TurretInstance(LoadContext context) {
        super(context);
    }

    public TurretInstance(BlockLocation location, StructureBodyDef bodyDef, Direction direction) {
        super(Structures.TURRET, location, bodyDef, direction);
    }

    protected LivingEntity target;

    @Override
    public void tick() {
        inEnergy.maintainEnergy(this);
        if (getStoredEnergy().get() < Turret.ENERGY_CONSUMPTION)
            return;

        Collection<Entity> entities = location.world.getBukkit().getNearbyEntities(location.toBukkit().add(.5, 0, .5), 5, 1.5, 5, e -> (e instanceof Monster || e instanceof Slime) && !e.getScoreboardTags().contains("torus_entity"));
        if (entities.isEmpty())
            return;

        target = (LivingEntity) entities.iterator().next();
        if (target == null || target.isDead() || target.getHealth() == 0)
            return;

        consumeEnergy(Turret.ENERGY_CONSUMPTION);

        Bukkit.getScheduler().runTaskLater(TorusPlugin.getInstance(), () -> {
            location.world.getBukkit().playSound(location.toBukkit().add(.5, 0, .5), Sound.ENTITY_SHULKER_SHOOT, 1f, 0.2f);

            Location diff = target.getLocation().add(target.getVelocity()).subtract(location.toBukkit().add(.5, 0, .5));
            diff.setY(0);
            Location laserPosition = diff.clone().multiply(.5).add(location.toBukkit().add(.5, 0, .5));
            laserPosition.setY(location.y + 1.375f);
            ItemDisplay laser = (ItemDisplay) location.world.getBukkit().spawnEntity(laserPosition, EntityType.ITEM_DISPLAY);
            laser.setItemStack(new ItemStack(Material.REDSTONE_BLOCK));
            laser.setPersistent(false);

            Transformation laserTransform = laser.getTransformation();
            float[] scale = new float[]{0.05f, 0.05f, (float) diff.length() - 1f};
            float angle = (float) Math.atan2(target.getLocation().getZ() - (0.5f + location.z), target.getLocation().getX() - (0.5f + location.x));

            laserTransform.getScale().set(scale);
            laser.setTransformation(laserTransform);

            laserPosition.setYaw((float) Math.toDegrees(angle) + 90f);
            laserPosition.setPitch(0f);
            laser.teleport(laserPosition);

            Bukkit.getScheduler().runTaskLater(TorusPlugin.getInstance(), () -> {
                target.damage(8f);
                laser.remove();
            }, 3L);

            updateHeadRotation();
            Bukkit.getScheduler().runTaskLater(TorusPlugin.getInstance(), this::updateHeadRotation, 8);
            Bukkit.getScheduler().runTaskLater(TorusPlugin.getInstance(), this::updateHeadRotation, 12);
            Bukkit.getScheduler().runTaskLater(TorusPlugin.getInstance(), this::updateHeadRotation, 16);
        }, (int) (Math.random() * 5));
    }

    public void updateHeadRotation() {
        double angle = Math.atan2(target.getLocation().getX() - (location.x + .5), target.getLocation().getZ() - (location.z + .5));
        structure.getModel().parts.get("head").recycle(model.getPart("head"), location.toBukkit().add(.5, 0, .5), 180f - (float) Math.toDegrees(angle), 0f);
    }

    @Override
    protected void setup() throws SetupException {
        head = requireComponent("head");
        inEnergy = requireSocket("in_energy");
        inEnergy.maximumInput = Turret.ENERGY_MAXIMUM_INPUT;
        inItem = requireSocket("in_item");
    }

    @Override
    public int getEnergyCapacity() {
        return Turret.ENERGY_CAPACITY;
    }

}