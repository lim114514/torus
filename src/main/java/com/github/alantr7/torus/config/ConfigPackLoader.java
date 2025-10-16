package com.github.alantr7.torus.config;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.item.ItemReference;
import com.github.alantr7.torus.recipe.CrusherRecipe;
import com.github.alantr7.torus.recipe.RecipeIngredient;
import com.github.alantr7.torus.recipe.WasherRecipe;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.io.File;
import java.util.*;

public class ConfigPackLoader {

    public final File packDirectory;

    public ConfigPackLoader(File packDirectory) {
        this.packDirectory = packDirectory;
    }

    public void load() {
        File[] files = packDirectory.listFiles();
        if (files == null)
            return;

        for (File file : files) {
            if (file.getName().endsWith(".recipes.yml")) {
                handleRecipeFile(file);
            }
            else {
                System.err.println("Config type for file not recognized: " + file.getName());
            }
        }
    }

    private void handleRecipeFile(File file) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (String recipeId : config.getKeys(false)) {
            ConfigurationSection section = config.getConfigurationSection(recipeId);
            String rawRecipeType = section.getString("type");

            if (rawRecipeType == null) {
                System.err.println("Recipe type can not be null.");
                continue;
            }

            switch (rawRecipeType) {
                case "CRAFTING" -> loadCraftingRecipe(section, recipeId);
                case "SMELTING" -> loadSmeltingRecipe(section, recipeId);
                case "CRUSHING" -> loadCrushingRecipe(section, recipeId);
                case "WASHING" -> loadWasherRecipe(section, recipeId);

                default -> System.err.println("Invalid recipe type: " + rawRecipeType);
            }

        }
        System.out.println("Recipes loaded!");
    }

    private void loadCraftingRecipe(ConfigurationSection section, String recipeId) {
        String rawResult = section.getString("result");
        if (rawResult == null) {
            System.err.println("Recipe result can not be null.");
            return;
        }

        ItemStack result = ItemReference.parse(rawResult).getItem();
        if (result == null) {
            System.err.println("Recipe result item not found.");
            return;
        }
        result.setAmount(section.getInt("amount", 1));

        List<String> rawShape = section.getStringList("shape");
        if (rawShape.isEmpty()) {
            System.err.println("Recipe shape can not be empty.");
            return;
        }

        Map<Character, ItemStack> ingredients = new HashMap<>();

        int size = rawShape.getFirst().length();
        if (size != 2 && size != 3) {
            System.err.println("Recipe shape must be 2x2 or 3x3.");
            return;
        }

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(TorusPlugin.getInstance(), recipeId), result);
        recipe.shape(rawShape.toArray(String[]::new));

        for (String row : rawShape) {
            if (row.length() != size) {
                System.err.println("Invalid recipe shape size.");
                return;
            }

            for (int i = 0; i < row.length(); i++) {
                char ch = row.charAt(i);
                if (ch == ' ' || ingredients.containsKey(ch))
                    continue;

                String rawReference = section.getString("ingredients." + ch);
                if (rawReference == null) {
                    System.err.println("Ingredient is not set for character: " + ch + ".");
                    return;
                }

                ItemStack ingredient = ItemReference.parse(rawReference).getItem();
                if (ingredient == null) {
                    System.err.println("Ingredient item not found.");
                    return;
                }

                ingredients.put(ch, ingredient);
                recipe.setIngredient(ch, new RecipeChoice.ExactChoice(ingredient));
            }
        }

        try {
            Bukkit.removeRecipe(recipe.getKey());
            Bukkit.addRecipe(recipe);
            System.out.println("Loaded recipe: " + recipeId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadSmeltingRecipe(ConfigurationSection section, String recipeId) {
        String rawResult = section.getString("result");
        if (rawResult == null) {
            System.err.println("Recipe result can not be null.");
            return;
        }

        ItemStack result = ItemReference.parse(rawResult).getItem();
        if (result == null) {
            System.err.println("Recipe result item not found.");
            return;
        }
        result.setAmount(section.getInt("amount", 1));

        String rawIngredient = section.getString("ingredient");
        if (rawIngredient == null) {
            System.err.println("Ingredient can not be null.");
            return;
        }

        ItemReference ingredientReference = ItemReference.parse(rawIngredient);
        ItemStack ingredient = ingredientReference.getItem();
        if (ingredient == null) {
            System.err.println("Ingredient item can not be found.");
            return;
        }

        TorusPlugin.getInstance().getRecipeManager().registerSmeltingRecipe(new FurnaceRecipe(
          new NamespacedKey(TorusPlugin.getInstance(), recipeId),
          result,
          new RecipeChoice.ExactChoice(ingredient), 0f, section.getInt("duration"))
        );
        System.out.println("Loaded smelting recipe: " + recipeId);
    }

    private void loadCrushingRecipe(ConfigurationSection section, String recipeId) {
        String rawResult = section.getString("result");
        if (rawResult == null) {
            System.err.println("Recipe result can not be null.");
            return;
        }

        ItemStack result = ItemReference.parse(rawResult).getItem();
        if (result == null) {
            System.err.println("Recipe result item not found.");
            return;
        }
        result.setAmount(section.getInt("amount", 1));

        String rawIngredient = section.getString("ingredient");
        if (rawIngredient == null) {
            System.err.println("Ingredient can not be null.");
            return;
        }

        ItemReference ingredientReference = ItemReference.parse(rawIngredient);
        ItemStack ingredient = ingredientReference.getItem();
        if (ingredient == null) {
            System.err.println("Ingredient item can not be found.");
            return;
        }

        TorusPlugin.getInstance().getRecipeManager().registerCrusherRecipe(new CrusherRecipe(
          recipeId,
          ingredientReference.providerId.equals("minecraft")
            ? new RecipeIngredient(ingredientReference.getItem().getType())
            : new RecipeIngredient(ingredientReference.providerId + ":" + ingredientReference.itemId),
          result,
          section.getInt("duration")
        ));
        System.out.println("Loaded recipe: " + recipeId);
    }

    private void loadWasherRecipe(ConfigurationSection section, String recipeId) {
        String rawResult = section.getString("result");
        if (rawResult == null) {
            System.err.println("Recipe result can not be null.");
            return;
        }

        ItemStack result = ItemReference.parse(rawResult).getItem();
        if (result == null) {
            System.err.println("Recipe result item not found.");
            return;
        }
        result.setAmount(section.getInt("amount", 1));

        String rawIngredient = section.getString("ingredient");
        if (rawIngredient == null) {
            System.err.println("Ingredient can not be null.");
            return;
        }

        ItemReference ingredientReference = ItemReference.parse(rawIngredient);
        ItemStack ingredient = ingredientReference.getItem();
        if (ingredient == null) {
            System.err.println("Ingredient item can not be found.");
            return;
        }

        TorusPlugin.getInstance().getRecipeManager().registerWasherRecipe(new WasherRecipe(
          recipeId,
          ingredientReference.providerId.equals("minecraft")
            ? new RecipeIngredient(ingredientReference.getItem().getType())
            : new RecipeIngredient(ingredientReference.providerId + ":" + ingredientReference.itemId),
          result,
          section.getInt("duration")
        ));
        System.out.println("Loaded recipe: " + recipeId);
    }

}
