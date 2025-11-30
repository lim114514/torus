package com.github.alantr7.torus.model;

import com.github.alantr7.torus.TorusPlugin;
import io.papermc.paper.entity.TeleportFlag;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
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
        if (TorusPlugin.usesPaperAPI()) {
            parent.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN, TeleportFlag.EntityState.RETAIN_PASSENGERS);
        } else {
            List<Entity> passengers = new ArrayList<>(parent.getPassengers());
            parent.eject();
            parent.teleport(location);
            for (Entity en : passengers) {
                parent.addPassenger(en);
            }
        }
    }

    public void remove() {
        parent.remove();
        entityReferences.forEach(ref -> {
            if (ref.getEntity() != null)
                ref.entity.remove();
        });
    }

}