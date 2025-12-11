package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.world.Fluid;
import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.recipe.WasherRecipe;
import com.github.alantr7.torus.structure.*;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.component.Socket;
import com.github.alantr7.torus.structure.data.Data;
import com.github.alantr7.torus.structure.inventory.CustomStructureInventory;
import com.github.alantr7.torus.structure.inventory.StructureInventory;
import com.github.alantr7.torus.world.BlockLocation;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class OreWasherInstance extends StructureInstance implements EnergyContainer, FluidContainer, Inspectable {

    protected Data<Integer> water = dataContainer.persist("fluid", Data.Type.INT, 0);

    @Getter
    protected Data<Integer> storedEnergy = dataContainer.persist("energy", Data.Type.INT, 0);

    protected Socket powerSocket, itemInSocket, waterInSocket, itemOutSocket;

    protected StructureInventory itemOutBuffer = new CustomStructureInventory(1);

    protected int processedTicks;

    private WasherRecipe recipe;

    public OreWasherInstance(LoadContext context) {
        super(context);
    }

    public OreWasherInstance(BlockLocation location, StructureBodyDef bodyDef, Direction direction) {
        super(Structures.ORE_WASHER, location, bodyDef, direction);
    }

    @Override
    public void tick() {
        powerSocket.maintainEnergy(this);
        itemOutSocket.attemptDirectItemExport();

        if (water.get() < OreWasher.FLUID_CAPACITY) {
            waterInSocket.updateNetwork();
            int consumed = waterInSocket.consumeFluid(Fluid.WATER, OreWasher.FLUID_CAPACITY - water.get());
            supplyFluid(consumed);
        }

        if (recipe != null) {
            if (!hasSufficientEnergy(OreWasher.ENERGY_CONSUMPTION) || water.get() < OreWasher.FLUID_CONSUMPTION) {
                return;
            }

            processedTicks++;
            consumeEnergy(OreWasher.ENERGY_CONSUMPTION);
            consumeFluid(OreWasher.FLUID_CONSUMPTION);
        } else {
            itemInSocket.updateNetwork();

            List<ItemStack> items = itemInSocket.consumeItems(OreWasher.INPUT_CRITERIA, 1, true);
            if (!items.isEmpty()) {
                recipe = TorusPlugin.getInstance().getRecipeManager().getWasherRecipeByIngredient(items.getFirst());
            }
        }

        if (recipe != null && processedTicks >= recipe.washTicks) {
            itemOutBuffer.addItem(recipe.result.asResult());
            recipe = null;
            processedTicks = 0;
        }
    }

    @Override
    protected void setup() {
        powerSocket = getSocket("power_connector");
        powerSocket.maximumInput = OreWasher.ENERGY_MAXIMUM_INPUT;
        itemInSocket = getSocket("item_connector");
        waterInSocket = getSocket("fluid_connector");
        itemOutSocket = getSocket("out_connector");
        itemOutSocket.linkedInventory = itemOutBuffer;
    }

    @Override
    public int getEnergyCapacity() {
        return OreWasher.ENERGY_CAPACITY;
    }

    @Override
    public @Nullable Fluid getFluid() {
        return Fluid.WATER;
    }

    @Override
    public int getFluidCapacity() {
        return 1_000;
    }

    @Override
    public int getStoredFluid() {
        return water.get();
    }

    @Override
    public void setStoredFluid(int fluid) {
        water.update(fluid);
    }

}
