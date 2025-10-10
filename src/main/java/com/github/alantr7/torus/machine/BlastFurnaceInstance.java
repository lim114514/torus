package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.gui.BlastFurnaceGUI;
import com.github.alantr7.torus.item.ItemReference;
import com.github.alantr7.torus.math.Direction;
import com.github.alantr7.torus.recipe.BlastFurnaceRecipe;
import com.github.alantr7.torus.structure.Inspectable;
import com.github.alantr7.torus.structure.LoadContext;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.Structures;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.component.Connector;
import com.github.alantr7.torus.structure.inventory.BukkitStructureInventory;
import com.github.alantr7.torus.world.BlockLocation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.LinkedList;
import java.util.List;

public class BlastFurnaceInstance extends StructureInstance implements Inspectable {

    protected Connector inputConnector;

    protected Connector itemOutputConnector, slugOutputConnector;

    public Inventory inventory = Bukkit.createInventory(null, 27, "Blast Furnace");

    public boolean recipeLookup;

    public BlastFurnaceRecipe recipe;

    public boolean isActive;

    public boolean autoSmelt;

    private int processedTicks;

    BlastFurnaceInstance(LoadContext context) {
        super(context);
    }

    public BlastFurnaceInstance(BlockLocation location, StructureBodyDef bodyDef, Direction direction) {
        super(Structures.BLAST_FURNACE, location, bodyDef, direction);
    }

    @Override
    public void tick() {
        itemOutputConnector.attemptDirectItemExport();

        if (recipe != null) {
            if (isActive && processedTicks++ >= recipe.smeltDuration) {
                ItemStack resultSlot = inventory.getItem(16);
                if (resultSlot != null && ItemReference.compare(resultSlot, recipe.result)) {
                    resultSlot.setAmount(resultSlot.getAmount() + recipe.result.getAmount());
                } else {
                    inventory.setItem(16, recipe.result.clone());
                }
                this.recipe = null;
                this.processedTicks = 0;
                this.isActive = false;
                this.recipeLookup = true;
            } else {
                updateRecipeDisplay();
            }
        }

        if (recipeLookup) {
            this.recipe = null;
            for (BlastFurnaceRecipe recipe : TorusPlugin.getInstance().getRecipeManager().getBlastFurnaceRecipes()) {
                if (canUseRecipe(recipe)) {
                    this.recipe = recipe;
                    break;
                }
            }
            updateRecipeDisplay();
            recipeLookup = false;
        }
    }

    private void updateRecipeDisplay() {
        if (recipe != null) {
            ItemStack recipeItem = recipe.result.clone();
            ItemMeta meta = recipeItem.getItemMeta();
            List<String> lore = new LinkedList<>();

            if (isActive) {
                meta.setDisplayName(ChatColor.YELLOW + "⏳ Smelting...");

                float ratio = (float) processedTicks / recipe.smeltDuration;
                int progress = (int) Math.floor(ratio * 10);
                lore.addAll(List.of(
                  ChatColor.GRAY + "" + "■".repeat(progress) + "□".repeat(10 - progress) + " " + String.format("%.0f%%", ratio * 100),
                  ChatColor.GRAY + "" + (recipe.smeltDuration - processedTicks) + "s remaining",
                  ""
                ));
            } else {
                lore.add(ChatColor.GRAY + "Duration: " + recipe.smeltDuration + "s");
                lore.add("");
                lore.add(ChatColor.GOLD + "\uD83D\uDD25 Click to smelt");
            }
            lore.add((autoSmelt ? (ChatColor.GREEN + "✔") : (ChatColor.RED + "✘")) + " Auto smelting");

            meta.setLore(lore);

            recipeItem.setItemMeta(meta);
            inventory.setItem(14, recipeItem);
        } else {
            ItemStack recipeNotFound = new ItemStack(Material.BARRIER);
            ItemMeta meta = recipeNotFound.getItemMeta();
            meta.setDisplayName(ChatColor.RED + "✘ Recipe not found");
            recipeNotFound.setItemMeta(meta);
            inventory.setItem(14, recipeNotFound);
        }
    }

    public void smelt() {
        if (isActive || !canUseRecipe(recipe))
            return;

        // Consume items and produce result
        outer: for (ItemReference ingredient : recipe.ingredients) {
            int consumedAmount = 0;
            for (int i = 10; i < 13; i++) {
                ItemStack stack = inventory.getContents()[i];
                if (stack == null)
                    continue;

                if (ItemReference.create(stack).itemId.equals(ingredient.itemId)) {
                    int taken = Math.min(stack.getAmount(), ingredient.getItem().getAmount() - consumedAmount);
                    consumedAmount += taken;

                    stack.setAmount(stack.getAmount() - taken);
                    if (stack.getAmount() == 0) {
                        inventory.setItem(i, null);
                    }

                    if (consumedAmount == ingredient.getItem().getAmount())
                        continue outer;
                }
            }
        }

        isActive = true;
        updateRecipeDisplay();
    }

    public boolean canUseRecipe(BlastFurnaceRecipe recipe) {
        if (recipe == null)
            return false;

        outer: for (ItemReference ingredient : recipe.ingredients) {
            for (int i = 10; i < 13; i++) {
                ItemStack stack = inventory.getContents()[i];
                if (stack == null)
                    continue;

                if (ItemReference.create(stack).itemId.equals(ingredient.itemId)) {
                    continue outer;
                }
            }
            return false;
        }
        return true;
    }

    @Override
    public void handlePlayerInteraction(PlayerInteractEvent event, BlockLocation location) {
        new BlastFurnaceGUI(this, event.getPlayer()).open();
    }

    @Override
    protected void setup() {
        inputConnector = getConnector("in_item");
        itemOutputConnector = getConnector("out_item");
        slugOutputConnector = getConnector("out_slug");

        itemOutputConnector.linkedInventory = new BukkitStructureInventory(inventory);
        itemOutputConnector.linkedInventoryAllowedSlots = new int[]{16};
        updateRecipeDisplay();
    }

    @Override
    public String getInspectionText(BlockLocation location, Player player) {
        return "Blast Furnace [" + (recipe != null ? recipe.id : "No recipe") + "] [" + String.format("%.0f %%", 100f * (float) processedTicks / (recipe != null ? recipe.smeltDuration : 1)) + "]";
    }

}
