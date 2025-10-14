package com.github.alantr7.torus.item;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.structure.Structure;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class TorusItem {

    public final String namespacedId;

    public final Category category;

    protected final ItemStack itemStack;

    @Getter
    protected Structure structure;

    public TorusItem(String namespacedId, Category category, Structure structure, Material material, String name, List<String> lore) {
        this.namespacedId = namespacedId;
        this.category = category;
        this.structure = structure;
        this.itemStack = new ItemStack(material);

        ItemMeta meta = itemStack.getItemMeta();
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
        itemStack.setItemMeta(meta);
    }

    public void setDisplayName(String name) {
        updateMeta(meta -> meta.setDisplayName(name));
    }

    public void setLore(List<String> lore) {
        updateMeta(meta -> meta.setLore(lore));
    }

    public void setCustomModelData(Integer cmd) {
        updateMeta(meta -> meta.setCustomModelData(cmd));
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

    private void updateMeta(Consumer<ItemMeta> consumer) {
        ItemMeta meta = itemStack.getItemMeta();
        consumer.accept(meta);

        itemStack.setItemMeta(meta);
    }

    public ItemStack toItemStack() {
        return itemStack;
    }

    public static TorusItem getById(String id) {
        return TorusPlugin.getInstance().getItemManager().getItemById(id);
    }

    public static TorusItem getByItemStack(ItemStack stack) {
        return TorusPlugin.getInstance().getItemManager().getItemByItemStack(stack);
    }

}
