package com.github.alantr7.torus.item;

import com.github.alantr7.torus.TorusPlugin;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ItemReference {

    public final String providerId;

    public final String itemId;

    public ItemReference(String providerId, String itemId) {
        this.providerId = providerId;
        this.itemId = itemId.toUpperCase();
    }

    @Nullable
    public ItemStack getItem() {
        return TorusPlugin.getInstance().getItemManager().getItemStackByReference(this);
    }

}
