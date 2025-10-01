package com.github.alantr7.torus.structure;

import com.github.alantr7.torus.world.BlockLocation;
import org.bukkit.entity.Player;

public interface Inspectable {

    String getInspectionText(BlockLocation location, Player player);

}
