package com.github.alantr7.torus.recipe;

import org.bukkit.inventory.ItemStack;

public class CrusherRecipe {

    public final String id;

    public final RecipeIngredient ingredient;

    public final ItemStack result;

    public final int crushTicks;

    public CrusherRecipe(String id, RecipeIngredient ingredient, ItemStack result, int crushTicks) {
        this.id = id;
        this.ingredient = ingredient;
        this.result = result;
        this.crushTicks = crushTicks;
    }

}
