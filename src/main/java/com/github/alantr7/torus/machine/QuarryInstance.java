package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.math.Direction;
import com.github.alantr7.torus.math.MathUtils;
import com.github.alantr7.torus.structure.EnergyContainer;
import com.github.alantr7.torus.structure.LoadContext;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.component.Connector;
import com.github.alantr7.torus.structure.component.StructureComponent;
import com.github.alantr7.torus.structure.data.Data;
import com.github.alantr7.torus.structure.inventory.CustomStructureInventory;
import com.github.alantr7.torus.structure.inventory.StructureInventory;
import com.github.alantr7.torus.world.BlockLocation;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;

public class QuarryInstance extends StructureInstance implements EnergyContainer {

    protected StructureComponent drillHolder, drill, drillTip, moverX, moverZ;

    @Getter
    protected Data<Integer> storedEnergy = dataContainer.persist("energy", Data.Type.INT, 0);

    protected Data<Integer> drillLength = dataContainer.persist("drill_length", Data.Type.INT, 3);

    protected BlockLocation drillPosition;

    protected StructureInventory outBuffer = new CustomStructureInventory(1);

    protected Connector inConnector, outConnector;

    protected int breakingTicks;

    QuarryInstance(LoadContext context) {
        super(context);
    }

    public QuarryInstance(Structure structure, BlockLocation location, StructureBodyDef bodyDef, Direction direction) {
        super(structure, location, bodyDef, direction);

        getComponent("drill_holder").getModel().entities.forEach(d -> d.setTeleportDuration(10));
        getComponent("drill").getModel().entities.forEach(d -> d.setTeleportDuration(10));
        getComponent("drill_tip").getModel().entities.forEach(d -> d.setTeleportDuration(10));
        getComponent("mover_x").getModel().entities.forEach(d -> d.setTeleportDuration(10));
        getComponent("mover_z").getModel().entities.forEach(d -> d.setTeleportDuration(10));
    }

    byte horizontalPosition = 0;

    byte level = 0;

    @Override
    public void tick() {
        inConnector.maintainEnergy(this);
        outConnector.attemptDirectItemExport();

        if (drillPosition != null) {
            Block ore = drillPosition.getRelative(0, -1, 0).getBlock();
            if (ore.getType() != Material.AIR) {
                if (storedEnergy.get() < 150)
                    return;

                for (Player player : location.world.getBukkit().getPlayersSeeingChunk(location.toBukkit().getChunk())) {
                    player.sendBlockDamage(ore.getLocation(), Math.min(1, (breakingTicks) / 3f));
                }
                if (breakingTicks == 4) {
                    breakingTicks = 0;
                    outBuffer.addItem(new ItemStack(ore.getType()));
                    ore.setType(Material.AIR);

                    for (Player player : location.world.getBukkit().getPlayersSeeingChunk(location.toBukkit().getChunk())) {
                        player.sendBlockDamage(ore.getLocation(), 0);
                    }
                }

                breakingTicks++;
                consumeEnergy(150);
                return;
            }
        }

        if (hasSufficientEnergy(50)) {
            consumeEnergy(50);
            advance();
        }
    }

    public void advance() {
        byte z = (byte) (horizontalPosition / 9);
        byte x = z % 2 == 0 ? (byte) (horizontalPosition % 9) : (byte) (8 - horizontalPosition % 9);
        setDrillRelativePosition(new byte[]{ x, (byte) (-level), (byte) (horizontalPosition / 9) });

        byte dir = level % 2 == 0 ? 1 : (byte) (-1);
        horizontalPosition += dir;

        if (dir == 1 && horizontalPosition == 81) {
            level++;
            horizontalPosition--;
        }
        else if (dir == -1 && horizontalPosition == -1) {
            level++;
            horizontalPosition = 0;
        }

        level = (byte) Math.min(level, 120);
        horizontalPosition = (byte) (horizontalPosition % 81);
    }

    public void setDrillRelativePosition(byte[] position0) {
        byte[] position = new byte[] {(byte) (-position0[0] + 4), position0[1], (byte) (position0[2] - 4)};
        position = MathUtils.rotateVectors(position, direction);

        byte[] xMoverPosition = new byte[] {0, 0, (byte)(position0[2] - 4)};
        xMoverPosition = MathUtils.rotateVectors(xMoverPosition, direction);

        byte[] zMoverPosition = new byte[] {(byte) (-position0[0] + 4), 0, 0};
        zMoverPosition = MathUtils.rotateVectors(zMoverPosition, direction);

        moverX.getModel().teleport(location.toBukkit().add(.5, 4.5f, .5).add(xMoverPosition[0], xMoverPosition[1], xMoverPosition[2]));
        moverZ.getModel().teleport(location.toBukkit().add(.5, 4.5f, .5).add(zMoverPosition[0], zMoverPosition[1], zMoverPosition[2]));

        drillPosition = location.getRelative(position[0], position[1], position[2]);
        updateDrillLength();

        drill.getModel().teleport(location.toBukkit().add(.5, .125f + drillLength.get() / 2f + .5f, .5).add(position[0], position[1], position[2]));
        drillTip.getModel().teleport(location.toBukkit().add(.5, .5f - .125f, .5).add(position[0], position[1], position[2]));

        float[] holderOffset = {.25f, .3f, .15f};
        holderOffset = MathUtils.rotateVectors(holderOffset, direction.getOpposite());
        drillHolder.getModel().teleport(location.toBukkit().add(.5 + holderOffset[0], 5 + holderOffset[1], .5 + holderOffset[2]).add(position[0], 0, position[2]));
    }

    public void updateDrillLength() {
        float f = .25f;
        int len = 3 + location.y - (drillPosition.y);

        drillLength.update(len);

        ItemDisplay drillModel = drill.getModel().entities.getFirst();
        Transformation transform = drillModel.getTransformation();
        transform.getScale().y = len + f;
        drillModel.setTransformation(transform);
    }

    @Override
    protected void setup() {
        drillHolder = getComponent("drill_holder");
        drill = getComponent("drill");
        drillTip = getComponent("drill_tip");
        moverX = getComponent("mover_x");
        moverZ = getComponent("mover_z");

        inConnector = getConnector("in_energy");
        inConnector.maximumInput = 350;
        outConnector = getConnector("out_item");
        outConnector.linkedInventory = outBuffer;
    }

    @Override
    public int getEnergyCapacity() {
        return 15_000;
    }

}
