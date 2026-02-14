package com.github.alantr7.torus.gui.structure;

import com.github.alantr7.bukkitplugin.gui.ClickType;
import com.github.alantr7.bukkitplugin.gui.CloseInitiator;
import com.github.alantr7.bukkitplugin.gui.GUI;
import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.item.ItemReference;
import com.github.alantr7.torus.machine.ConnectorInstance;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.github.alantr7.torus.lang.Localization.translate;

public class ConnectorFilterEditGUI extends GUI {

    private final ConnectorInstance iii;

    public ConnectorFilterEditGUI(Player player, ConnectorInstance iii) {
        super(TorusPlugin.getInstance(), player, false);
        this.iii = iii;

        init();
    }

    @Override
    protected void init() {
        createInventory(translate("gui.connector_filter.title"), 9);
        setInteractionEnabled(false);
    }

    @Override
    protected void fill(Inventory inventory) {
        int idx = 0;
        for (ItemReference ref : iii.getFilter()) {
            if (ref == null) continue;
            setItem(idx++, ref.getItem());
        }

        for (; idx < 9; idx++) {
            ItemStack emptySlot = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            ItemMeta meta = emptySlot.getItemMeta();
            meta.setDisplayName(translate("gui.connector_filter.empty_slot.name").replace("{slot}", String.valueOf((idx + 1))));
            emptySlot.setItemMeta(meta);
            setItem(idx, emptySlot);
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
        if (clickType != ClickType.LEFT)
            return;

        if (i < 9 && itemStack != null) {
            ItemReference[] filter = iii.getFilter();
            filter[i] = null;
            iii.setFilter(filter);
            refill();

            return;
        }

        if (itemStack != null) {
            ItemReference[] filter = iii.getFilter();
            ItemReference newItem = ItemReference.create(itemStack);
            for (int j = 0; j < filter.length; j++) {
                ItemReference ref = filter[j];
                if (ref != null) {
                    if (ref.equals(newItem))
                        return;

                    continue;
                }

                filter[j] = TorusPlugin.getInstance().getItemRegistry().createItemReference(itemStack);
                break;
            }
            iii.setFilter(filter);
            refill();
        }
    }

}
