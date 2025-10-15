package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.math.Direction;
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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.List;

public class OreCrusherInstance extends StructureInstance implements Inspectable, EnergyContainer {

    protected StructureComponent leftWheel, rightWheel;
    protected Connector energyConnector, itemInConnector, itemOutConnector;

    protected CustomStructureInventory itemOutBuffer = new CustomStructureInventory(1);

    protected int processedTicks;

    @Getter
    protected Data<Integer> storedEnergy = dataContainer.persist("energy", Data.Type.INT, 0);

    @Getter
    protected int energyCapacity = 20_000;

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
                itemOutBuffer.addItem(recipe.result.clone());
                recipe = null;
                processedTicks = 0;
            } else {
                if (!hasSufficientEnergy(OreCrusher.ENERGY_CONSUMPTION_PER_TICK)) {
                    return;
                }

                processedTicks++;
                updateModel();
                consumeEnergy(OreCrusher.ENERGY_CONSUMPTION_PER_TICK);
            }
        }

    }

    private void updateModel() {
        float angle = (processedTicks * 120) / 180f * (float) Math.PI;
        leftWheel.getModel().entityReferences.forEach(ref -> {
            ItemDisplay display = ref.getEntity();
            Transformation transformation = display.getTransformation();
            display.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(angle * 0.9f, 0, 0, 1f), transformation.getScale(), new AxisAngle4f(transformation.getRightRotation())));
            display.setInterpolationDelay(0);
            display.setInterpolationDuration(20);
        });
        rightWheel.getModel().entityReferences.forEach(ref -> {
            ItemDisplay display = ref.getEntity();
            Transformation transformation = display.getTransformation();
            display.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(angle, 0, 0, 1f), transformation.getScale(), new AxisAngle4f(transformation.getRightRotation())));
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
        energyConnector.maximumInput = 500;
    }

    @Override
    public String getInspectionText(BlockLocation location, Player player) {
        return "Ore Crusher " + String.format("[%d RF] [%s %.2f%%]", getStoredEnergy().get(), recipe != null ? recipe.id : "No recipe", (float) processedTicks / (recipe != null ? recipe.crushTicks : 1) * 100);
    }

}
