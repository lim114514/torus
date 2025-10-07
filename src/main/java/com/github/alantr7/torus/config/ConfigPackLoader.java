package com.github.alantr7.torus.config;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.item.ItemReference;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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

            if (rawRecipeType.equals("CRAFTING")) {
                String rawResult = section.getString("result");
                List<String> rawShape = section.getStringList("shape");

                if (rawResult == null) {
                    System.err.println("Recipe result can not be null.");
                    continue;
                }

                ItemStack result = ItemReference.parse(rawResult).getItem();
                if (result == null) {
                    System.err.println("Recipe result item not found.");
                    continue;
                }

                if (rawShape.isEmpty()) {
                    System.err.println("Recipe shape can not be empty.");
                    continue;
                }

                Map<Character, ItemStack> ingredients = new HashMap<>();

                int size = rawShape.getFirst().length();
                if (size != 2 && size != 3) {
                    System.err.println("Recipe shape must be 2x2 or 3x3.");
                    continue;
                }

                ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(TorusPlugin.getInstance(), recipeId), result);
                recipe.shape(rawShape.toArray(String[]::new));

                for (String row : rawShape) {
                    if (row.length() != size) {
                        System.err.println("Invalid recipe shape size.");
                        continue;
                    }

                    for (int i = 0; i < row.length(); i++) {
                        char ch = row.charAt(i);
                        if (ch == ' ' || ingredients.containsKey(ch))
                            continue;

                        String rawReference = section.getString("ingredients." + ch);
                        if (rawReference == null) {
                            System.err.println("Ingredient is not set for character: " + ch + ".");
                            continue;
                        }

                        ItemStack ingredient = ItemReference.parse(rawReference).getItem();
                        if (ingredient == null) {
                            System.err.println("Ingredient item not found.");
                            continue;
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

            else {
                System.err.println("Invalid recipe type: " + rawRecipeType);
                continue;
            }

        }
        System.out.println("Recipes loaded!");
    }

}
