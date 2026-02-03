package com.github.alantr7.torus.model.de_provider;

import com.github.alantr7.torus.item.HeadData;
import com.github.alantr7.torus.log.Category;
import com.github.alantr7.torus.log.TorusLogger;
import com.github.alantr7.torus.model.PartModel;
import com.github.alantr7.torus.model.PartModelTemplate;
import com.github.alantr7.torus.model.RendererConfigLoader;
import com.github.alantr7.torus.model.animation.Animation;
import com.github.alantr7.torus.model.animation.AnimationProvider;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DisplayEntitiesRendererConfigLoader extends RendererConfigLoader {

    private static final Pattern ITEM_PATTERN = Pattern.compile("[a-zA-Z0-9_.\\-]+(\\[[a-zA-Z0-9_=,:\"]+])?");

    private static final Pattern ATTRIBUTE_PATTERN = Pattern.compile("[a-z]+=[a-zA-Z0-9_:]+");

    public DisplayEntitiesRendererConfigLoader() {
        super("display_entities");
    }

    @Override
    public @Nullable PartModelTemplate load(ConfigurationSection section, String name, Vector3f offset, Map<String, String> variables) {
        Map<String, AnimationProvider<PartModel, Animation>> animationMap;
        ConfigurationSection animationMapSection = section.getConfigurationSection("animation_map");
        if (animationMapSection != null) {
            animationMap = new HashMap<>();
            for (String modelAnimation : animationMapSection.getKeys(false)) {
                String animationId = animationMapSection.getString(modelAnimation);
                if (animationId == null) {
                    continue;
                }

                animationMap.put(modelAnimation, part -> ((DisplayEntitiesPartModel) part).predefinedAnimations.get(animationId));
            }
        } else {
            animationMap = Collections.emptyMap();
        }

        DisplayEntitiesPartModelTemplate partModelTemplate = new DisplayEntitiesPartModelTemplate(name, offset, section.getInt("teleport_duration", 0), animationMap);

        List<String> elements = section.getStringList("elements");
        if (!variables.isEmpty()) {
            elements.replaceAll(str -> {
                for (Map.Entry<String, String> entry : variables.entrySet()) {
                    str = str.replace("#" + entry.getKey(), entry.getValue());
                }

                return str;
            });
        }

        for (String rawElement : elements) {
            Matcher matcher = ITEM_PATTERN.matcher(rawElement);
            String rendererType = nextString(rawElement, matcher);

            if (rendererType == null) {
                TorusLogger.error(Category.MODELS, "Renderer type is null.");
                continue;
            }

            PartModelElementDisplayRenderer renderer = switch (rendererType) {
                case "item_display" -> parseElementWithItemDisplayRenderer(matcher, rawElement);
                case "block_display" -> parseElementWithBlockDisplayRenderer(matcher, rawElement);
                default -> null;
            };

            if (renderer == null) {
                TorusLogger.error(Category.MODELS, "Invalid renderer type: " + rendererType);
            } else {
                partModelTemplate.parts.add(renderer);
            }
        }

        return partModelTemplate;
    }

    public static PartModelElementItemDisplayRenderer parseElementWithItemDisplayRenderer(Matcher matcher, String raw) {
        String rawMaterial = nextString(raw, matcher);
        Material material;

        Map<String, String> itemAttributes = new HashMap<>();
        int attributesPosition = rawMaterial.indexOf("[");
        if (attributesPosition != -1) {
            material = Material.valueOf(rawMaterial.substring(0, attributesPosition).toUpperCase());

            Matcher attributeMatcher = ATTRIBUTE_PATTERN.matcher(rawMaterial.substring(attributesPosition + 1, rawMaterial.length() - 1));
            while (attributeMatcher.find()) {
                String attributeNameValue = rawMaterial.substring(attributesPosition + 1 + attributeMatcher.start(), attributesPosition + 1 + attributeMatcher.end());
                int attributeValueSeparatorPosition = attributeNameValue.indexOf("=");

                itemAttributes.put(attributeNameValue.substring(0, attributeValueSeparatorPosition), attributeNameValue.substring(attributeValueSeparatorPosition + 1));
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

        ItemStack itemStack = (material == Material.PLAYER_HEAD && itemAttributes.containsKey("texture"))
          ? HeadData.create("http://textures.minecraft.net/texture/" + itemAttributes.get("texture"))
          : new ItemStack(material);

        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemAttributes.containsKey("model")) {
            CustomModelDataComponent customModelDataComponent = itemMeta.getCustomModelDataComponent();
            customModelDataComponent.setStrings(Collections.singletonList(itemAttributes.get("model")));
            itemMeta.setCustomModelDataComponent(customModelDataComponent);
        }

        itemStack.setItemMeta(itemMeta);
        return new PartModelElementItemDisplayRenderer(itemStack, offsetScaleRotation);
    }

    public static PartModelElementBlockDisplayRenderer parseElementWithBlockDisplayRenderer(Matcher matcher, String raw) {
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

        return new PartModelElementBlockDisplayRenderer(material.createBlockData(), offsetScaleRotation);
    }

    public static String nextString(String string, Matcher matcher) {
        if (matcher.find())
            return string.substring(matcher.start(), matcher.end());

        return null;
    }

}
