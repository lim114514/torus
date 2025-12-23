package com.github.alantr7.torus.model;

import com.github.alantr7.torus.item.HeadData;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModelLoader {

    private static final Pattern ITEM_PATTERN = Pattern.compile("[a-zA-Z0-9_.\\-]+(\\[[a-zA-Z0-9_=,]+])?");

    private static final Pattern ATTRIBUTE_PATTERN = Pattern.compile("[a-z]+=[a-zA-Z0-9]+");

    public static ModelTemplate load(File file) {
        return load(YamlConfiguration.loadConfiguration(file));
    }

    public static ModelTemplate load(FileConfiguration yaml) {
        ModelTemplate template = new ModelTemplate(yaml.getInt("model_version", 1));

        for (String partName : yaml.getKeys(false)) {
            PartModelTemplate partModelTemplate = new PartModelTemplate(partName);
            List<String> elements = yaml.getStringList(partName);
            for (String rawElement : elements) {
                PartModelElementItemDisplayRenderer renderer = parseElement(rawElement);
                if (renderer != null) {
                    partModelTemplate.add(renderer);
                }
            }

            template.add(partModelTemplate);
        }

        return template;
    }

    public static PartModelElementItemDisplayRenderer parseElement(String raw) {
        Matcher matcher = ITEM_PATTERN.matcher(raw);
        nextString(raw, matcher);

        String rawMaterial = nextString(raw, matcher);
        Material material;

        Map<String, String> attributes = new HashMap<>();
        int attributesPosition = rawMaterial.indexOf("[");
        if (attributesPosition != -1) {
            material = Material.valueOf(rawMaterial.substring(0, attributesPosition).toUpperCase());

            Matcher attributeMatcher = ATTRIBUTE_PATTERN.matcher(rawMaterial.substring(attributesPosition + 1, rawMaterial.length() - 1));
            while (attributeMatcher.find()) {
                String attributeNameValue = rawMaterial.substring(attributesPosition + 1 + attributeMatcher.start(), attributesPosition + 1 + attributeMatcher.end());
                int attributeValueSeparatorPosition = attributeNameValue.indexOf("=");

                attributes.put(attributeNameValue.substring(0, attributeValueSeparatorPosition), attributeNameValue.substring(attributeValueSeparatorPosition + 1));
            }
        } else {
            material = Material.valueOf(rawMaterial.toUpperCase());
        }

        float[] offsetScaleRotation = {
          Float.parseFloat(nextString(raw, matcher)),
          Float.parseFloat(nextString(raw, matcher)),
          Float.parseFloat(nextString(raw, matcher)),

          Float.parseFloat(nextString(raw, matcher)),
          Float.parseFloat(nextString(raw, matcher)),
          Float.parseFloat(nextString(raw, matcher)),

          Float.parseFloat(nextString(raw, matcher)),
          Float.parseFloat(nextString(raw, matcher)),
          Float.parseFloat(nextString(raw, matcher)),
        };

        ItemStack itemStack = (material == Material.PLAYER_HEAD && attributes.containsKey("texture"))
          ? HeadData.create("http://textures.minecraft.net/texture/" + attributes.get("texture"))
          : new ItemStack(material);

        ItemMeta itemMeta = itemStack.getItemMeta();

        if (attributes.containsKey("model")) {
            CustomModelDataComponent customModelDataComponent = itemMeta.getCustomModelDataComponent();
            customModelDataComponent.setStrings(Collections.singletonList(attributes.get("model")));
            itemMeta.setCustomModelDataComponent(customModelDataComponent);
        }

        itemStack.setItemMeta(itemMeta);
        return new PartModelElementItemDisplayRenderer(itemStack, offsetScaleRotation);
    }

    private static String nextString(String string, Matcher matcher) {
        if (matcher.find())
            return string.substring(matcher.start(), matcher.end());

        return null;
    }

}
