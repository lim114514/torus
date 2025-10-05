package com.github.alantr7.torus.recipe;

import org.bukkit.inventory.ItemStack;

public class WasherRecipe {

    public final String id;

    public final RecipeIngredient ingredient;

    public final ItemStack result;

    public final int washTicks;

    public WasherRecipe(String id, RecipeIngredient ingredient, ItemStack result, int washTicks) {
        this.id = id;
        this.ingredient = ingredient;
        this.result = result;
        this.washTicks = washTicks;
    }

}
