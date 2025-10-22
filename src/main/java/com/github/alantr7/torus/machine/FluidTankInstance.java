package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.world.Fluid;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.structure.*;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.component.Connector;
import com.github.alantr7.torus.structure.component.StructureComponent;
import com.github.alantr7.torus.structure.data.Data;
import org.bukkit.Material;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;

public class FluidTankInstance extends StructureInstance implements FluidContainer, Inspectable {

    protected StructureComponent liquidComponent;

    protected Connector input;

    protected Data<Integer> fluid = dataContainer.persist("fluid", Data.Type.INT, -1);

    protected Data<Integer> stored = dataContainer.persist("stored", Data.Type.INT, 0);

    FluidTankInstance(LoadContext context) {
        super(context);
    }

    public FluidTankInstance(BlockLocation location, StructureBodyDef bodyDef, Direction direction) {
        super(Structures.FLUID_TANK, location, bodyDef, direction);
    }

    @Override
    public void tick() {
        input.updateNetwork();
        if (input.networkConnections.isEmpty())
            return;

        if (stored.get() < getFluidCapacity()) {
            Fluid fluid = getFluid();
            if (fluid == null) {
                for (Fluid fluid1 : Fluid.values()) {
                    int consumed = input.consumeFluid(fluid1, 1000);
                    if (consumed != 0) {
                        supplyFluid(consumed);
                        this.fluid.update(fluid1.ordinal());
                        break;
                    }
                }
            } else {
                int consumed = input.consumeFluid(fluid, 1000);
                supplyFluid(consumed);
            }
        }

        updateLiquidModel();
    }

    public void updateLiquidModel() {
        float height = (float) stored.get() / getFluidCapacity() * 2.1f;
        float bottomOffset = 1.265f + height / 2;

        ItemDisplay display = liquidComponent.getModel().entityReferences.getFirst().getEntity();
        if ((display.getItemStack().getType() == Material.BLUE_CONCRETE && fluid.get() != Fluid.WATER.ordinal()) || (display.getItemStack().getType() == Material.ORANGE_CONCRETE && fluid.get() != Fluid.LAVA.ordinal())) {
            display.setItemStack(new ItemStack(fluid.get() == Fluid.WATER.ordinal() ? Material.BLUE_CONCRETE : Material.ORANGE_CONCRETE));
        }

        Transformation transformation = display.getTransformation();

        Vector3f scale = transformation.getScale();
        scale.y = height;

        display.setTransformation(transformation);
        display.teleport(location.getBlock().getLocation().add(.5f, bottomOffset, .5f));
    }

    @Override
    protected void setup() {
        liquidComponent = getComponent("liquid");
        input = getConnector("input");
    }

    @Override
    public Fluid getFluid() {
        return (fluid.get() == -1 || stored.get() == 0) ? null : Fluid.values()[fluid.get()];
    }

    @Override
    public int getFluidCapacity() {
        return 96_000;
    }

    @Override
    public int getStoredFluid() {
        return stored.get();
    }

    @Override
    public void setStoredFluid(int fluid) {
        stored.update(fluid);
        if (fluid == 0) {
            this.fluid.update(-1);
        }
    }

    @Override
    public String getInspectionText(BlockLocation location, Player player) {
        return "Fluid Tank [" + getStoredFluid() + " / " + getFluidCapacity() + " mb]";
    }

}
