package com.github.alantr7.torus.recipe;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.config.MainConfig;
import com.github.alantr7.torus.item.ItemReference;
import com.github.alantr7.torus.log.Category;
import com.github.alantr7.torus.log.TorusLogger;
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

public class RecipeLoader {

    public final File recipesDirectory;

    public RecipeLoader(File recipesDirectory) {
        this.recipesDirectory = recipesDirectory;
    }

    public void load() {
        File[] files = recipesDirectory.listFiles();
        if (files == null)
            return;

        for (File file : files) {
            if (file.getName().endsWith(".recipes.yml")) {
                handleRecipeFile(file);
            }
            else {
                TorusLogger.error(Category.RECIPES, "Config type for file not recognized: " + file.getName());
            }
        }
    }

    private void handleRecipeFile(File file) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (String recipeId : config.getKeys(false)) {
            ConfigurationSection section = config.getConfigurationSection(recipeId);
            String rawRecipeType = section.getString("type");

            if (rawRecipeType == null) {
                TorusLogger.error(Category.RECIPES, "Recipe type can not be null.");
                continue;
            }

            switch (rawRecipeType) {
                case "CRAFTING" ->  loadCraftingRecipe(section, recipeId);
                case "SMELTING" ->  loadSmeltingRecipe(section, recipeId);
                case "CRUSHING" ->  loadCrushingRecipe(section, recipeId);
                case "WASHING" ->   loadWasherRecipe(section, recipeId);
                case "BLASTING" ->  loadBlastFurnaceRecipe(section, recipeId);

                default -> TorusLogger.error(Category.RECIPES, "Invalid recipe type: " + rawRecipeType);
            }
        }
    }

    private void loadCraftingRecipe(ConfigurationSection section, String recipeId) {
        String rawResult = section.getString("result");
        if (rawResult == null) {
            TorusLogger.error(Category.RECIPES,  "Recipe result can not be null.");
            return;
        }

        ItemStack result = ItemReference.parse(rawResult).getItem();
        if (result == null) {
            TorusLogger.error(Category.RECIPES,  "Recipe result item not found.");
            return;
        }
        result.setAmount(section.getInt("amount", 1));

        List<String> rawShape = section.getStringList("shape");
        if (rawShape.isEmpty()) {
            TorusLogger.error(Category.RECIPES,  "Recipe shape can not be empty.");
            return;
        }

        Map<Character, ItemStack> ingredients = new HashMap<>();

        int size = rawShape.getFirst().length();
        if (size != 2 && size != 3) {
            TorusLogger.error(Category.RECIPES,  "Recipe shape must be 2x2 or 3x3.");
            return;
        }

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(TorusPlugin.getInstance(), recipeId), result);
        recipe.shape(rawShape.toArray(String[]::new));

        for (String row : rawShape) {
            if (row.length() != size) {
                TorusLogger.error(Category.RECIPES,  "Invalid recipe shape size.");
                return;
            }

            for (int i = 0; i < row.length(); i++) {
                char ch = row.charAt(i);
                if (ch == ' ' || ingredients.containsKey(ch))
                    continue;

                String rawReference = section.getString("ingredients." + ch);
                if (rawReference == null) {
                    TorusLogger.error(Category.RECIPES,  "Ingredient is not set for character: " + ch + ".");
                    return;
                }

                ItemStack ingredient = ItemReference.parse(rawReference).getItem();
                if (ingredient == null) {
                    TorusLogger.error(Category.RECIPES,  "Ingredient item not found.");
                    return;
                }

                ingredients.put(ch, ingredient);
                recipe.setIngredient(ch, new RecipeChoice.ExactChoice(ingredient));
            }
        }

        TorusPlugin.getInstance().getRecipeManager().registerBukkitRecipe(recipe);
        if (MainConfig.LOGS_RECIPE_LOAD) {
            TorusLogger.info(Category.RECIPES, "Loaded crafting recipe: " + recipeId);
        }
    }

    private void loadSmeltingRecipe(ConfigurationSection section, String recipeId) {
        String rawResult = section.getString("result");
        if (rawResult == null) {
            TorusLogger.error(Category.RECIPES,  "Recipe result can not be null.");
            return;
        }

        ItemStack result = ItemReference.parse(rawResult).getItem();
        if (result == null) {
            TorusLogger.error(Category.RECIPES,  "Recipe result item not found.");
            return;
        }
        result.setAmount(section.getInt("amount", 1));

        String rawIngredient = section.getString("ingredient");
        if (rawIngredient == null) {
            TorusLogger.error(Category.RECIPES,  "Ingredient can not be null.");
            return;
        }

        ItemReference ingredientReference = ItemReference.parse(rawIngredient);
        ItemStack ingredient = ingredientReference.getItem();
        if (ingredient == null) {
            TorusLogger.error(Category.RECIPES,  "Ingredient item can not be found.");
            return;
        }

        TorusPlugin.getInstance().getRecipeManager().registerBukkitRecipe(new FurnaceRecipe(
          new NamespacedKey(TorusPlugin.getInstance(), recipeId),
          result,
          new RecipeChoice.ExactChoice(ingredient), 0f, section.getInt("duration"))
        );
        if (MainConfig.LOGS_RECIPE_LOAD) {
            TorusLogger.info(Category.RECIPES, "Loaded smelting recipe: " + recipeId);
        }
    }

    private void loadCrushingRecipe(ConfigurationSection section, String recipeId) {
        String rawResult = section.getString("result");
        if (rawResult == null) {
            TorusLogger.error(Category.RECIPES, "Recipe result can not be null.");
            return;
        }

        ItemStack resultItem = ItemReference.parse(rawResult).getItem();
        if (resultItem == null) {
            TorusLogger.error(Category.RECIPES, "Recipe result item not found.");
            return;
        }

        String rawIngredient = section.getString("ingredient");
        if (rawIngredient == null) {
            TorusLogger.error(Category.RECIPES, "Ingredient can not be null.");
            return;
        }

        ItemReference ingredientReference = ItemReference.parse(rawIngredient);
        ItemStack ingredient = ingredientReference.getItem();
        if (ingredient == null) {
            TorusLogger.error(Category.RECIPES, "Ingredient item can not be found.");
            return;
        }

        short rangeMin, rangeMax;
        if (section.isSet("amount")) {
            if (section.isInt("amount")) {
                rangeMin = rangeMax = (short) section.getInt("amount");
            } else {
                rangeMin = (short) section.getInt("amount.min", 1);
                rangeMax = (short) section.getInt("amount.max", 1);
            }
        } else {
            rangeMin = 1;
            rangeMax = 1;
        }

        TorusPlugin.getInstance().getRecipeManager().registerCrusherRecipe(new CrusherRecipe(
          recipeId,
          ingredientReference,
          new RecipeResult(resultItem, rangeMin, rangeMax),
          section.getInt("duration")
        ));
        if (MainConfig.LOGS_RECIPE_LOAD) {
            TorusLogger.info(Category.RECIPES, "Loaded crushing recipe: " + recipeId);
        }
    }

    private void loadWasherRecipe(ConfigurationSection section, String recipeId) {
        String rawResult = section.getString("result");
        if (rawResult == null) {
            TorusLogger.error(Category.RECIPES, "Recipe result can not be null.");
            return;
        }

        ItemStack resultItem = ItemReference.parse(rawResult).getItem();
        if (resultItem == null) {
            TorusLogger.error(Category.RECIPES, "Recipe result item not found.");
            return;
        }

        short rangeMin, rangeMax;
        if (section.isSet("amount")) {
            if (section.isInt("amount")) {
                rangeMin = rangeMax = (short) section.getInt("amount");
            } else {
                rangeMin = (short) section.getInt("amount.min", 1);
                rangeMax = (short) section.getInt("amount.max", 1);
            }
        } else {
            rangeMin = 1;
            rangeMax = 1;
        }

        String rawIngredient = section.getString("ingredient");
        if (rawIngredient == null) {
            TorusLogger.error(Category.RECIPES, "Ingredient can not be null.");
            return;
        }

        ItemReference ingredientReference = ItemReference.parse(rawIngredient);
        ItemStack ingredient = ingredientReference.getItem();
        if (ingredient == null) {
            TorusLogger.error(Category.RECIPES, "Ingredient item can not be found.");
            return;
        }

        TorusPlugin.getInstance().getRecipeManager().registerWasherRecipe(new WasherRecipe(
          recipeId,
          ingredientReference,
          new RecipeResult(resultItem, rangeMin, rangeMax),
          section.getInt("duration")
        ));
        if (MainConfig.LOGS_RECIPE_LOAD) {
            TorusLogger.info(Category.RECIPES, "Loaded washing recipe: " + recipeId);
        }
    }

    private void loadBlastFurnaceRecipe(ConfigurationSection section, String recipeId) {
        String rawResult = section.getString("result");
        if (rawResult == null) {
            TorusLogger.error(Category.RECIPES, "Recipe result can not be null.");
            return;
        }

        ItemStack resultItem = ItemReference.parse(rawResult).getItem();
        if (resultItem == null) {
            TorusLogger.error(Category.RECIPES, "Recipe result item not found.");
            return;
        }

        short rangeMin, rangeMax;
        if (section.isSet("amount")) {
            if (section.isInt("amount")) {
                rangeMin = rangeMax = (short) section.getInt("amount");
            } else {
                rangeMin = (short) section.getInt("amount.min", 1);
                rangeMax = (short) section.getInt("amount.max", 1);
            }
        } else {
            rangeMin = 1;
            rangeMax = 1;
        }

        List<String> rawIngredients = section.getStringList("ingredients");
        if (rawIngredients.isEmpty() || rawIngredients.size() > 3) {
            TorusLogger.error(Category.RECIPES, "Ingredients count must be between 1 and 3.");
            return;
        }

        ItemReference[] ingredients = new ItemReference[rawIngredients.size()];
        for (int i = 0; i < rawIngredients.size(); i++) {
            String rawIngredient = rawIngredients.get(i);
            if (rawIngredient == null) {
                TorusLogger.error(Category.RECIPES, "Ingredient can not be null.");
                return;
            }

            ItemReference ingredientReference = ItemReference.parse(rawIngredient);
            ItemStack ingredient = ingredientReference.getItem();
            if (ingredient == null) {
                TorusLogger.error(Category.RECIPES, "Ingredient item can not be found.");
                return;
            }

            ingredients[i] = ingredientReference;
        }

        TorusPlugin.getInstance().getRecipeManager().registerBlastFurnaceRecipe(new BlastFurnaceRecipe(
          recipeId,
          ingredients,
          new RecipeResult(resultItem, rangeMin, rangeMax),
          section.getInt("duration")
        ));
        if (MainConfig.LOGS_RECIPE_LOAD) {
            TorusLogger.info(Category.RECIPES, "Loaded blasting recipe: " + recipeId);
        }
    }

}
