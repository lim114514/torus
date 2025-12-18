package com.github.alantr7.torus.gui.browser;

import com.github.alantr7.bukkitplugin.gui.ClickType;
import com.github.alantr7.bukkitplugin.gui.CloseInitiator;
import com.github.alantr7.bukkitplugin.gui.GUI;
import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.item.Category;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemBrowserMainGUI extends GUI {

    public ItemBrowserMainGUI(Player player) {
        super(TorusPlugin.getInstance(), player);
    }

    @Override
    protected void init() {
        createInventory("Item Browser", 54);
        setInteractionEnabled(false);
    }

    @Override
    protected void fill(Inventory inventory) {
        int slot = 0;
        for (Category category : TorusPlugin.getInstance().getItemRegistry().getCategories()) {
            ItemStack display = category.display.clone();
            ItemMeta meta = display.getItemMeta();
            meta.setDisplayName(ChatColor.WHITE + category.name);
            display.setItemMeta(meta);

            setItem(slot, display);
            registerInteractionCallback(slot, ClickType.LEFT, () -> new ItemBrowserCategoryGUI(category, getPlayer()).open());

            slot++;
        }
    }

    @Override
    protected void onInventoryOpen() {

    }

    @Override
    protected void onInventoryClose(CloseInitiator closeInitiator) {

    }

    @Override
    public void onItemInteract(int i, @NotNull ClickType clickType, @Nullable ItemStack itemStack) {

    }
}
