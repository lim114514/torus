package com.github.alantr7.torus.item;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.URL;
import java.util.UUID;

public class HeadData {

    public final ItemStack stack;

    public HeadData(String textureUrl) {
        stack = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) stack.getItemMeta();

        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
        try {
            PlayerTextures textures = profile.getTextures();
            textures.setSkin(new URL(textureUrl));

            profile.setTextures(textures);
        } catch (Exception e) {
            e.printStackTrace();
        }

        meta.setOwnerProfile(profile);
        stack.setItemMeta(meta);
    }

}
