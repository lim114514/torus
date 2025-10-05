package com.github.alantr7.torus.gui;

import com.github.alantr7.bukkitplugin.BukkitPlugin;
import com.github.alantr7.bukkitplugin.gui.ClickType;
import com.github.alantr7.bukkitplugin.gui.CloseInitiator;
import com.github.alantr7.bukkitplugin.gui.GUI;
import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.item.ItemReference;
import com.github.alantr7.torus.machine.InventoryInterfaceInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InventoryInterfaceFilterEditGUI extends GUI {

    private final InventoryInterfaceInstance iii;

    public InventoryInterfaceFilterEditGUI(Player player, InventoryInterfaceInstance iii) {
        super(TorusPlugin.getInstance(), player, false);
        this.iii = iii;

        init();
    }

    @Override
    protected void init() {
        createInventory("Inventory Interface | Filter", 9);
        setInteractionEnabled(false);
    }

    @Override
    protected void fill(Inventory inventory) {
        int idx = 0;
        for (ItemReference ref : iii.getFilter()) {
            if (ref == null) continue;
            setItem(idx++, ref.getItem());
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
            for (int j = 0; j < filter.length; j++) {
                ItemReference ref = filter[j];
                if (ref != null) continue;

                filter[j] = TorusPlugin.getInstance().getItemManager().createItemReference(itemStack);
                break;
            }
            iii.setFilter(filter);
            refill();
        }
    }

}
