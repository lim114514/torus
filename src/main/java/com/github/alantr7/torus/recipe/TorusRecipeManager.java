package com.github.alantr7.torus.recipe;

import com.github.alantr7.bukkitplugin.annotations.core.Invoke;
import com.github.alantr7.bukkitplugin.annotations.core.Singleton;
import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.item.ItemReference;
import com.github.alantr7.torus.item.TorusItem;
import com.github.alantr7.torus.machine.OreCrusher;
import com.github.alantr7.torus.machine.OreWasher;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class TorusRecipeManager {

    private final Map<String, CrusherRecipe> crusherRecipes = new HashMap<>();

    private final Map<String, WasherRecipe> washerRecipes = new HashMap<>();

    private final Map<String, BlastFurnaceRecipe> blastFurnaceRecipes = new HashMap<>();

    public CrusherRecipe getCrusherRecipeById(String id) {
        return crusherRecipes.get(id);
    }

    public CrusherRecipe getCrusherRecipeByIngredient(ItemStack ingredient) {
        TorusItem torusIngredient = TorusItem.getByItemStack(ingredient);
        for (Map.Entry<String, CrusherRecipe> recipe : crusherRecipes.entrySet()) {
            RecipeIngredient recipeIngredient = recipe.getValue().ingredient;
            if (recipeIngredient.isVanillaItem()) {
                if (recipeIngredient.material == ingredient.getType())
                    return recipe.getValue();
            }

            else if (torusIngredient != null && torusIngredient.namespacedId.equals(recipe.getValue().ingredient.namespacedId)) {
                return recipe.getValue();
            }
        }

        return null;
    }

    public void registerCrusherRecipe(CrusherRecipe recipe) {
        crusherRecipes.put(recipe.id, recipe);
        if (recipe.ingredient.isVanillaItem()) {
            OreCrusher.INPUT_CRITERIA.materials.add(recipe.ingredient.material);
        } else {
            OreCrusher.INPUT_CRITERIA.ids.add(recipe.ingredient.namespacedId);
        }
    }

    public WasherRecipe getWasherRecipeById(String id) {
        return washerRecipes.get(id);
    }

    public WasherRecipe getWasherRecipeByIngredient(ItemStack ingredient) {
        TorusItem torusIngredient = TorusItem.getByItemStack(ingredient);
        for (Map.Entry<String, WasherRecipe> recipe : washerRecipes.entrySet()) {
            RecipeIngredient recipeIngredient = recipe.getValue().ingredient;
            if (recipeIngredient.isVanillaItem()) {
                if (recipeIngredient.material == ingredient.getType())
                    return recipe.getValue();
            }

            else if (torusIngredient != null && torusIngredient.namespacedId.equals(recipe.getValue().ingredient.namespacedId)) {
                return recipe.getValue();
            }
        }

        return null;
    }

    public void registerWasherRecipe(WasherRecipe recipe) {
        washerRecipes.put(recipe.id, recipe);
        if (recipe.ingredient.isVanillaItem()) {
            OreWasher.INPUT_CRITERIA.materials.add(recipe.ingredient.material);
        } else {
            OreWasher.INPUT_CRITERIA.ids.add(recipe.ingredient.namespacedId);
        }
    }

    public void registerSmeltingRecipe(FurnaceRecipe recipe) {
        if (Bukkit.getRecipe(recipe.getKey()) != null) {
            Bukkit.removeRecipe(recipe.getKey());
        }

        Bukkit.addRecipe(recipe);
    }

    public void registerBlastFurnaceRecipe(BlastFurnaceRecipe recipe) {
        blastFurnaceRecipes.put(recipe.id, recipe);
    }

    public Collection<BlastFurnaceRecipe> getBlastFurnaceRecipes() {
        return blastFurnaceRecipes.values();
    }

    @Invoke(Invoke.Schedule.AFTER_PLUGIN_ENABLE)
    private void registerDefaultRecipes() {
        registerBlastFurnaceRecipe(new BlastFurnaceRecipe("torus:steel_ingot", new ItemReference[]{ new ItemReference("minecraft", "COAL"), new ItemReference("minecraft", "IRON_INGOT") }, TorusItem.getById("torus:steel_ingot").toItemStack(), 10));
    }

}
