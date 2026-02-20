package com.github.alantr7.torus.gui.structure;

import com.github.alantr7.bukkitplugin.gui.ClickType;
import com.github.alantr7.bukkitplugin.gui.CloseInitiator;
import com.github.alantr7.bukkitplugin.gui.GUI;
import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.machine.TurretInstance;
import com.github.alantr7.torus.utils.MathUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.github.alantr7.torus.lang.Localization.translate;

public class TurretGUI extends GUI {

    protected final TurretInstance turret;

    public TurretGUI(Player player, TurretInstance turret) {
        super(TorusPlugin.getInstance(), player, false);
        this.turret = turret;
        init();
    }

    @Override
    protected void init() {
        createInventory(translate("gui.turret_settings.title"), 9);
        setInteractionEnabled(false);
    }

    @Override
    protected void fill(Inventory inventory) {
        registerButton(0, "gui.turret_settings.targets.players", TurretInstance.TARGET_PLAYERS, Material.CRAFTING_TABLE);
        registerButton(1, "gui.turret_settings.targets.monsters", TurretInstance.TARGET_MONSTERS, Material.BONE);
        registerButton(2, "gui.turret_settings.targets.animals", TurretInstance.TARGET_ANIMALS, Material.LEATHER);
    }

    private void registerButton(int slot, String translateKey, byte targetMask, Material material) {
        ItemStack target = new ItemStack(material);
        target.editMeta(meta -> {
            meta.setDisplayName(translate(translateKey).replace("{status}", translate("gui.turret_settings.targets.status." + MathUtils.hasFlag(turret.getTargets(), targetMask))));
        });

        if (MathUtils.hasFlag(turret.getTargets(), targetMask)) {
            target.editMeta(meta -> {
                meta.addEnchant(Enchantment.SILK_TOUCH,  1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            });
        }
        setItem(slot, target);
        registerInteractionCallback(slot, ClickType.LEFT, () -> {
            turret.setTargets((byte) MathUtils.setFlag(turret.getTargets(), targetMask, !MathUtils.hasFlag(turret.getTargets(), targetMask)));
            refill();
        });
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
