package com.github.alantr7.torus.model;

import com.github.alantr7.torus.TorusPlugin;
import io.papermc.paper.entity.TeleportFlag;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class PartModel {

    public abstract void teleport(Location location);

    public abstract void remove();

}