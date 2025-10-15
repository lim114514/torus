package com.github.alantr7.torus.plugin;

import com.github.alantr7.bukkitplugin.annotations.core.Singleton;
import com.github.alantr7.bukkitplugin.commands.annotations.CommandHandler;
import com.github.alantr7.bukkitplugin.commands.executor.ExecutorType;
import com.github.alantr7.bukkitplugin.commands.factory.CommandBuilder;
import com.github.alantr7.bukkitplugin.commands.registry.Command;
import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.gui.browser.ItemBrowserMainGUI;
import com.github.alantr7.torus.item.TorusItem;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.component.Connector;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.TorusChunk;
import com.github.alantr7.torus.world.TorusWorld;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

@Singleton
public class Commands {

    @com.github.alantr7.bukkitplugin.annotations.generative.Command(name = "torus", description = "")
    public static final String TORUS = "torus";

    @CommandHandler Command give = CommandBuilder.using("torus")
      .parameter("give")
      .parameter("{item}", p -> p.tabComplete((args) -> {
          return TorusPlugin.getInstance().getItemManager().getItemIds();
      }))
      .executes(ctx -> {
          TorusItem item = TorusPlugin.getInstance().getItemManager().getItemById((String) ctx.getArgument("item"));
          if (item == null) {
              ctx.respond("Unknown item.");
              return;
          }

          ((Player) ctx.getExecutor()).getInventory().addItem(item.toItemStack());
          ctx.respond("You received 1 x " + item.namespacedId);
      });

    @CommandHandler Command browse = CommandBuilder.using("torus")
      .parameter("browse")
      .forExecutors(ExecutorType.PLAYER)
      .executes(ctx -> {
          new ItemBrowserMainGUI((Player) ctx.getExecutor()).open();
      });

    @CommandHandler Command inspectStructure = CommandBuilder.using("torus")
      .parameter("debug")
      .parameter("inspect_structure")
      .executes(ctx -> {
          Player player = (Player) ctx.getExecutor();
          Block block = player.getTargetBlockExact(5);
          if (block == null) {
              ctx.respond("Not looking at any structure.");
              return;
          }

          TorusWorld world = TorusPlugin.getInstance().getWorldManager().getWorld(player.getWorld());
          if (world == null) {
              ctx.respond("Structures aren't allowed in this world.");
              return;
          }

          StructureInstance structure = world.getStructure(new BlockLocation(block.getLocation()));
          if (structure == null) {
              ctx.respond("Not looking at any structure.");
              return;
          }

          ctx.respond("\n");
          ctx.respond(ChatColor.GOLD + "Structure ID: " + ChatColor.RESET + structure.structure.id);
          ctx.respond(ChatColor.GOLD + "Connectors: (" + structure.getConnectors().size() + ")");
          for (Connector connector : structure.getConnectors()) {
              ctx.respond("  - " + ChatColor.YELLOW + connector.getComponent().absoluteLocation + ":");
              ctx.respond("    - Matter: " + ChatColor.GRAY + connector.matter);
              ctx.respond("    - Flow: " + ChatColor.GRAY + connector.getFlowDirection());
              ctx.respond("    - Connections: " + ChatColor.GRAY + connector.getConnections());
          }
      });

    @CommandHandler Command inspectChunk = CommandBuilder.using("torus")
      .parameter("debug")
      .parameter("inspect_chunk")
      .executes(ctx -> {
          Player player = (Player) ctx.getExecutor();
          TorusChunk chunk = TorusPlugin.getInstance().getWorldManager().getWorld(player.getWorld()).getChunk(
            new BlockLocation(player.getLocation())
          );

          if (chunk == null) {
              ctx.respond("This chunk does not have any structures.");
              return;
          }

          ctx.respond("Chunk position: " + chunk.position);
          ctx.respond("Chunk size: " + chunk.getSize());
          ctx.respond("Structures: " + chunk.getStructures().size());
          ctx.respond("Occupations: " + chunk.getOccupations().size());
      });

}
