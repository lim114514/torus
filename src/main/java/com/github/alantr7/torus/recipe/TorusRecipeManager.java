package com.github.alantr7.torus.recipe;

import com.github.alantr7.bukkitplugin.annotations.core.Invoke;
import com.github.alantr7.bukkitplugin.annotations.core.Singleton;
import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.item.TorusItem;
import com.github.alantr7.torus.machine.OreCrusher;
import com.github.alantr7.torus.machine.OreWasher;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class TorusRecipeManager {

    private final Map<String, CrusherRecipe> crusherRecipes = new HashMap<>();

    private final Map<String, WasherRecipe> washerRecipes = new HashMap<>();

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
        if (Bukkit.getRecipe(recipe.getKey()) != null)
            return;

        Bukkit.addRecipe(recipe);
    }

    @Invoke(Invoke.Schedule.AFTER_PLUGIN_ENABLE)
    private void registerDefaultRecipes() {
        registerCrusherRecipe(new CrusherRecipe("torus:crushed_coal", new RecipeIngredient(Material.COAL_ORE), new ItemStack(Material.COAL, 5), 2));
        registerCrusherRecipe(new CrusherRecipe("torus:crushed_copper", new RecipeIngredient(Material.COPPER_ORE), new ItemStack(Material.RAW_COPPER, 2), 5));
        registerCrusherRecipe(new CrusherRecipe("torus:crushed_iron", new RecipeIngredient(Material.IRON_ORE), new ItemStack(Material.RAW_IRON, 2), 5));
        registerCrusherRecipe(new CrusherRecipe("torus:crushed_gold", new RecipeIngredient(Material.GOLD_ORE), new ItemStack(Material.RAW_GOLD, 2), 7));

        registerWasherRecipe(new WasherRecipe("torus:iron_dust", new RecipeIngredient(Material.RAW_IRON), TorusItem.getById("torus:iron_dust").toItemStack(), 8));
        registerWasherRecipe(new WasherRecipe("torus:copper_dust", new RecipeIngredient(Material.RAW_COPPER), TorusItem.getById("torus:copper_dust").toItemStack(), 8));
        registerWasherRecipe(new WasherRecipe("torus:gold_dust", new RecipeIngredient(Material.RAW_GOLD), TorusItem.getById("torus:gold_dust").toItemStack(), 10));

        registerSmeltingRecipe(new FurnaceRecipe(new NamespacedKey(TorusPlugin.getInstance(), "iron_dust_to_ingot"), new ItemStack(Material.IRON_INGOT), new RecipeChoice.ExactChoice(TorusItem.getById("torus:iron_dust").toItemStack()), 0f, 60));
        registerSmeltingRecipe(new FurnaceRecipe(new NamespacedKey(TorusPlugin.getInstance(), "copper_dust_to_ingot"), new ItemStack(Material.COPPER_INGOT), new RecipeChoice.ExactChoice(TorusItem.getById("torus:copper_dust").toItemStack()), 0f, 60));
        registerSmeltingRecipe(new FurnaceRecipe(new NamespacedKey(TorusPlugin.getInstance(), "gold_dust_to_ingot"), new ItemStack(Material.GOLD_INGOT), new RecipeChoice.ExactChoice(TorusItem.getById("torus:gold_dust").toItemStack()), 0f, 60));
    }

}
