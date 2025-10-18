package com.github.alantr7.torus.recipe;

import com.github.alantr7.torus.item.ItemReference;

public class CrusherRecipe extends TorusRecipe {

    public final ItemReference ingredient;

    public final RecipeResult result;

    public final int crushTicks;

    public CrusherRecipe(String id, ItemReference ingredient, RecipeResult result, int crushTicks) {
        super(id);
        this.ingredient = ingredient;
        this.result = result;
        this.crushTicks = crushTicks;
    }

}
