package com.github.alantr7.torus.gui.browser;

import com.github.alantr7.bukkitplugin.gui.ClickType;
import com.github.alantr7.bukkitplugin.gui.CloseInitiator;
import com.github.alantr7.bukkitplugin.gui.GUI;
import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.item.Category;
import com.github.alantr7.torus.item.TorusItem;
import com.github.alantr7.torus.plugin.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemBrowserCategoryGUI extends GUI {

    private final Category category;

    public ItemBrowserCategoryGUI(Category category, Player player) {
        super(TorusPlugin.getInstance(), player, false);
        this.category = category;

        init();
    }

    @Override
    protected void init() {
        createInventory("Item Browser / " + category.name, 54);
        setInteractionEnabled(false);
    }

    @Override
    protected void fill(Inventory inventory) {
        int slot = 0;
        for (TorusItem item : category.items) {
            ItemStack stack = item.toItemStack().clone();
            if (getPlayer().hasPermission(Permissions.BROWSE_GUI_GET_ITEM)) {
                ItemMeta meta = stack.getItemMeta();
                meta.setLore(List.of(ChatColor.GRAY + "Left click to get"));

                registerInteractionCallback(slot, ClickType.LEFT, () -> {
                    getPlayer().getInventory().addItem(item.toItemStack().clone());
                });

                stack.setItemMeta(meta);
            }
            setItem(slot++, stack);
        }
    }

    @Override
    protected void onInventoryOpen() {

    }

    @Override
    protected void onInventoryClose(CloseInitiator closeInitiator) {
        new ItemBrowserMainGUI(getPlayer()).open();;
    }

    @Override
    public void onItemInteract(int i, @NotNull ClickType clickType, @Nullable ItemStack itemStack) {

    }
}
