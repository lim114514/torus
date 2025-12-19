package com.github.alantr7.torus.item;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.api.addon.TorusAddon;
import com.github.alantr7.torus.log.TorusLogger;
import com.github.alantr7.torus.structure.Structure;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemLoader {

    public static void load(TorusAddon addon) {
        File[] files = addon.itemsDirectory.listFiles();
        if (files == null)
            return;

        for (File file : files) {
            FileConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            for (String itemId : yaml.getKeys(false)) {
                ConfigurationSection data = yaml.getConfigurationSection(itemId);
                if (!data.isString("base")) {
                    TorusLogger.error(com.github.alantr7.torus.log.Category.ITEMS, "Base item is not set.");
                    continue;
                }

                ItemReference base = ItemReference.parse(data.getString("base"));
                String headTexture = data.getString("head_texture_url");
                String name = data.getString("name");
                String structureId = data.getString("structure");
                List<String> categoriesRaw = data.getStringList("categories");
                List<String> lore = data.getStringList("lore");

                ItemStack baseItem = base.getItem();
                if (baseItem == null) {
                    TorusLogger.error(com.github.alantr7.torus.log.Category.ITEMS, "Base item not found.");
                    continue;
                }

                List<Category> categories = new ArrayList<>();
                for (String categoryId : categoriesRaw) {
                    Category category = TorusPlugin.getInstance().getItemRegistry().getCategory(categoryId);
                    if (category == null) {
                        TorusLogger.error(com.github.alantr7.torus.log.Category.ITEMS, "Invalid category: " + categoryId);
                        continue;
                    }

                    categories.add(category);
                }

                Structure structure;
                if (structureId != null) {
                    structure = TorusPlugin.getInstance().getStructureRegistry().getStructure(structureId);
                    if (structure == null) {
                        TorusLogger.error(com.github.alantr7.torus.log.Category.ITEMS, "Structure not found.");
                        continue;
                    }
                } else {
                    structure = null;
                }

                TorusItem item = baseItem.getType() == Material.PLAYER_HEAD
                  ? new TorusItem(addon, itemId, categories.toArray(Category[]::new), structure, new HeadData(headTexture), name, lore)
                  : new TorusItem(addon, itemId, categories.toArray(Category[]::new), structure, base.getItem(), name, lore);

                TorusPlugin.getInstance().getItemRegistry().registerItem(item);
            }
        }
    }

}
