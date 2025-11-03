package com.github.alantr7.torus.model;

import io.papermc.paper.entity.TeleportFlag;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.List;

public class PartModel {

    public final PartModelTemplate template;

    public final Display parent;

    public final List<EntityReference> entityReferences;

    public PartModel(PartModelTemplate template, Display parent, List<EntityReference> entityReferences) {
        this.template = template;
        this.parent = parent;
        this.entityReferences = entityReferences;
    }

    public void teleport(Location location) {
        parent.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN, TeleportFlag.EntityState.RETAIN_PASSENGERS);
    }

    public void remove() {
        parent.remove();
        entityReferences.forEach(ref -> {
            if (ref.getEntity() != null)
                ref.entity.remove();
        });
    }

}