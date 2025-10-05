package com.github.alantr7.torus.recipe;

import org.bukkit.Material;

public class RecipeIngredient {

    public final String namespacedId;

    public final Material material;

    public RecipeIngredient(String namespacedId) {
        this.namespacedId = namespacedId;
        this.material = null;
    }

    public RecipeIngredient(Material material) {
        this.namespacedId = null;
        this.material = material;
    }

    public boolean isVanillaItem() {
        return material != null;
    }

    public boolean isTorusItem() {
        return namespacedId != null;
    }

}
