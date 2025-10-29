package com.github.alantr7.torus.recipe;

import com.github.alantr7.torus.item.ItemReference;

public class BlastFurnaceRecipe extends TorusRecipe {

    public final ItemReference[] ingredients;

    public final RecipeResult result;

    public final int smeltDuration;

    public BlastFurnaceRecipe(String id, ItemReference[] ingredients, RecipeResult result, int smeltDuration) {
        super(id);
        this.ingredients = ingredients;
        this.result = result;
        this.smeltDuration = smeltDuration;
    }

}
