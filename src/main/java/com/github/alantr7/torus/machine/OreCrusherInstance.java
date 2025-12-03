package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.structure.inspection.InspectableData;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.recipe.CrusherRecipe;
import com.github.alantr7.torus.structure.*;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.component.Connector;
import com.github.alantr7.torus.structure.component.StructureComponent;
import com.github.alantr7.torus.structure.data.Data;
import com.github.alantr7.torus.structure.inventory.CustomStructureInventory;
import com.github.alantr7.torus.world.BlockLocation;
import lombok.Getter;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;

import java.util.List;

public class OreCrusherInstance extends StructureInstance implements Inspectable, EnergyContainer {

    protected StructureComponent leftWheel, rightWheel;
    protected Connector energyConnector, itemInConnector, itemOutConnector;

    protected CustomStructureInventory itemOutBuffer = new CustomStructureInventory(1);

    protected int processedTicks;

    @Getter
    protected Data<Integer> storedEnergy = dataContainer.persist("energy", Data.Type.INT, 0);

    protected float angle;

    private CrusherRecipe recipe;

    OreCrusherInstance(LoadContext context) {
        super(context);
    }

    public OreCrusherInstance(BlockLocation location, StructureBodyDef bodyDef, Direction direction) {
        super(Structures.ORE_CRUSHER, location, bodyDef, direction);
    }

    @Override
    public void tick() {
        energyConnector.maintainEnergy(this);
        itemOutConnector.attemptDirectItemExport();

        if (recipe == null) {
            itemInConnector.updateNetwork();
            List<ItemStack> items = itemInConnector.consumeItems(OreCrusher.INPUT_CRITERIA, 1, true);
            if (!items.isEmpty()) {
                this.recipe = TorusPlugin.getInstance().getRecipeManager().getCrusherRecipeByIngredient(items.getFirst());
            }
        } else {
            if (processedTicks >= recipe.crushTicks) {
                ItemStack result = recipe.result.asResult();
                itemOutBuffer.addItem(result);
                recipe = null;
                processedTicks = 0;
            } else {
                if (!hasSufficientEnergy(OreCrusher.ENERGY_CONSUMPTION)) {
                    return;
                }

                processedTicks++;
                angle += (120) / 180f * (float) Math.PI;
                updateModel();
                consumeEnergy(OreCrusher.ENERGY_CONSUMPTION);
            }
        }

    }

    public void updateModel() {
        model.getPart("wheel_left").entityReferences.forEach(ref -> {
            ItemDisplay display = ref.getEntity();
            Transformation transformation = display.getTransformation();
            display.setTransformation(new Transformation(transformation.getTranslation(), new AxisAngle4f(angle * 0.9f, 0, 0, 1f), transformation.getScale(), new AxisAngle4f(transformation.getRightRotation())));
            display.setInterpolationDelay(0);
            display.setInterpolationDuration(20);
        });
        model.getPart("wheel_right").entityReferences.forEach(ref -> {
            ItemDisplay display = ref.getEntity();
            Transformation transformation = display.getTransformation();
            display.setTransformation(new Transformation(transformation.getTranslation(), new AxisAngle4f(angle, 0, 0, 1f), transformation.getScale(), new AxisAngle4f(transformation.getRightRotation())));
            display.setInterpolationDelay(0);
            display.setInterpolationDuration(20);
        });
    }

    @Override
    protected void setup() {
        leftWheel = getComponent("wheel_left");
        rightWheel = getComponent("wheel_right");

        itemInConnector = getConnector("item_connector");
        itemOutConnector = getConnector("out_connector");
        itemOutConnector.linkedInventory = itemOutBuffer;
        energyConnector = getConnector("power_connector");
        energyConnector.maximumInput = OreCrusher.ENERGY_MAXIMUM_INPUT;
    }

    @Override
    public int getEnergyCapacity() {
        return OreCrusher.ENERGY_CONSUMPTION;
    }

    @Override
    public InspectableData setupInspectableData() {
        return new InspectableData((byte) 2)
          .property("RF", InspectableData.TEMPLATE_RF.apply(this))
          .property("Recipe", () -> recipe != null ? recipe.key.toString() : "(None)");
    }

}
