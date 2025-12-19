package com.github.alantr7.torus.api.addon;

import com.github.alantr7.torus.item.ItemLoader;
import com.github.alantr7.torus.item.ItemRegistry;
import com.github.alantr7.torus.recipe.RecipeLoader;
import com.github.alantr7.torus.recipe.TorusRecipeManager;
import com.github.alantr7.torus.structure.StructureRegistry;

public abstract class LifecycleAdapter {

    protected final TorusAddon addon;

    public LifecycleAdapter(TorusAddon addon) {
        this.addon = addon;
    }

    public void registerStructures(StructureRegistry registry) {}

    public void registerItems(ItemRegistry registry) {
        if (!addon.allowsExternalConfig(ConfigType.ITEMS))
            return;

        ItemLoader.load(addon);
    }

    public void registerRecipes(TorusRecipeManager registry) {
        if (!addon.allowsExternalConfig(ConfigType.RECIPES))
            return;

        new RecipeLoader(addon.recipesDirectory).load();
    }

}
