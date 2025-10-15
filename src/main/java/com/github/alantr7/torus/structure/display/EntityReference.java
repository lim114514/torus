package com.github.alantr7.torus.structure.display;

import org.bukkit.Bukkit;
import org.bukkit.entity.ItemDisplay;

import java.util.UUID;

public class EntityReference {

    public final UUID id;

    public ItemDisplay entity;

    public EntityReference(UUID id) {
        this.id = id;
        this.entity = (ItemDisplay) Bukkit.getEntity(id);
    }

    public EntityReference(ItemDisplay entity) {
        this.id = entity.getUniqueId();
        this.entity = entity;
    }

    public ItemDisplay getEntity() {
        return entity != null ? entity : (entity = (ItemDisplay) Bukkit.getEntity(id));
    }

}
