package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.world.Fluid;
import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.recipe.WasherRecipe;
import com.github.alantr7.torus.structure.*;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.component.Connector;
import com.github.alantr7.torus.structure.data.Data;
import com.github.alantr7.torus.structure.inventory.CustomStructureInventory;
import com.github.alantr7.torus.structure.inventory.StructureInventory;
import com.github.alantr7.torus.world.BlockLocation;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class OreWasherInstance extends StructureInstance implements EnergyContainer, FluidContainer, Inspectable {

    protected Data<Integer> water = dataContainer.persist("fluid", Data.Type.INT, 0);

    @Getter
    protected Data<Integer> storedEnergy = dataContainer.persist("energy", Data.Type.INT, 0);

    @Getter
    private int energyCapacity = 10_000;

    protected Connector powerConnector, itemInConnector, waterInConnector, itemOutConnector;

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
        powerConnector.maintainEnergy(this);
        itemOutConnector.attemptDirectItemExport();

        if (water.get() < 1_000) {
            waterInConnector.updateNetwork();
            int consumed = waterInConnector.consumeFluid(Fluid.WATER, 1000 - water.get());
            supplyFluid(consumed);
        }

        if (recipe != null) {
            if (!hasSufficientEnergy(OreWasher.ENERGY_CONSUMPTION_PER_TICK) || water.get() < 100) {
                return;
            }

            processedTicks++;
            consumeEnergy(OreWasher.ENERGY_CONSUMPTION_PER_TICK);
            consumeFluid(100);
        } else {
            itemInConnector.updateNetwork();

            List<ItemStack> items = itemInConnector.consumeItems(OreWasher.INPUT_CRITERIA, 1, true);
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
        powerConnector = getConnector("power_connector");
        powerConnector.maximumInput = 500;
        itemInConnector = getConnector("item_connector");
        waterInConnector = getConnector("fluid_connector");
        itemOutConnector = getConnector("out_connector");
        itemOutConnector.linkedInventory = itemOutBuffer;
    }

    @Override
    public String getInspectionText(BlockLocation location, Player player) {
        return String.format("Ore Washer [%d mb] [%d RF] [%s %.0f%%]", water.get(), getStoredEnergy().get(), recipe != null ? recipe.key : "No recipe", (float) processedTicks / (recipe == null ? 1 : recipe.washTicks) * 100);
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
