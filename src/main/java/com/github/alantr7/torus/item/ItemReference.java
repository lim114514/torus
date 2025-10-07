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

    public static ItemReference parse(String item) {
        int pos = item.indexOf(":");
        return pos == -1 ? new ItemReference("minecraft", item) : new ItemReference(item.substring(0, pos), item.substring(pos + 1));
    }

}
