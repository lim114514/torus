package com.github.alantr7.torus.item;

import com.github.alantr7.torus.TorusPlugin;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ItemReference that = (ItemReference) o;
        return Objects.equals(providerId, that.providerId) && Objects.equals(itemId, that.itemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(providerId, itemId);
    }

    public static ItemReference parse(String item) {
        int pos = item.indexOf(":");
        return pos == -1 ? new ItemReference("minecraft", item) : new ItemReference(item.substring(0, pos), item.substring(pos + 1));
    }

    public static ItemReference create(ItemStack stack) {
        return TorusPlugin.getInstance().getItemManager().createItemReference(stack);
    }

    public static boolean compare(ItemStack stack1, ItemStack stack2) {
        return create(stack1).equals(create(stack2));
    }

}
