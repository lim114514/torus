package com.github.alantr7.torus.item;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.api.addon.TorusAddon;
import com.github.alantr7.torus.structure.Structure;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.function.Consumer;

public class TorusItem {

    public final TorusAddon addon;

    public final String namespacedId;

    @Getter
    protected Category[] categories;

    public final String name;

    protected ItemStack baseItem;

    @Getter
    protected final Set<Keyed> recipes = new HashSet<>();

    @Getter
    protected Structure structure;

    public TorusItem(TorusAddon addon, String id, Category[] categories, Structure structure, HeadData data, String name, List<String> lore) {
        this(addon, id, categories, structure, data.stack.clone(), name, lore);
    }

    public TorusItem(TorusAddon addon, String id, Category[] categories, Structure structure, Material material, String name, List<String> lore) {
        this(addon, id, categories, structure, new ItemStack(material), name, lore);
    }

    public TorusItem(TorusAddon addon, String id, Category[] categories, Structure structure, ItemStack stack, String name, List<String> lore) {
        this.addon = addon;
        this.namespacedId = addon.id + ":" + id;
        this.name = name;
        this.structure = structure;
        this.categories = categories;
        setBaseItem(stack, name, lore);
    }

    public boolean isPlaceable() {
        return structure != null;
    }

    public void setStructure(Structure structure) {
        this.structure = structure;
        updateMeta(meta ->
            meta.getPersistentDataContainer().set(new NamespacedKey(TorusPlugin.getInstance(), "torus_item"), PersistentDataType.STRING, namespacedId)
        );
    }

    public void setCategories(Category[] categories) {
        for (Category category : this.categories) {
            category.items.remove(this);
        }
        this.categories = categories;
        for (Category category : categories) {
            category.items.add(this);
        }
    }

    public boolean hasRecipes() {
        return !recipes.isEmpty();
    }

    private void updateMeta(Consumer<ItemMeta> consumer) {
        ItemMeta meta = baseItem.getItemMeta();
        consumer.accept(meta);

        baseItem.setItemMeta(meta);
    }

    public ItemStack toItemStack() {
        return baseItem;
    }

    public void setBaseItem(ItemStack itemStack, String name, List<String> lore) {
        baseItem = itemStack.clone();
        ItemMeta meta = baseItem.getItemMeta();
        meta.getPersistentDataContainer().set(new NamespacedKey(TorusPlugin.getInstance(), "torus_item"), PersistentDataType.STRING, namespacedId);
        meta.setDisplayName(ChatColor.WHITE + name);

        List<String> lore0 = meta.hasLore() ? meta.getLore() : new LinkedList<>();
        if (structure != null) {
            byte[] size = structure.getSize();
            lore0.add(ChatColor.BLUE + String.format("Structure [%dx%dx%d]", size[0], size[1], size[2]));
            if (!lore.isEmpty())
                lore0.add("");
        }
        lore0.addAll(lore);
        meta.setLore(lore0);

        CustomModelDataComponent component = meta.getCustomModelDataComponent();
        component.setStrings(Collections.singletonList(namespacedId));
        meta.setCustomModelDataComponent(component);
        baseItem.setItemMeta(meta);
    }

    public static TorusItem getById(String id) {
        return TorusPlugin.getInstance().getItemRegistry().getItemById(id);
    }

    public static TorusItem getByItemStack(ItemStack stack) {
        return TorusPlugin.getInstance().getItemRegistry().getItemByItemStack(stack);
    }

    public static boolean is(ItemStack stack, String namespacedId) {
        TorusItem item = getByItemStack(stack);
        return item != null && item.namespacedId.equals(namespacedId);
    }

}
