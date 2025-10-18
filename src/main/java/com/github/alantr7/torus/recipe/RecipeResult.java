package com.github.alantr7.torus.recipe;

import org.bukkit.inventory.ItemStack;

public class RecipeResult {

    public final ItemStack item;

    public short rangeMin;

    public short rangeMax;

    public RecipeResult(ItemStack item, short rangeMin, short rangeMax) {
        this.item = item;
        this.rangeMin = rangeMin;
        this.rangeMax = rangeMax;
    }

    public RecipeResult(ItemStack item, short amount) {
        this(item, amount, amount);
    }

    public ItemStack asResult() {
        ItemStack result = item.clone();
        result.setAmount(Math.max(0, (int) (rangeMin + Math.floor((rangeMax - rangeMin) * Math.random()))));

        return result;
    }

}
