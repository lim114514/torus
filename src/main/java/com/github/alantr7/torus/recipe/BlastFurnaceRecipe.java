package com.github.alantr7.torus.recipe;

import com.github.alantr7.torus.item.ItemReference;
import org.bukkit.inventory.ItemStack;

public class BlastFurnaceRecipe extends TorusRecipe {

    public final ItemReference[] ingredients;

    public final ItemStack result;

    public final int smeltDuration;

    public BlastFurnaceRecipe(String id, ItemReference[] ingredients, ItemStack result, int smeltDuration) {
        super(id);
        this.ingredients = ingredients;
        this.result = result;
        this.smeltDuration = smeltDuration;
    }

}
