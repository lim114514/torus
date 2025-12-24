package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.model.de_provider.DisplayEntitiesPartModel;
import com.github.alantr7.torus.model.de_provider.DisplayEntitiesPartModelTemplate;
import com.github.alantr7.torus.structure.inspection.InspectableData;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.math.MathUtils;
import com.github.alantr7.torus.structure.EnergyContainer;
import com.github.alantr7.torus.structure.LoadContext;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.component.Socket;
import com.github.alantr7.torus.structure.component.StructureComponent;
import com.github.alantr7.torus.structure.data.Data;
import com.github.alantr7.torus.structure.inventory.CustomStructureInventory;
import com.github.alantr7.torus.structure.inventory.StructureInventory;
import com.github.alantr7.torus.world.BlockLocation;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;

public class QuarryInstance extends StructureInstance implements EnergyContainer {

    protected StructureComponent head, feed, bit, gantryX, gantryZ;

    @Getter
    protected Data<Integer> storedEnergy = dataContainer.persist("energy", Data.Type.INT, 0);

    protected Data<Integer> drillLength = dataContainer.persist("drill_length", Data.Type.INT, 3);

    protected BlockLocation drillPosition;

    protected StructureInventory outBuffer = new CustomStructureInventory(1);

    protected Socket inSocket, outSocket;

    protected int breakingTicks;

    protected Data<Byte> horizontalPosition = dataContainer.persist("pos_h", Data.Type.BYTE, (byte) 0);

    protected Data<Byte> level = dataContainer.persist("pos_v", Data.Type.BYTE, (byte) 0);

    QuarryInstance(LoadContext context) {
        super(context);
    }

    public QuarryInstance(Structure structure, BlockLocation location, StructureBodyDef bodyDef, Direction direction) {
        super(structure, location, bodyDef, direction);
    }

    @Override
    public void tick() {
        inSocket.maintainEnergy(this);
        outSocket.attemptDirectItemExport();

        if (drillPosition != null) {
            Block ore = drillPosition.getRelative(0, -1, 0).getBlock();
            if (!ore.getType().isAir() && !ore.isLiquid() && !Quarry.BLOCK_BLACKLIST.contains(ore.getType())) {
                if (storedEnergy.get() < Quarry.ENERGY_CONSUMPTION_ON_MINE)
                    return;

                if (breakingTicks == 4) {
                    breakingTicks = 0;
                    for (ItemStack drop : ore.getDrops(new ItemStack(Material.IRON_PICKAXE))) {
                        outBuffer.addItem(drop.clone());
                        break;
                    }

                    ore.getWorld().playSound(ore.getLocation(), ore.getBlockSoundGroup().getBreakSound(), 0.75f, 1f);
                    ore.setType(Material.AIR);

                    for (Player player : location.world.getBukkit().getPlayersSeeingChunk(location.toBukkit().getChunk())) {
                        player.sendBlockDamage(ore.getLocation(), 0);
                    }
                } else {
                    ore.getWorld().playSound(ore.getLocation(), ore.getBlockSoundGroup().getHitSound(), 0.75f, 1f);
                    for (Player player : location.world.getBukkit().getPlayersSeeingChunk(location.toBukkit().getChunk())) {
                        player.sendBlockDamage(ore.getLocation(), Math.min(1, (breakingTicks) / 3f));
                    }
                }

                breakingTicks++;
                consumeEnergy(Quarry.ENERGY_CONSUMPTION_ON_MINE);
                return;
            }
        }

        if (hasSufficientEnergy(Quarry.ENERGY_CONSUMPTION_ON_MOVE)) {
            advance();
        }
    }

    @Override
    public void handleModelInit() {
        // TODO: Abstraction
        ((DisplayEntitiesPartModel) model.getPart("head")).parent.setTeleportDuration(10);
        ((DisplayEntitiesPartModel) model.getPart("feed")).parent.setTeleportDuration(10);
        ((DisplayEntitiesPartModel) model.getPart("drill_bit")).parent.setTeleportDuration(10);
        ((DisplayEntitiesPartModel) model.getPart("gantry_x")).parent.setTeleportDuration(10);
        ((DisplayEntitiesPartModel) model.getPart("gantry_z")).parent.setTeleportDuration(10);
    }

