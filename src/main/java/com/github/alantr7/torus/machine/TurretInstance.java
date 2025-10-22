package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.math.Direction;
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
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;

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
        if (!hasAmmo) {
            if (!inItem.consumeItems(Turret.AMMO_CRITERIA, 1, true).isEmpty()) {
                hasAmmo = true;
            } else {
                return;
            }
        }

        Collection<Entity> entities = location.world.getBukkit().getNearbyEntities(location.toBukkit().add(.5, 0, .5), 5, 1.5, 5, e -> e instanceof Monster || e instanceof Slime);
        if (entities.isEmpty())
            return;

        target = (LivingEntity) entities.iterator().next();
        if (target == null || target.isDead() || target.getHealth() == 0)
            return;

        Bukkit.getScheduler().runTaskLater(TorusPlugin.getInstance(), () -> {
            location.world.getBukkit().playSound(location.toBukkit().add(.5, 0, .5), Sound.ENTITY_SHULKER_SHOOT, 1f, 0.2f);

            Bukkit.getScheduler().runTaskLater(TorusPlugin.getInstance(), () -> target.damage(8f), 5L);

            updateHeadRotation();
            Bukkit.getScheduler().runTaskLater(TorusPlugin.getInstance(), this::updateHeadRotation, 4);
            Bukkit.getScheduler().runTaskLater(TorusPlugin.getInstance(), this::updateHeadRotation, 8);
            Bukkit.getScheduler().runTaskLater(TorusPlugin.getInstance(), this::updateHeadRotation, 12);
            Bukkit.getScheduler().runTaskLater(TorusPlugin.getInstance(), this::updateHeadRotation, 16);

            hasAmmo = false;
        }, (int) (Math.random() * 5));
    }

    public void updateHeadRotation() {
        double angle = Math.atan2(target.getLocation().getX() - (location.x + .5), target.getLocation().getZ() - (location.z + .5));
        head.getModel().template.recycle(head.getModel(), location.toBukkit().add(.5, 0, .5), 180f - (float) Math.toDegrees(angle), 0f);
    }

    @Override
    protected void setup() {
        head = getComponent("head");
        inEnergy = getConnector("in_energy");
        inItem = getConnector("in_item");
    }

    @Override
    public int getEnergyCapacity() {
        return 5_000;
    }

    @Override
    public String getInspectionText(BlockLocation location, Player player) {
        return EnergyContainer.super.getInspectionText(location, player) + " [Ammo: " + (hasAmmo ? 1 : 0) + " / 1]";
    }

}