package com.github.alantr7.torus.item;

import com.github.alantr7.torus.TorusPlugin;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class ItemCriteria {

    public final Set<Material> materials = new HashSet<>();

    public final Set<String> ids = new HashSet<>();

    public boolean matches(ItemStack item) {
        if (materials.contains(item.getType()))
            return true;

        TorusItem torusItem = TorusPlugin.getInstance().getItemRegistry().getItemByItemStack(item);
        return torusItem != null && ids.contains(torusItem.namespacedId);
    }

}
