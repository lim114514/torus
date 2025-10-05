package com.github.alantr7.torus.recipe;

import com.github.alantr7.bukkitplugin.annotations.core.Invoke;
import com.github.alantr7.bukkitplugin.annotations.core.Singleton;
import com.github.alantr7.torus.item.TorusItem;
import com.github.alantr7.torus.machine.OreCrusher;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class TorusRecipeManager {

    private final Map<String, CrusherRecipe> crusherRecipes = new HashMap<>();

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

            else if (torusIngredient == null) {
                continue;
            }

            else if (torusIngredient.namespacedId.equals(recipe.getValue().ingredient.namespacedId)) {
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

    @Invoke(Invoke.Schedule.AFTER_PLUGIN_ENABLE)
    private void registerDefaultRecipes() {
        registerCrusherRecipe(new CrusherRecipe("torus:crushed_iron", new RecipeIngredient(Material.IRON_ORE), new ItemStack(Material.RAW_IRON, 2), 4));
        registerCrusherRecipe(new CrusherRecipe("torus:crushed_gold", new RecipeIngredient(Material.GOLD_ORE), new ItemStack(Material.RAW_GOLD, 2), 8));
    }

}
