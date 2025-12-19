package com.github.alantr7.torus.recipe;

import com.github.alantr7.bukkitplugin.annotations.core.Singleton;
import com.github.alantr7.bukkitplugin.gui.GUI;
import com.github.alantr7.torus.gui.recipeview.ViewCraftingRecipeGUI;
import com.github.alantr7.torus.item.ItemCriteria;
import com.github.alantr7.torus.item.ItemReference;
import com.github.alantr7.torus.item.TorusItem;
import com.github.alantr7.torus.machine.OreCrusher;
import com.github.alantr7.torus.machine.OreWasher;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Singleton
public class TorusRecipeManager {

    private final List<Keyed> recipesRegisteredToBukkit = new ArrayList<>();

    private final Map<String, CrusherRecipe> crusherRecipes = new HashMap<>();

    private final Map<String, WasherRecipe> washerRecipes = new HashMap<>();

    private final Map<String, BlastFurnaceRecipe> blastFurnaceRecipes = new HashMap<>();

    public void clear() {
        for (Keyed recipe : recipesRegisteredToBukkit) {
            Bukkit.removeRecipe(recipe.getKey());
        }
        recipesRegisteredToBukkit.clear();
        crusherRecipes.clear();
        washerRecipes.clear();
        blastFurnaceRecipes.clear();

        OreCrusher.INPUT_CRITERIA = new ItemCriteria();
        OreWasher.INPUT_CRITERIA = new ItemCriteria();
    }

    private void trackTorusItemRecipe(ItemStack result, Keyed key) {
        TorusItem item = TorusItem.getByItemStack(result);
        if (item != null)
            item.getRecipes().add(key);
    }

    public <R extends Recipe & Keyed> void registerBukkitRecipe(R recipe) {
        Bukkit.removeRecipe(recipe.getKey());
        Bukkit.addRecipe(recipe);
        recipesRegisteredToBukkit.add(recipe);

        trackTorusItemRecipe(recipe.getResult(), recipe);
    }

    public CrusherRecipe getCrusherRecipeById(String id) {
        return crusherRecipes.get(id);
    }

    public CrusherRecipe getCrusherRecipeByIngredient(ItemStack ingredient) {
        ItemReference ingredientReference = ItemReference.create(ingredient);
        for (Map.Entry<String, CrusherRecipe> recipe : crusherRecipes.entrySet()) {
            ItemReference recipeIngredient = recipe.getValue().ingredient;
            if (recipeIngredient.equals(ingredientReference))
                return recipe.getValue();
        }

        return null;
    }

    public void registerCrusherRecipe(CrusherRecipe recipe) {
        crusherRecipes.put(recipe.key.toString(), recipe);
        if (recipe.ingredient.isVanillaItem()) {
            OreCrusher.INPUT_CRITERIA.materials.add(recipe.ingredient.getItem().getType());
        } else {
            OreCrusher.INPUT_CRITERIA.ids.add(recipe.ingredient.getNamespacedId());
        }

        trackTorusItemRecipe(recipe.result.item, recipe);
    }

    public WasherRecipe getWasherRecipeById(String id) {
        return washerRecipes.get(id);
    }

    public WasherRecipe getWasherRecipeByIngredient(ItemStack ingredient) {
        ItemReference ingredientReference = ItemReference.create(ingredient);
        for (Map.Entry<String, WasherRecipe> recipe : washerRecipes.entrySet()) {
            if (recipe.getValue().ingredient.equals(ingredientReference))
                return recipe.getValue();
        }

        return null;
    }

    public void registerWasherRecipe(WasherRecipe recipe) {
        washerRecipes.put(recipe.key.toString(), recipe);
        if (recipe.ingredient.isVanillaItem()) {
            OreWasher.INPUT_CRITERIA.materials.add(recipe.ingredient.getItem().getType());
        } else {
            OreWasher.INPUT_CRITERIA.ids.add(recipe.ingredient.getNamespacedId());
        }

        trackTorusItemRecipe(recipe.result.item, recipe);
    }

    public void registerBlastFurnaceRecipe(BlastFurnaceRecipe recipe) {
        blastFurnaceRecipes.put(recipe.key.toString(), recipe);
        trackTorusItemRecipe(recipe.result.item, recipe);
    }

    public Collection<BlastFurnaceRecipe> getBlastFurnaceRecipes() {
        return blastFurnaceRecipes.values();
    }

    @Nullable
    public <R extends Keyed> GUI createRecipeViewer(Player player, R recipe) {
        GUI viewer;
        if (recipe instanceof ShapedRecipe shaped) {
            viewer = new ViewCraftingRecipeGUI(shaped, player);
        } else {
            return null;
        }

        return viewer;
    }

}