    public void advance() {
        byte z = (byte) (horizontalPosition.get() / 9);
        byte x = z % 2 == 0 ? (byte) (horizontalPosition.get() % 9) : (byte) (8 - horizontalPosition.get() % 9);
        if (!setDrillRelativePosition(new byte[]{ x, (byte) (-level.get()), (byte) (horizontalPosition.get() / 9) })) {
            return;
        }

        consumeEnergy(Quarry.ENERGY_CONSUMPTION_ON_MOVE);

        byte dir = level.get() % 2 == 0 ? 1 : (byte) (-1);
        horizontalPosition.update((byte) (horizontalPosition.get() + dir));

        if (dir == 1 && horizontalPosition.get() == 81) {
            level.update((byte) (level.get() + 1));
            horizontalPosition.update((byte) (horizontalPosition.get() - 1));
        }
        else if (dir == -1 && horizontalPosition.get() == -1) {
            level.update((byte) (level.get() + 1));
            horizontalPosition.update((byte) 0);
        }

        level.update((byte) Math.min(level.get(), Quarry.MAXIMUM_DEPTH));
        horizontalPosition.update((byte) (horizontalPosition.get() % 81));
    }

    public boolean setDrillRelativePosition(byte[] position0) {
        byte[] position = new byte[] {(byte) (-position0[0] + 4), position0[1], (byte) (position0[2] - 4)};
        position = MathUtils.rotateVectors(position, direction);

        BlockLocation nextDrillPosition = location.getRelative(position[0], position[1], position[2]);
        if (Quarry.BLOCK_BLACKLIST.contains(nextDrillPosition.getBlock().getType())) {
            return false;
        }

        drillPosition = nextDrillPosition;

        byte[] xMoverPosition = new byte[] {0, 0, (byte)(position0[2] - 4)};
        xMoverPosition = MathUtils.rotateVectors(xMoverPosition, direction);

        byte[] zMoverPosition = new byte[] {(byte) (-position0[0] + 4), 0, 0};
        zMoverPosition = MathUtils.rotateVectors(zMoverPosition, direction);

        model.getPart("gantry_x").teleport(location.toBukkit().add(.5, 0f, .5).add(xMoverPosition[0], xMoverPosition[1], xMoverPosition[2]));
        model.getPart("gantry_z").teleport(location.toBukkit().add(.5, 0f, .5).add(zMoverPosition[0], zMoverPosition[1], zMoverPosition[2]));

        updateDrillLength();

        model.getPart("feed").teleport(location.toBukkit().add(.5, .125f, .5).add(position[0], position[1], position[2]));
        model.getPart("drill_bit").teleport(location.toBukkit().add(.5, .125f, .5).add(position[0], position[1], position[2]));
        model.getPart("head").teleport(location.toBukkit().add(.5, 0, .5).add(position[0], 0, position[2]));

        return true;
    }

    public void updateDrillLength() {
        float f = .25f;
        int len = 3 + location.y - (drillPosition.y);

        drillLength.update(len);

        // TODO: Abstraction
        Display drillModel = ((DisplayEntitiesPartModel) model.getPart("feed")).entityReferences.getFirst().getEntity();
        Transformation transform = drillModel.getTransformation();
        transform.getTranslation().y = ((DisplayEntitiesPartModelTemplate) structure.getModel().partsByName.get("feed")).parts.getFirst().offset[1] + len / 2f - 1.5f;
        transform.getScale().y = len + f;
        drillModel.setTransformation(transform);
    }

    @Override
    protected void setup() {
        head = getComponent("head");
        feed = getComponent("feed");
        bit = getComponent("drill_bit");
        gantryX = getComponent("gantry_x");
        gantryZ = getComponent("gantry_z");

        inSocket = getSocket("in_energy");
        inSocket.maximumInput = Quarry.ENERGY_MAXIMUM_INPUT;
        outSocket = getSocket("out_item");
        outSocket.linkedInventory = outBuffer;
    }

    @Override
    public InspectableData setupInspectableData() {
        InspectableData data = new InspectableData((byte) 1);
        data.property("RF", InspectableData.TEMPLATE_RF.apply(this));

        byte[] controllerPosition = MathUtils.rotateVectors(new byte[] { 0, 0, -6 }, direction);
        data.inspectableBlocks.add(location.getRelative(controllerPosition[0], 0, controllerPosition[2]));
        return data;
    }

    @Override
    public int getEnergyCapacity() {
        return 15_000;
    }

}
