package com.github.alantr7.torus.model;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.joml.Vector3f;

import java.util.List;

public class PartModel {

    public final PartModelTemplate template;

    public final Entity parent;

    public final List<EntityReference> entityReferences;

    public PartModel(PartModelTemplate template, Entity parent, List<EntityReference> entityReferences) {
        this.template = template;
        this.parent = parent;
        this.entityReferences = entityReferences;
    }

    public void teleport(Location location) {
        Vector3f min = new Vector3f(3_000_000, 3_000_000, 3_000_000);
        Vector3f max = new Vector3f(-3_000_000, -3_000_000, -3_000_000);

        for (EntityReference ref : entityReferences) {
            if (ref.getEntity() == null)
                continue;

            Location loc = ref.entity.getLocation();
            min.x = (float) Math.min(min.x, loc.getX());
            min.y = (float) Math.min(min.y, loc.getY());
            min.z = (float) Math.min(min.z, loc.getZ());

            max.x = (float) Math.max(max.x, loc.getX());
            max.y = (float) Math.max(max.y, loc.getY());
            max.z = (float) Math.max(max.z, loc.getZ());
        }

        Vector3f center = new Vector3f(min.x + (max.x - min.x) / 2f, min.y + (max.y - min.y) / 2f, min.z + (max.z - min.z) / 2f);

        // Teleport based on relative coordinates
        for (EntityReference ref : entityReferences) {
            if (ref.getEntity() == null)
                continue;

            Location loc = ref.entity.getLocation();
            double offsetX = (loc.getX() - center.x);
            double offsetY = (loc.getY() - center.y);
            double offsetZ = (loc.getZ() - center.z);

            Location newLocation = location.clone().add(offsetX, offsetY, offsetZ);
            newLocation.setYaw(loc.getYaw());
            newLocation.setPitch(loc.getPitch());
            ref.entity.teleport(newLocation);
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