package com.github.alantr7.torus.gui.browser;

import com.github.alantr7.bukkitplugin.gui.ClickType;
import com.github.alantr7.bukkitplugin.gui.CloseInitiator;
import com.github.alantr7.bukkitplugin.gui.GUI;
import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.item.Category;
import com.github.alantr7.torus.item.TorusItem;
import com.github.alantr7.torus.plugin.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.Keyed;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

import static com.github.alantr7.torus.lang.Localization.translate;

public class ItemBrowserCategoryGUI extends GUI {

    private final Category category;

    public ItemBrowserCategoryGUI(Category category, Player player) {
        super(TorusPlugin.getInstance(), player, false);
        this.category = category;

        init();
    }

    @Override
    protected void init() {
        createInventory(translate("gui.browse.category.title").replace("{category}", category.name), 54);
        setInteractionEnabled(false);

        registerEventCallback(Action.CLOSE, () -> new ItemBrowserMainGUI(getPlayer()).open());
    }

    @Override
    protected void fill(Inventory inventory) {
        int slot = 0;
        for (TorusItem item : category.items) {
            ItemStack stack = item.toItemStack();
            if (getPlayer().hasPermission(Permissions.BROWSE_GUI_GET_ITEM)) {
                ItemMeta meta = stack.getItemMeta();
                List<String> lore = new LinkedList<>();
                lore.add(translate("gui.browse.category.item.click_obtain"));
                if (item.hasRecipes()) {
                    lore.add(translate("gui.browse.category.item.click_view_recipes"));
                }

                meta.setLore(lore);

                registerInteractionCallback(slot, ClickType.LEFT, () -> {
                    getPlayer().getInventory().addItem(item.toItemStack());
                });

                registerInteractionCallback(slot, ClickType.RIGHT, () -> {
                    Keyed recipe = item.getRecipes().iterator().next();
                    GUI viewer = TorusPlugin.getInstance().getRecipeRegistry().createRecipeViewer(getPlayer(), recipe);

                    if (viewer == null) {
                        getPlayer().sendMessage(translate("gui.browse.category.item.no_recipe_preview"));
                        return;
                    }

                    clearEventCallbacks(Action.CLOSE);

                    Player player = getPlayer();
                    viewer.registerEventCallback(Action.CLOSE, () -> new ItemBrowserCategoryGUI(category, player).open());
                    viewer.open();
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
    }

    @Override
    public void onItemInteract(int i, @NotNull ClickType clickType, @Nullable ItemStack itemStack) {

    }
}
