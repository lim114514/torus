package com.github.alantr7.torus.model.de_provider;

import org.bukkit.Bukkit;
import org.bukkit.entity.Display;

import java.util.UUID;

public class EntityReference {

    public final UUID id;

    public Display entity;

    public EntityReference(UUID id) {
        this.id = id;
        this.entity = (Display) Bukkit.getEntity(id);
    }

    public EntityReference(Display entity) {
        this.id = entity.getUniqueId();
        this.entity = entity;
    }

    public Display getEntity() {
        return entity != null ? entity : (entity = (Display) Bukkit.getEntity(id));
    }

}
