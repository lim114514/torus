package com.github.alantr7.torus.gui.recipeview;

import com.github.alantr7.bukkitplugin.BukkitPlugin;
import com.github.alantr7.bukkitplugin.gui.ClickType;
import com.github.alantr7.bukkitplugin.gui.CloseInitiator;
import com.github.alantr7.bukkitplugin.gui.GUI;
import com.github.alantr7.torus.TorusPlugin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ViewCraftingRecipeGUI extends GUI {

    private final ShapedRecipe recipe;

    public ViewCraftingRecipeGUI(ShapedRecipe recipe, Player player) {
        super(TorusPlugin.getInstance(), player, false);
        this.recipe = recipe;

        init();
    }

    @Override
    protected void init() {
        createInventory("Recipe Viewer", 45);
        setInteractionEnabled(false);
    }

    @Override
    protected void fill(Inventory inventory) {
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setMaxStackSize(1);
        filler.setItemMeta(fillerMeta);

        for (int i = 0; i < 9; i++) {
            setItem(i, filler.clone());
            setItem(i + 36, filler.clone());
        }

        for (int i = 9; i < 36; i += 9) {
            setItem(i, filler.clone());
            setItem(i + 4, filler.clone());
            setItem(i + 5, filler.clone());
            setItem(i + 6, filler.clone());
            setItem(i + 7, filler.clone());
            setItem(i + 8, filler.clone());
        }

        int slot = 10;
        for (String row : recipe.getShape()) {
            for (int i = 0; i < row.length(); i++, slot++) {
                if (row.charAt(i) == ' ')
                    continue;

                ItemStack ingr = recipe.getIngredientMap().get(row.charAt(i));
                if (ingr == null)
                    continue;

                setItem(slot, ingr.clone());
            }
            slot += 9 - row.length();
        }

        setItem(25, recipe.getResult().clone());
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
