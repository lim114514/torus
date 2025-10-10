package com.github.alantr7.torus.gui;

import com.github.alantr7.bukkitplugin.gui.ClickType;
import com.github.alantr7.bukkitplugin.gui.CloseInitiator;
import com.github.alantr7.bukkitplugin.gui.GUI;
import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.machine.BlastFurnaceInstance;
import com.github.alantr7.torus.recipe.BlastFurnaceRecipe;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlastFurnaceGUI extends GUI {

    public final BlastFurnaceInstance furnace;

    public BlastFurnaceGUI(BlastFurnaceInstance instance, Player player) {
        super(TorusPlugin.getInstance(), player, false);
        this.furnace = instance;

        init();
    }

    @Override
    protected void init() {
        createInventory(furnace.inventory);
    }

    @Override
    protected void fill(Inventory inventory) {
        ItemStack fillerItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = fillerItem.getItemMeta();
        fillerMeta.setDisplayName(ChatColor.BLACK + "");
        fillerItem.setItemMeta(fillerMeta);

        for (int i = 0; i < 9; i++) {
            setItem(i, fillerItem.clone());
            setItem(i + 18, fillerItem.clone());
        }
        setItem(9, fillerItem.clone());
        setItem(13, fillerItem.clone());
        setItem(15, fillerItem.clone());
        setItem(17, fillerItem.clone());

        registerInteractionCallback(14, ClickType.LEFT, furnace::smelt);
        registerInteractionCallback(14, ClickType.RIGHT, () -> furnace.autoSmelt = !furnace.autoSmelt);
    }

    @Override
    protected void onInventoryOpen() {

    }

    @Override
    protected void onInventoryClose(CloseInitiator closeInitiator) {

    }

    @Override
    public void onItemInteract(int i, @NotNull ClickType clickType, @Nullable ItemStack itemStack) {
        if (i < 10 || (i > 12 && i < 27))
            cancel();

        if (i == 16 && getItem(i) != null) {
            ItemStack item = getItem(i);
            getPlayer().getInventory().addItem(item.clone());

            item.setAmount(0);
        }

        if (!furnace.isActive)
            furnace.recipeLookup = true;
    }

}
