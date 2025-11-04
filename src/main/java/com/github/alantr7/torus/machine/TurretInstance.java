package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.exception.SetupException;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.structure.EnergyContainer;
import com.github.alantr7.torus.structure.LoadContext;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.Structures;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.component.Connector;
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

    protected Connector inEnergy;

    protected Connector inItem;

    protected boolean hasAmmo;

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
//        if (!hasAmmo) {
//            if (!inItem.consumeItems(Turret.AMMO_CRITERIA, 1, true).isEmpty()) {
//                hasAmmo = true;
//            } else {
//                return;
//            }
//        }

        if (getStoredEnergy().get() < 250)
            return;

        Collection<Entity> entities = location.world.getBukkit().getNearbyEntities(location.toBukkit().add(.5, 0, .5), 5, 1.5, 5, e -> e instanceof Monster || e instanceof Slime);
        if (entities.isEmpty())
            return;

        target = (LivingEntity) entities.iterator().next();
        if (target == null || target.isDead() || target.getHealth() == 0)
            return;

        consumeEnergy(250);

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

            hasAmmo = false;
        }, (int) (Math.random() * 5));
    }

    public void updateHeadRotation() {
        double angle = Math.atan2(target.getLocation().getX() - (location.x + .5), target.getLocation().getZ() - (location.z + .5));
        Turret.MODEL_HEAD.recycle(model.getPart("head"), location.toBukkit().add(.5, 0, .5), 180f - (float) Math.toDegrees(angle), 0f);
    }

    @Override
    protected void setup() throws SetupException {
        head = requireComponent("head");
        inEnergy = requireConnector("in_energy");
        inItem = requireConnector("in_item");
    }

    @Override
    public int getEnergyCapacity() {
        return 5_000;
    }

}