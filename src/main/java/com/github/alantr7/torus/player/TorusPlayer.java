package com.github.alantr7.torus.player;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.machine.WireConnectorInstance;
import com.github.alantr7.torus.structure.StructureInstance;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TorusPlayer {

    public final UUID id;

    public final String name;

    public long placementCooldownExpiry;

    public long interactionCooldownExpiry;

    public TextDisplay activeInspectionHologram;

    public WireConnectorInstance pendingWireConnection;

    public TorusPlayer(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public boolean isOnline() {
        return Bukkit.getPlayer(id) != null;
    }

    public Player asBukkit() {
        return Bukkit.getPlayer(id);
    }

    public void abortWireConnection() {
        if (pendingWireConnection != null) {
            pendingWireConnection.connectionCandidate.setLeashHolder(null);
            pendingWireConnection = null;
        }
    }

    public void showInspectionHologram(StructureInstance structure) {
        if (activeInspectionHologram == structure.inspectionHologram)
            return;

        if (activeInspectionHologram != null) {
            asBukkit().hideEntity(TorusPlugin.getInstance(), activeInspectionHologram);
        }
        asBukkit().showEntity(TorusPlugin.getInstance(), structure.inspectionHologram);
        activeInspectionHologram = structure.inspectionHologram;
    }

    public void hideInspectionHologram() {
        if (activeInspectionHologram != null) {
            asBukkit().hideEntity(TorusPlugin.getInstance(), activeInspectionHologram);
        }
        activeInspectionHologram = null;
    }

    @NotNull
    public static TorusPlayer get(Player player) {
        return TorusPlugin.getInstance().getPlayerManager().asTorusPlayer(player);
    }

}
