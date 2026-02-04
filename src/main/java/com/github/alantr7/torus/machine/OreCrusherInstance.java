package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.exception.SetupException;
import com.github.alantr7.torus.structure.inspection.InspectableDataContainer;
import com.github.alantr7.torus.structure.socket.EnergySocket;
import com.github.alantr7.torus.structure.socket.ItemSocket;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.recipe.CrusherRecipe;
import com.github.alantr7.torus.structure.*;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.StructurePart;
import com.github.alantr7.torus.structure.data.Data;
import com.github.alantr7.torus.structure.inventory.CustomStructureInventory;
import com.github.alantr7.torus.world.BlockLocation;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static com.github.alantr7.torus.machine.OreCrusher.STATE_WORKING;

public class OreCrusherInstance extends StructureInstance implements Inspectable, EnergyContainer {

    protected StructurePart leftWheel, rightWheel;
    protected EnergySocket energySocket;
    protected ItemSocket itemInSocket, itemOutSocket;

    protected CustomStructureInventory itemOutBuffer = new CustomStructureInventory(1);

    protected int processedTicks;

    @Getter
    protected Data<Integer> storedEnergy = dataContainer.persist("energy", Data.Type.INT, 0);

    public float wheelsAngle;

    private CrusherRecipe recipe;

    OreCrusherInstance(LoadContext context) {
        super(context);
    }

    public OreCrusherInstance(BlockLocation location, StructureBodyDef bodyDef, Direction direction) {
        super(Structures.ORE_CRUSHER, location, bodyDef, direction);
    }

    @Override
    public void tick(boolean isVirtual) {
        energySocket.maintainEnergy(this);
        itemOutSocket.attemptDirectItemExport();

        if (recipe == null) {
            List<ItemStack> items = itemInSocket.consumeItems(OreCrusher.INPUT_CRITERIA, 1, true);
            if (!items.isEmpty()) {
                this.recipe = TorusPlugin.getInstance().getRecipeRegistry().getCrusherRecipeByIngredient(items.getFirst());
            }
            state.set(STATE_WORKING, false);
        } else {
            if (processedTicks >= recipe.crushTicks) {
                ItemStack result = recipe.result.asResult();
                itemOutBuffer.addItem(result);
                recipe = null;
                processedTicks = 0;
                state.set(STATE_WORKING, false);
            } else {
                if (!hasSufficientEnergy(OreCrusher.ENERGY_CONSUMPTION)) {
                    return;
                }

                processedTicks++;
                wheelsAngle += (120) / 180f * (float) Math.PI;
                state.set(STATE_WORKING, true);
                consumeEnergy(OreCrusher.ENERGY_CONSUMPTION);
            }
        }

    }

    @Override
    protected void setup() throws SetupException {
        leftWheel = getPart("wheel_left");
        rightWheel = getPart("wheel_right");

        itemInSocket = requireSocket("item_connector", ItemSocket.class);
        itemOutSocket = requireSocket("out_connector", ItemSocket.class);
        itemOutSocket.linkedInventory = itemOutBuffer;
        energySocket = requireSocket("power_connector", EnergySocket.class);
        energySocket.maximumInput = OreCrusher.ENERGY_MAXIMUM_INPUT;

        state.set(STATE_WORKING, false, false);
    }

    @Override
    public int getEnergyCapacity() {
        return OreCrusher.ENERGY_CONSUMPTION;
    }

    @Override
    public InspectableDataContainer setupInspectableData() {
        return new InspectableDataContainer((byte) 2)
          .property("RF", InspectableDataContainer.TEMPLATE_RF.apply(this))
          .property("Recipe", () -> recipe != null ? recipe.key.toString() : "(None)");
    }

}
