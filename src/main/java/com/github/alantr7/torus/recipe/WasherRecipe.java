package com.github.alantr7.torus.recipe;

import com.github.alantr7.torus.item.ItemReference;
import org.bukkit.inventory.ItemStack;

public class WasherRecipe extends TorusRecipe {

    public final ItemReference ingredient;

    public final RecipeResult result;

    public final int washTicks;

    public WasherRecipe(String id, ItemReference ingredient, RecipeResult result, int washTicks) {
        super(id);
        this.ingredient = ingredient;
        this.result = result;
        this.washTicks = washTicks;
    }

}
